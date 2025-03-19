package cn.peyriat.koola

import GameUpdateEvent
import androidx.annotation.Keep
import cn.peyriat.koola.util.LogUtils
import com.bytedance.shadowhook.ShadowHook
import com.bytedance.shadowhook.ShadowHook.ConfigBuilder
import de.robv.android.xposed.XposedBridge


object NativeHook {
    init {
        ShadowHook.init(
                ConfigBuilder()
                    .setMode(ShadowHook.Mode.UNIQUE)
                    .setDebuggable(true)
                    .setRecordable(true)
                    .build()
            )
            System.loadLibrary("koola")
            initHook()

        }
    external fun initHook():Int
    //external fun getLocalPlayer(): Int
    external fun flyToSky(enable: Boolean): Int

    private external fun nativeOnGameUpdate()

    @Keep //
    private fun onNativeGameUpdate() {
        EventBus.post(GameUpdateEvent())
    }





}





