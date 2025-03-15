package cn.peyriat.koola
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
@InjectYukiHookWithXposed
class HookEntry:IYukiHookXposedInit{
    override fun onHook() {
        YukiHookAPI.encase{
               "com.netease.android.protect.StubApp".toClass().apply {
                   method {
                       name = "attachBaseContext"
                       param(ContextClass)
                   }.hook {
                       after {
                           loadHooker(JavaHook)
                   }
               }
            }
        }
    }
}

