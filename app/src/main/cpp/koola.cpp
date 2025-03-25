#include <sstream>
#include "koola.h"
#include <cstdio>
#include <cstring>
#include <atomic>
#include <thread>
#include "shadowhook/include/shadowhook.h"
#include "dexode/EventBus.hpp"
#include "dexode/eventbus/Bus.hpp"
#include "GameEvents.h"

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
//EventBus things
dexode::EventBus::Listener listener{bus};



// Function Pointer Define

static void* (*origLocalPlayerTickWorld)(void*);
static void* (*origPlayerAttack)(void*, void*, void*);
static void* (*origActorTick)(void*, void*);


//Point Addr Define
static uint64_t localPlayer;






// Hook 函数实现
static void* my_LocalPlayerTickWorld(void* player) {
    if (player != nullptr && localPlayer != reinterpret_cast<uint64_t>(player)) {
        localPlayer = reinterpret_cast<uint64_t>(player);
    }
    bus->postpone(event::GameUpdate{});
    LOG_DEBUG("LocalPlayer Address: %llx ", localPlayer);
    return origLocalPlayerTickWorld(player);
}

static void* my_ActorTick(void* actor, void* BlockSourceFromMainChunkSource) {
    LOG_DEBUG("Actor Address: %llx ", actor);
    return origActorTick(actor, BlockSourceFromMainChunkSource);
}

static void* my_PlayerAttack(void* PlayerInventory, void* Actor, void* ActorDamageCause) {
    LOG_DEBUG("PlayerInventory Address: %llx ", PlayerInventory);
    LOG_DEBUG("Player Attacked");
    return origPlayerAttack(PlayerInventory, Actor, ActorDamageCause);
}




JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    shadowhook_init(SHADOWHOOK_MODE_UNIQUE, true);
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}



extern "C"
JNIEXPORT jint JNICALL
Java_cn_peyriat_koola_NativeHook_initHook(JNIEnv *env, jobject thiz) {


    minecraftInfo.createInfo("libminecraftpe.so");

    void *hookLocalPlayerTickWorld = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x558CFC0),
            (void*) my_LocalPlayerTickWorld,
            (void**)&origLocalPlayerTickWorld);


    void *hookActorTick = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x725DA34),
            (void*) my_ActorTick,
            (void**)&origActorTick);

    void *hookPlayerAttack = shadowhook_hook_func_addr(
            (void*)(minecraftInfo.head + 0x6D0CE6C),
            (void*)my_PlayerAttack,
            (void**)&origPlayerAttack);



    if (hookLocalPlayerTickWorld == nullptr) {
        LOG_DEBUG("hookLocalPlayerTickWorld failed");
        return -1;
    }
    if (hookActorTick == nullptr) {
        LOG_DEBUG("hookActorTick failed");
        return -1;
    }

    if (hookPlayerAttack == nullptr) {
        LOG_DEBUG("hookPlayerAttack failed");
        return -1;
    }

    return 0;

}







