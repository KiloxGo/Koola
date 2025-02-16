package cn.peyriat.koola

import android.content.Context
import cn.peyriat.koola.util.LogUtils
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    lateinit var stubbedClassLoader: ClassLoader
    lateinit var stubbedContext: Context
    lateinit var packagename:String
    var loaded:Boolean = false
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        var stubclass = XposedHelpers.findClassIfExists(
            "com.netease.android.protect.StubApp",
            lpparam.classLoader
        )
        if (stubclass != null) {
            packagename = lpparam.packageName

            //NativeHook.hookNativeGetUserDataPath(packagename)

            XposedHelpers.findAndHookMethod(
                stubclass,
                "attachBaseContext",
                Context::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        stubbedContext = param.args[0] as Context
                        stubbedClassLoader = stubbedContext.classLoader
                        hookLoadLibrary()
                    }
                }
            )

        }
    }

    private fun hookLoadLibrary() {
        XposedHelpers.findAndHookMethod(
            "com.mojang.minecraftpe.MainActivity",
            stubbedClassLoader,
            "nativeGetUserDataPath",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    LogUtils.javaLog("nativeGetUserDataPath ${param.result}")
                }
            })
        XposedHelpers.findAndHookMethod(
            Runtime::class.java,
            "loadLibrary0",
            Class::class.java,
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val libName = param.args[1] as String
                    LogUtils.javaLog("loadLibrary0: $libName")
                }
            }
        )


    }





}

