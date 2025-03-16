    #include "koola.h"
    #include <jni.h>
    #include "string"
    #include <shadowhook.h>
    #include <android/log.h>


    class ModuleInfo {
    public:
        uint64_t head;
        uint64_t end;

        bool createInfo(const char* libName);
    };


    bool ModuleInfo::createInfo(const char *libName) {
        char buff[256];
        FILE* fp;

        fp = fopen("/proc/self/maps", "r");
        if (fp == 0)  return false;

        bool isFound = false;
        bool flag = false;

        while (!feof(fp))
        {
            fgets(buff, sizeof(buff), fp);
            if (!feof(fp) && strstr(buff, "(deleted)") == 0 && strstr(buff, libName) != 0) {
                uint64_t hAddr = 0;
                uint64_t eAddr = 0;
                sscanf(buff, "%lx-%lx", &hAddr, &eAddr);
                if (hAddr != 0 && eAddr != 0)
                {
                    if (!flag)
                    {
                        head = hAddr;
                        flag = true;
                    }
                    end = eAddr;
                    isFound = true;
                }
            }
        }
        fclose(fp);
        return isFound;
    }

    static ModuleInfo minecraftInfo;

    class Vector3f {
    public:
        float x;
        float y;
        float z;

        Vector3f(float x, float y, float z) : x(x), y(y), z(z) {}
    };

    class StateVectorComp {
    public:
        Vector3f currentPos;
        Vector3f eyePos;
        Vector3f velocity;
    };

    class AABBShapeComp {
    public:
        Vector3f lowerPos;
        Vector3f upperPos;
        float width; //HitBox
        float height;
    };

    class RotationComp {
        float pitch;
        float yaw;
        float pitchFake;
        float yawFake;
    };


#define LOG_DEBUG(...)  ( __android_log_print(ANDROID_LOG_DEBUG, "Koola", __VA_ARGS__));
    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
        JNIEnv* env;
        if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
            return JNI_ERR;
        }
        return JNI_VERSION_1_6;
    }

    static void* (*origLocalPlayerOnTick)(void*, void*); //原函数指针
    static void* (*origPlayerOnTick)(void*, void*); //原函数指针

    static void* my_LocalPlayerOnTick(void* player,void* tick) {
        LOG_DEBUG("LocalPlayer: %llx", player);

        if (player == nullptr) {
            return origLocalPlayerOnTick(player, tick);
        }

        //Just for test
        StateVectorComp** stateVectorCompPtr = (StateVectorComp**)((uint64_t)player + 0x318);
        StateVectorComp* stateVectorComp = *stateVectorCompPtr;
        if (stateVectorComp == nullptr) {
            return origLocalPlayerOnTick(player, tick);
        }
        //stateVectorComp->velocity.y = 1;
        //Fly into sky.


        return origLocalPlayerOnTick(player, tick);
    }
    static void* my_PlayerOnTick(void* player, void* tick) {
        LOG_DEBUG("Player: %llx", player);
        //stateVectorComp->velocity.y = 1;
        //Fly into sky.
        return origPlayerOnTick(player, tick);
    }



    extern "C" JNIEXPORT jint JNICALL
    Java_cn_peyriat_koola_NativeHook_getPlayer(JNIEnv* env, jobject thiz) {
        minecraftInfo.createInfo("libminecraftpe.so");
        void *hookPlayer = shadowhook_hook_func_addr(
                (void*)(minecraftInfo.head + 0x6D14028),
                (void*)my_PlayerOnTick,
                (void**)&origPlayerOnTick);


        void *hookLocalPlayer = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x558CFC0),
            (void*)my_LocalPlayerOnTick,
            (void**)&origLocalPlayerOnTick);

        if (hookLocalPlayer == nullptr) {
            LOG_DEBUG("Hook failed");
            return -1;
        }
        if (hookPlayer == nullptr) {
            LOG_DEBUG("Hook failed");
            return -1;
        }
        return 0;
    }
