//
// Created by Laptop on 2025/3/25.
//

#ifndef KOOLA_FEATURES_H
#define KOOLA_FEATURES_H

namespace Module {

    class FlyToSky {
        int speed = 0;
    public:
        void enable();
        void disable();
        bool isEnabled();

    };


} // Module

#endif //KOOLA_FEATURES_H
