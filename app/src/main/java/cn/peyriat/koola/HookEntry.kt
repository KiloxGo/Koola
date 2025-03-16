package cn.peyriat.koola
import android.app.Activity
import android.view.ViewGroup
import android.widget.TextView
import cn.peyriat.koola.ui.CONFIG
import cn.peyriat.koola.util.LogUtils
import cn.peyriat.koola.util.saveConfig
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry:IYukiHookXposedInit {
    private var floatingView: TextView? = null
    override fun onHook() {
        YukiHookAPI.encase {
            loadApp(true) {
                if (packageName == "com.google.android.webview") {return@loadApp}
                "com.netease.android.protect.StubApp".toClass().apply {
                    method {
                        name = "attachBaseContext"
                        param(ContextClass)
                    }.hook {
                        after {
                            loadHooker(ActivityHook)
                            //loadHooker(RNhook)
                            loadHooker(LibHook)
                        }
                    }
                }
            }
        }
    }
    object RNhook:YukiBaseHooker() {
        override fun onHook() {
            "com.mojang.minecraftpe.MainActivity".toClass().apply {
                method {
                    name = "nativeJsCallCpp"
                    param(StringClass)
                }.hook {
                    before {
                        LogUtils.javaLog("RNMCBridge callCpp: ${args}}")
                    }
                }
                method{
                    name = "nativeGetUserDataPath"
                    returnType = StringClass
                }.hook{
                    after {
                        LogUtils.javaLog("hi")
                        LogUtils.javaLog("nativeGetUserDataPath: ${result as String}}")
                    }
                }
            }
        }
    }

    object LibHook : YukiBaseHooker() {
        override fun onHook() {
            Runtime::class.java.apply {
                method {
                    name = "loadLibrary0"
                    param(Class::class.java, StringClass)
                }.hook {
                    after {
                        LogUtils.javaLog(args[1] as String)
                        if (args[1] as String != "minecraftpe") {
                            return@after
                        }
                        if (NativeHook.getPlayer() == 0) {
                            LogUtils.javaLog("hook success")
                        } else {
                            LogUtils.javaLog("hook failed")
                        }

                    }
                }
            }
        }
    }

    object ActivityHook : YukiBaseHooker() {
        override fun onHook() {
            "com.mojang.minecraftpe.MainActivity".toClass().apply {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }.hook {
                    after {
                        val activity = instance as? Activity ?: return@after
                        val containerLayout = android.widget.LinearLayout(activity).apply {
                            orientation = android.widget.LinearLayout.VERTICAL
                            setBackgroundColor(android.graphics.Color.parseColor("#F0FFFFFF"))
                            setPadding(20, 10, 20, 10)
                            alpha = 0.9f
                        }
                        
                        val titleText = TextView(activity).apply {
                            text = "Koola 已启用"
                            textSize = 16f
                            setTextColor(android.graphics.Color.BLACK)
                            setPadding(0, 0, 0, 10)
                        }
                        containerLayout.addView(titleText)
                        
                        val switch1Layout = createSwitchLayout(activity, "Item1", CONFIG.optBoolean("InGameSwitch1", false)) { isChecked ->
                            LogUtils.javaLog("Item1: $isChecked")
                            CONFIG.put("InGameSwitch1", isChecked)
                            saveConfig(activity, CONFIG)
                        }
                        containerLayout.addView(switch1Layout)
                        
                        val switch2Layout = createSwitchLayout(activity, "Item2", CONFIG.optBoolean("InGameSwitch2", false)) { isChecked ->
                            LogUtils.javaLog("Item2: $isChecked")
                            CONFIG.put("InGameSwitch2", isChecked)
                            saveConfig(activity, CONFIG)
                        }
                        containerLayout.addView(switch2Layout)
                        
                        val layoutParams = android.widget.FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = android.view.Gravity.TOP or android.view.Gravity.END
                            setMargins(0, 100, 20, 0) 
                        }
                        
                        val rootView = activity.window.decorView as ViewGroup
                        rootView.addView(containerLayout, layoutParams)
                        
                        containerLayout.setOnTouchListener(object : android.view.View.OnTouchListener {
                            private var initialX = 0
                            private var initialY = 0
                            private var initialTouchX = 0f
                            private var initialTouchY = 0f
                            
                            override fun onTouch(v: android.view.View, event: android.view.MotionEvent): Boolean {
                                when (event.action) {
                                    android.view.MotionEvent.ACTION_DOWN -> {
                                        initialX = layoutParams.leftMargin
                                        initialY = layoutParams.topMargin
                                        initialTouchX = event.rawX
                                        initialTouchY = event.rawY
                                        return true
                                    }
                                    android.view.MotionEvent.ACTION_MOVE -> {
                                        layoutParams.leftMargin = initialX + (event.rawX - initialTouchX).toInt()
                                        layoutParams.topMargin = initialY + (event.rawY - initialTouchY).toInt()
                                        containerLayout.layoutParams = layoutParams
                                        return true
                                    }
                                }
                                return false
                            }
                        })
                    }
                }
            }
        }
        
        private fun createSwitchLayout(context: Activity, label: String, initialState: Boolean, onCheckedChange: (Boolean) -> Unit): android.widget.LinearLayout {
            return android.widget.LinearLayout(context).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 5
                    bottomMargin = 5
                }
                
                val textView = TextView(context).apply {
                    text = label
                    textSize = 14f
                    setTextColor(android.graphics.Color.BLACK)
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }
                addView(textView)
                
                val switch = android.widget.Switch(context).apply {
                    isChecked = initialState
                    setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }
                }
                addView(switch)
            }
        }
    }

}

