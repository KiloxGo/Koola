    #include "koola.h"
    #include <jni.h>
    #include "string"
    #include <shadowhook.h>
    #include <android/log.h>

    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
        JNIEnv* env;
        if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
            return JNI_ERR;
        }
        return JNI_VERSION_1_6;
    }

    static void* (*origLocalPlayerOnTick)(void*, void*); //原函数指针

    #define LOG_DEBUG(...)  ( __android_log_print(ANDROID_LOG_DEBUG, "Koola", __VA_ARGS__));

    //GameType


    //他参数传过来不是void嘛
    static void* my_LocalPlayerOnTick(void* player,void* tick) {
        LOG_DEBUG("Player: %llx", player);

        if (player == nullptr) {
            return origLocalPlayerOnTick(player, tick);
        }

        //Just for test
        StateVectorComp** stateVectorCompPtr = (StateVectorComp**)((uint64_t)player + 0x318);
        StateVectorComp* stateVectorComp = *stateVectorCompPtr;
        if (stateVectorComp == nullptr) {
            return origLocalPlayerOnTick(player, tick);
        }
        stateVectorComp->velocity.y = 1;
        //Fly into sky.
        return origLocalPlayerOnTick(player, tick);
    }




    extern "C" JNIEXPORT jint JNICALL
    Java_cn_peyriat_koola_NativeHook_getPlayer(JNIEnv* env, jobject thiz) {
        minecraftInfo.createInfo("libminecraftpe.so");
        void *hook = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x558CFC0),
            (void*)my_LocalPlayerOnTick,
            (void**)&origLocalPlayerOnTick);
        return (hook != nullptr) ? 0 : -1;
    }