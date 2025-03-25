//
// Created by Laptop on 2025/3/25.
//
#include "koola.h"
#include "Features.h"
#include "dexode/eventbus/Bus.hpp"
#include "GameEvents.h"


namespace Module {
    dexode::EventBus::Listener listener{bus};
    void FlyToSky::enable() {
        listener.listen([](const event::GameUpdate& event) // listen with lambda
                        {
                            ANDROID_LOG_DEBUG"I received gold: " << event.goldReceived << " 💰" << std::endl;
                        });
        speed = 100;

    }

    void FlyToSky::disable() {

    }

    bool FlyToSky::isEnabled() {

        return false; // Placeholder return value
    }


} // Module