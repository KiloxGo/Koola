#ifndef KOOLA_H
#define KOOLA_H

#include <jni.h>
#include <cstdint>
#include <string>

#include <android/log.h>
#include "dexode/EventBus.hpp"

class ModuleInfo {
public:
    uint64_t head;
    uint64_t end;
    bool createInfo(const char* libName);
};


class Vector3f {
public:
    float x;
    float y;
    float z;
    Vector3f(float x, float y, float z);
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
    float width;
    float height;
};

class RotationComp {
public:
    float pitch;
    float yaw;
    float pitchFake;
    float yawFake;
};


#define LOG_DEBUG(...)  ( __android_log_print(ANDROID_LOG_DEBUG, "Koola", __VA_ARGS__))

extern ModuleInfo minecraftInfo;

#endif // KOOLA_H
