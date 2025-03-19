#include <sstream>
#include "koola.h"
#include <cstdio>
#include <cstring>
#include <atomic>
#include <thread>

// ModuleInfo 实现
bool ModuleInfo::createInfo(const char *libName) {
    char buff[256];
    FILE* fp;

    fp = fopen("/proc/self/maps", "r");
    if (fp == nullptr) return false;

    bool isFound = false;
    bool flag = false;

    while (!feof(fp)) {
        fgets(buff, sizeof(buff), fp);
        if (!feof(fp) && strstr(buff, "(deleted)") == nullptr &&
            strstr(buff, libName) != nullptr) {
            uint64_t hAddr = 0;
            uint64_t eAddr = 0;
            char *endPtr;
            hAddr = strtoul(buff, &endPtr, 16);
            if (*endPtr == '-') {
                eAddr = strtoul(endPtr + 1, nullptr, 16);
            }
            if (hAddr != 0 && eAddr != 0) {
                if (!flag) {
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

// Vector3f
Vector3f::Vector3f(float x, float y, float z) : x(x), y(y), z(z) {}

// Global Variables
ModuleInfo minecraftInfo;
JNIEnv *globalEnv = nullptr;
jobject globalObj = nullptr;

// Function Pointer Define
static void* (*origLocalPlayerOnTick)(void*, void*);
static void* (*origPlayerOnTick)(void*, void*);

//Point Addr Define
static uintptr_t localPlayerAddr;

//Funtion Flag
std::atomic<bool> isFlying(false);

//CallBack
extern "C"
JNIEXPORT void JNICALL
Java_cn_peyriat_koola_NativeHook_nativeOnGameUpdate(JNIEnv *env, jobject clazz) {}



// Hook 函数实现
static void* my_LocalPlayerOnTick(void* player, void* tick) {
    localPlayerAddr = reinterpret_cast<uintptr_t>(player);
    LOG_DEBUG("LocalPlayer Address: %llx ", localPlayerAddr);
    Java_cn_peyriat_koola_NativeHook_nativeOnGameUpdate(globalEnv, globalObj);
    return origLocalPlayerOnTick(player, tick);
}




JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jint JNICALL
Java_cn_peyriat_koola_NativeHook_initHook(JNIEnv *env, jobject thiz) {
    globalEnv = env;
    globalObj = thiz;
    minecraftInfo.createInfo("libminecraftpe.so");
    void *hookLocalPlayer = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x558CFC0),
            (void*)my_LocalPlayerOnTick,
            (void**)&origLocalPlayerOnTick);
    if (hookLocalPlayer == nullptr) {
        LOG_DEBUG("Hook failed");
        return -1;
    }
    return 0;

}
extern "C"
JNIEXPORT jint JNICALL
Java_cn_peyriat_koola_NativeHook_flyToSky(JNIEnv* env, jobject thiz, jboolean enable) {
    if (localPlayerAddr == 0) {
        LOG_DEBUG("LocalPlayer Address is null");
        return -1;
    }
    // 根据 enable 参数决定是否持续修改 y
    if (enable) {
        isFlying.store(true); // 设置飞行状态
        std::thread([]() { // 嵌套线程进行持续修改
            while (isFlying.load()) { // 如果标志变量为 true，则持续执行
                StateVectorComp** stateVectorCompPtr = (StateVectorComp**)((uint64_t)localPlayerAddr + 0x318);
                if (stateVectorCompPtr) {
                    StateVectorComp* stateVectorComp = *stateVectorCompPtr;
                    if (stateVectorComp) {
                        stateVectorComp->velocity.y = 1; // 持续修改 y
                        LOG_DEBUG("Setting velocity.y to 1");
                    }
                }
                usleep(100000); // 休息 100 毫秒，防止过于频繁修改
            }
        }).detach(); // 分离线程，防止阻塞主线程
    } else {
        isFlying.store(false); // 停止飞行状态
    }
    return 0;
}