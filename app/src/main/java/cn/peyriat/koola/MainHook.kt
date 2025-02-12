package cn.peyriat.koola

import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage {
    var stubbedClassLoader: ClassLoader? = null
    var context: Context? = null
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
                        context = param.args[0] as Context
                        stubbedClassLoader = context!!.classLoader
                        XposedBridge.log("Koola: StubApp attached")
                        hookLoadlibrary()
                    }
                }
            )


        }else{
            XposedBridge.log("Koola: ${lpparam.packageName} not matched")
        }
    }

    private fun hookLoadlibrary() {
        XposedHelpers.findAndHookMethod(
            "com.mojang.minecraftpe.MainActivity",
            stubbedClassLoader,
            "loadLibrary",
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val libName = param.args[0] as String
                    if (libName == "minecraftpe") {
                        XposedBridge.log("Koola: MinecraftPE loaded")
                        loadKoola()
                    }
                }
            }
        )
    }

    private fun loadKoola() {
        try {
            KoolaNative.setCustomUserDataPath(context!!.filesDir.absolutePath+"/koola/games/com.netease")
            Toast.makeText(context, "Koola loaded", Toast.LENGTH_SHORT).show()
        } catch (e: Error) {
            XposedBridge.log(e)
        }
    }
    object KoolaNative {
        init {
            System.loadLibrary("koola")
        }

        external fun setCustomUserDataPath(path: String)
    }

}

