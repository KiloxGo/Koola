package cn.peyriat.koola.util

import de.robv.android.xposed.XposedBridge

object LogUtils {
    @JvmStatic
    fun javaLog(message: String) {
            XposedBridge.log("[Java]: $message")
    }
    @JvmStatic
    fun nativeLog(message: String) {
            XposedBridge.log("[Native]: $message")
    }
}