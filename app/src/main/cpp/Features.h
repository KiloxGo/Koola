// Features.h
#ifndef FEATURES_H
#define FEATURES_H

#include "dexode/eventbus/Bus.hpp"
#include "GameEvents.h"

namespace Module {

    class FlyToSky {
        static float speed;
        static dexode::EventBus::Listener _listener;
    public:
        static void setSpeed(float speed);
        static void enable();
        static void disable();
    private:
        static void onUpdate();
    };

}

#endif  // FEATURES_H
