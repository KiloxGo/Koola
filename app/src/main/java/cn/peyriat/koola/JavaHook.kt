package cn.peyriat.koola

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass

object JavaHook: YukiBaseHooker() {
    override fun onHook() {
        "com.mojang.minecraftpe.MainActivity".toClass().apply {
            method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                after {

                }
            }
        }
    }


}
