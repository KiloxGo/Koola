package cn.peyriat.koola

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    lateinit var stubbedClassLoader: ClassLoader
    lateinit var stubbedContext: Context
    lateinit var packagename:String
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val stubAppClass = XposedHelpers.findClassIfExists(
            "com.netease.android.protect.StubApp",
            lpparam.classLoader
        )
        if (stubAppClass != null) {
            packagename = lpparam.packageName
            XposedBridge.log("Koola: Detected StubApp in ${lpparam.packageName}")
            XposedHelpers.findAndHookMethod(
                stubAppClass,
                "attachBaseContext",
                Context::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        stubbedContext = param.args[0] as Context
                        stubbedClassLoader = stubbedContext.classLoader
                        XposedBridge.log("Koola: StubApp attached in $packagename")
                        hookLoadLibrary()
                    }
                }
            )
        } else {
            XposedBridge.log("Koola: ${lpparam.packageName} skipped (no StubApp)")
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
                    val libName = param.args[1] as String
                    if (libName == "minecraftpe") {
                        if (NativeHook.hookNativeGetUserDataPath(packagename) == 0) {
                            XposedBridge.log("Koola:  hooked")
                        } else {
                            XposedBridge.log("Koola:  failed")
                        }
                    }
                }
            }
        )
    }





}

