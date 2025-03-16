#ifndef KOOLA_KOOLA_H
#define KOOLA_KOOLA_H


class koola {




};

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



#endif //KOOLA_KOOLA_H
