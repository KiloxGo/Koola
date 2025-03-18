package cn.peyriat.koola

import cn.peyriat.koola.util.LogUtils
import com.bytedance.shadowhook.ShadowHook
import com.bytedance.shadowhook.ShadowHook.ConfigBuilder
import de.robv.android.xposed.XposedBridge


//use object bcz @jvmstatic
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

        }
    external fun initHook():Int
    //external fun getLocalPlayer(): Int
    external fun flyToSky(enable: Boolean): Int




}





