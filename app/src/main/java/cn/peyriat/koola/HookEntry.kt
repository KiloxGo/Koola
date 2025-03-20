package cn.peyriat.koola
import android.app.Activity
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import cn.peyriat.koola.game.features.FlyToSky
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
    companion object {
        private var floatingView: View? = null
        private var floatingViewParams: WindowManager.LayoutParams? = null
    }

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
                        if (args[1] as String == "minecraftpe") {
                            NativeHook.initHook()
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
                        LogUtils.javaLog("MainActivity onCreate hooked")
                        
                        val activity = instance as Activity
                        val windowManager = activity.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
                        
                        if (floatingView == null) {
                            val container = android.widget.LinearLayout(activity).apply {
                                orientation = android.widget.LinearLayout.VERTICAL
                                setPadding(25, 20, 25, 20) // 增大内边距
                            background = android.graphics.drawable.GradientDrawable().apply {
                                cornerRadius = 25f  // 增大圆角
                                setColor(android.graphics.Color.parseColor("#F5FFFFFF"))  // 白底微透明
                            }
                            }

                            val titleView = TextView(activity).apply {
                                text = "Koola 控制面板"
                                textSize = 18f
                                setTextColor(android.graphics.Color.BLACK)
                                gravity = android.view.Gravity.CENTER
                                setPadding(0, 0, 0, 20)
                            }
                            container.addView(titleView)

                            val switch1Container = android.widget.LinearLayout(activity).apply {
                                orientation = android.widget.LinearLayout.HORIZONTAL
                                layoutParams = android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    bottomMargin = 15
                                }
                            }
                            
                            val switch1Label = TextView(activity).apply {
                                text = "功能1"
                                textSize = 16f
                                setTextColor(android.graphics.Color.BLACK)
                                layoutParams = android.widget.LinearLayout.LayoutParams(
                                    0,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                                )
                            }
                            
                            val switch1 = android.widget.Switch(activity).apply {
                                isChecked = false
                                setOnCheckedChangeListener { _, isChecked ->
                                    LogUtils.javaLog("FlyToSky: $isChecked")
                                }
                            }
                            
                            switch1Container.addView(switch1Label)
                            switch1Container.addView(switch1)
                            container.addView(switch1Container)

                            val switch2Container = android.widget.LinearLayout(activity).apply {
                                orientation = android.widget.LinearLayout.HORIZONTAL
                                layoutParams = android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            }
                            
                            val switch2Label = TextView(activity).apply {
                                text = "功能2"
                                textSize = 14f
                                setTextColor(android.graphics.Color.BLACK)
                                layoutParams = android.widget.LinearLayout.LayoutParams(
                                    0,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                                )
                            }
                            
                            val switch2 = android.widget.Switch(activity).apply {
                                isChecked = false
                                setOnCheckedChangeListener { _, isChecked ->
                                    LogUtils.javaLog("开关2状态: $isChecked")
                                    // 在这里添加功能2的逻辑
                                }
                            }
                            
                            switch2Container.addView(switch2Label)
                            switch2Container.addView(switch2)
                            container.addView(switch2Container)

                            floatingView = container

                            floatingViewParams = WindowManager.LayoutParams().apply {
                                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    WindowManager.LayoutParams.TYPE_APPLICATION
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    WindowManager.LayoutParams.TYPE_PHONE
                                } else {
                                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                                }
                                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                                       WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                width = WindowManager.LayoutParams.WRAP_CONTENT
                                height = WindowManager.LayoutParams.WRAP_CONTENT
                                x = 100
                                y = 200
                            }

                            container.setOnTouchListener(object : View.OnTouchListener {
                                private var initialX: Int = 0
                                private var initialY: Int = 0
                                private var initialTouchX: Float = 0f
                                private var initialTouchY: Float = 0f

                                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> {
                                            initialX = floatingViewParams!!.x
                                            initialY = floatingViewParams!!.y
                                            initialTouchX = event.rawX
                                            initialTouchY = event.rawY
                                            return true
                                        }
                                        MotionEvent.ACTION_MOVE -> {
                                            floatingViewParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                                            floatingViewParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                                            windowManager.updateViewLayout(floatingView, floatingViewParams)
                                            return true
                                        }
                                        MotionEvent.ACTION_UP -> {
                                            val isDrag = Math.abs(event.rawX - initialTouchX) > 10 || 
                                                        Math.abs(event.rawY - initialTouchY) > 10
                                            if (!isDrag) {
                                                return false
                                            }
                                            return true
                                        }
                                    }
                                    return false
                                }
                            })
                            
                            try {
                                windowManager.addView(floatingView, floatingViewParams)
                                LogUtils.javaLog("悬浮窗添加成功")
                            } catch (e: Exception) {
                                LogUtils.javaLog("悬浮窗添加失败: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }
}

