#include "koola.h"
#include <jni.h>
#include "string"
#include <shadowhook.h>

static jclass nativeHookClass = nullptr;
static jmethodID nativeLogMethod = nullptr;
static jstring apppackagename = nullptr;

void initJniEnv(JNIEnv* env) {
    if (nativeHookClass == nullptr) {
        const char* className = "cn/peyriat/koola/NativeHook";
        jclass localClass = env->FindClass(className);
        if (localClass == nullptr) {
            return;
        }
        nativeHookClass = (jclass) env->NewGlobalRef(localClass);
        env->DeleteLocalRef(localClass);
    }

    if (nativeLogMethod == nullptr) {
        nativeLogMethod = env->GetStaticMethodID(nativeHookClass, "nativeLog", "(Ljava/lang/String;)V");
    }
}

void logToXposed(JNIEnv* env, const char* message) {
    if (nativeHookClass != nullptr && nativeLogMethod != nullptr) {
        jstring jMessage = env->NewStringUTF(message);
        env->CallStaticVoidMethod(nativeHookClass, nativeLogMethod, jMessage);
        env->DeleteLocalRef(jMessage);
    }
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    initJniEnv(env);
    return JNI_VERSION_1_6;
}

static jstring my_nativeGetUserDataPath(JNIEnv* env, jobject thiz) {
    logToXposed(env, "Hook triggered!");
    const char* packageNameCStr = env->GetStringUTFChars(apppackagename, nullptr);
    std::string path = "/storage/emulated/0/Android/data/" + std::string(packageNameCStr) + "/files/koola/games/com.netease";
    env->ReleaseStringUTFChars(apppackagename, packageNameCStr);
    return env->NewStringUTF(path.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_cn_peyriat_koola_NativeHook_hookNativeGetUserDataPath(JNIEnv* env, jobject thiz, jstring packagename) {
    apppackagename = packagename;
    void* original;
    void* hook = shadowhook_hook_sym_name(
            "libminecraftpe.so",
            "Java_com_mojang_minecraftpe_MainActivity_nativeGetUserDataPath",
            (jstring *)my_nativeGetUserDataPath,
            &original
    );
    return (hook != nullptr) ? 0 : -1;
}