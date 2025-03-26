#include "Features.h"
#include "koola.h"

namespace Module {


    float FlyToSky::speed = 0.0f;
    dexode::EventBus::Listener FlyToSky::_listener;

    void FlyToSky::setSpeed(float newSpeed) {
        FlyToSky::speed = newSpeed;
    }

    void FlyToSky::enable() {
        LOG_DEBUG("enable fly to sky");
        if (!bus) {
            LOG_DEBUG("EventBus is not initialized!");
            return;
        }
        if (!_listener.isListening<event::GameUpdate>()) {
            LOG_DEBUG("Listener is not listening to GameUpdate event!");
        }
        _listener.listen<event::GameUpdate>(
                std::bind(&FlyToSky::onUpdate)
        );
    }


    void FlyToSky::disable() {
        _listener.unlisten<event::GameUpdate>();
    }

    void FlyToSky::onUpdate() {
        LOG_DEBUG("received event");
    }

}
