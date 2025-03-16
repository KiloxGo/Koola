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
                env->ExceptionClear();
                return;
            }
            nativeHookClass = (jclass) env->NewGlobalRef(localClass);
            env->DeleteLocalRef(localClass);
        }

        if (nativeLogMethod == nullptr) {
            nativeLogMethod = env->GetStaticMethodID(nativeHookClass, "nativeLog", "(Ljava/lang/String;)V");
            if (nativeLogMethod == nullptr) {
                env->ExceptionClear();
            }
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

    void my_nativeGetUserDataPath_hook_callback(int error_number, const char* lib_name, const char* sym_name,
                       void* sym_addr, void* new_addr, void* orig_addr, void* arg) {
        JNIEnv* env = (JNIEnv*) arg;
        if (error_number == 0) {
            logToXposed(env, "Hook successful!");
        } else {
            const char* error_msg = shadowhook_to_errmsg(error_number);
            logToXposed(env, error_msg);
        }
    }




    static jstring my_nativeGetUserDataPath(JNIEnv* env, jobject thiz) {
        const char* packageNameCStr = env->GetStringUTFChars(apppackagename, nullptr);
        std::string path = "/storage/emulated/0/Android/data/" + std::string(packageNameCStr) + "/files/koola/games/com.netease";
        env->ReleaseStringUTFChars(apppackagename, packageNameCStr);
        return env->NewStringUTF(path.c_str());
    }
    extern "C" JNIEXPORT jint JNICALL
    Java_cn_peyriat_koola_NativeHook_starthook(JNIEnv* env, jobject thiz){
        logToXposed(env, "Hooking started");
        return 0;
    }









    extern "C" JNIEXPORT jint JNICALL
    Java_cn_peyriat_koola_NativeHook_hookNativeGetUserDataPath(JNIEnv* env, jobject thiz, jstring packagename) {
        apppackagename = (jstring)env->NewGlobalRef(packagename);
        void *orig;
        void *hook = shadowhook_hook_sym_name_callback("libminecraftpe.so",
                                                       "Java_com_mojang_minecraftpe_MainActivity_nativeGetUserDataPath",
                                                       (void*)my_nativeGetUserDataPath,
                                                       &orig,
                                                       my_nativeGetUserDataPath_hook_callback,
                                                       (void*) env);

        return (hook != nullptr) ? 0 : -1;
    }