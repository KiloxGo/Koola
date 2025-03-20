package cn.peyriat.koola


import com.bytedance.shadowhook.ShadowHook
import com.bytedance.shadowhook.ShadowHook.ConfigBuilder


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



}





