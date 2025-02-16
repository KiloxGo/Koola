package cn.peyriat.koola

import com.bytedance.shadowhook.ShadowHook
import com.bytedance.shadowhook.ShadowHook.ConfigBuilder
import de.robv.android.xposed.XposedBridge



object NativeHook {
    fun init() {
        ShadowHook.init(
            ConfigBuilder()
                .setMode(ShadowHook.Mode.SHARED)
                .setDebuggable(true)
                .setRecordable(true)
                .build()
        )
        System.loadLibrary("koola")
    }
    external fun hookNativeGetUserDataPath(packagename:String):Int
    @JvmStatic
    fun nativeLog(message: String) {
            XposedBridge.log("[koola] $message")
    }

}





