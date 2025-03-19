package cn.peyriat.koola.game.features

import GameUpdateEvent
import SubscribeEvent
import cn.peyriat.koola.util.LogUtils

class FlyToSky {
    init {
        LogUtils.javaLog("FlyToSky initialized")
        // Register the event handler
        EventBus.register(this)
    }
    @SubscribeEvent
    fun handleGameUpdate(event: GameUpdateEvent) {
        LogUtils.javaLog("Game Update Event Triggered")
    }

}