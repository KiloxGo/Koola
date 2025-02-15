package cn.peyriat.koola

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.bytedance.shadowhook.ShadowHook;

class MainHook : IXposedHookLoadPackage {
    lateinit var stubbedClassLoader: ClassLoader
    lateinit var stubbedContext: Context
    var xposedClassLoader = MainHook::class.java.classLoader
    var loaded = false
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.contains("com.netease.x19")) {
            XposedHelpers.findAndHookMethod(
                "com.netease.android.protect.StubApp",
                lpparam.classLoader,
                "attachBaseContext",
                Context::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        stubbedContext = param.args[0] as Context
                        stubbedClassLoader = stubbedContext.classLoader
                        XposedBridge.log("Koola: StubApp attached")
                        hookLoadLibrary()
                    }
                }
            )
        } else {
            XposedBridge.log("Koola: ${lpparam.packageName} not matched")
        }
    }

    private fun hookLoadLibrary() {
        XposedHelpers.findAndHookMethod(
            Runtime::class.java,
            "loadLibrary0",
            Class::class.java,
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    XposedBridge.log("Koola: ${param.args[1]} loaded")
                    val libName = param.args[1] as String
                    if (libName == "minecraftpe") {
                        NativeHook.init()
                        XposedBridge.log("Koola: MinecraftPE loaded")
                    }
                }
            }
        )
    }





}

