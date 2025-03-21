package cn.peyriat.koola.ui.ingame

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import cn.peyriat.koola.util.LogUtils

class FloatingWindow(private val activity: Activity) {

    private var floatingView: View? = null
    private var floatingViewParams: WindowManager.LayoutParams? = null
    private val windowManager by lazy { activity.windowManager }

    fun show() {
        if (floatingView != null) return

        floatingView = createContainerView().apply {
            setOnTouchListener(TouchListener())
        }

        floatingViewParams = createLayoutParams()

        try {
            windowManager.addView(floatingView, floatingViewParams)
            LogUtils.javaLog("应用内悬浮窗添加成功")
        } catch (e: Exception) {
            LogUtils.javaLog("悬浮窗添加失败: ${e.message}")
            e.printStackTrace()
        }
    }

    fun hide() {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
            floatingViewParams = null
        }
    }

    private fun createContainerView() = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(25, 20, 25, 20)
        background = android.graphics.drawable.GradientDrawable().apply {
            cornerRadius = 25f
            setColor(android.graphics.Color.parseColor("#F5FFFFFF"))
        }

        addView(createTitleView())
        addView(createSwitchRow("功能1") { isChecked ->
            LogUtils.javaLog("FlyToSky: $isChecked")
        })
        addView(createSwitchRow("功能2") { isChecked ->
            LogUtils.javaLog("功能2状态: $isChecked")
        })
    }

    private fun createTitleView() = TextView(activity).apply {
        text = "Koola 控制面板"
        textSize = 18f
        setTextColor(android.graphics.Color.BLACK)
        gravity = Gravity.CENTER
        setPadding(0, 0, 0, 20)
    }

    private fun createSwitchRow(label: String, onChecked: (Boolean) -> Unit) =
        LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 15 }

            addView(TextView(activity).apply {
                text = label
                textSize = 16f
                setTextColor(android.graphics.Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            })

            addView(Switch(activity).apply {
                isChecked = false
                setOnCheckedChangeListener { _, isChecked -> onChecked(isChecked) }
            })
        }

    private fun createLayoutParams() = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowManager.LayoutParams.TYPE_PHONE
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        token = activity.window?.decorView?.windowToken
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        x = 100
        y = 200
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.START or Gravity.TOP
    }

    private inner class TouchListener : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

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
                    val isDrag = kotlin.math.abs(event.rawX - initialTouchX) > 10 ||
                            kotlin.math.abs(event.rawY - initialTouchY) > 10
                    return isDrag
                }
            }
            return false
        }
    }
}
