#include "koola.h"
#include <jni.h>
#include <shadowhook.h>

static jclass nativeHookClass = nullptr;
static jmethodID nativeLogMethod = nullptr;

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
    return env->NewStringUTF("/sdcard/Android/data/files/games/com.mojang");
}

extern "C" JNIEXPORT jint JNICALL
Java_cn_peyriat_koola_NativeHook_hookNativeGetUserDataPath(JNIEnv* env, jobject thiz) {
    void* original;
    void* hook = shadowhook_hook_sym_name(
            "libminecraftpe.so",
            "Java_com_mojang_minecraftpe_MainActivity_nativeGetUserDataPath",
            (void*)my_nativeGetUserDataPath,
            &original
    );
    return (hook != nullptr) ? 0 : -1;
}