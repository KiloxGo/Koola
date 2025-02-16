package cn.peyriat.koola

import cn.peyriat.koola.util.LogUtils
import com.bytedance.shadowhook.ShadowHook
import com.bytedance.shadowhook.ShadowHook.ConfigBuilder
import de.robv.android.xposed.XposedBridge


//use object bcz @jvmstatic
object NativeHook {
    private var loaded = false
    init {
        if (!loaded) {
            ShadowHook.init(
                ConfigBuilder()
                    .setMode(ShadowHook.Mode.UNIQUE)
                    .setDebuggable(true)
                    .setRecordable(true)
                    .build()
            )
            System.loadLibrary("koola")
            loaded = true
        }
    }
    external fun hookNativeGetUserDataPath(packagename:String):Int

    @JvmStatic
    fun nativeLog(message: String) {
        LogUtils.nativeLog(message)
    }

}





