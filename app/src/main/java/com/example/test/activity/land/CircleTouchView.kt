package com.example.test.activity.land

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class CircleTouchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val debugPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x6600FF00 // 半透明绿色，用于调试
        style = Paint.Style.FILL
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var isInCircle = false
    private var isClickConsumed = false
    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var initialX = 0f
    private var initialY = 0f

    var onCircleInClickListener: (() -> Unit)? = null
    var onCircleInLongClickListener: (() -> Unit)? = null
    var onCircleOutClickListener: (() -> Unit)? = null
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = width / 2f
        centerY = height / 2f
        radius = width / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 调试用，正式版可移除
        canvas.drawCircle(centerX, centerY, radius, debugPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.rawX
                initialY = event.rawY
                isInCircle = isPointInCircle(x, y)
                isClickConsumed = false

                if (isInCircle) {
                    // 启动长按检测
                    postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
                }
                return true // 总是消费DOWN事件以便跟踪后续事件
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isClickConsumed) {
                    val dx = abs(event.rawX - initialX)
                    val dy = abs(event.rawY - initialY)

                    // 检测滑动距离是否超过阈值
                    if (dx > touchSlop || dy > touchSlop) {
                        // 滑动发生时取消点击/长按检测
                        removeCallbacks(longPressRunnable)
                        isClickConsumed = true
                        isInCircle = false
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                removeCallbacks(longPressRunnable)
                if (!isClickConsumed ) {
                    if (isInCircle && isPointInCircle(x, y)) {
                        onCircleInClickListener?.invoke()
                    } else {
                        onCircleOutClickListener?.invoke()
                    }
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressRunnable)
                return true
            }
        }
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(longPressRunnable)
    }

    private val longPressRunnable = Runnable {
        if (isInCircle && !isClickConsumed) {
            isClickConsumed = true
            onCircleInLongClickListener?.invoke()
        }
    }

    private fun isPointInCircle(x: Float, y: Float): Boolean {
        val dx = x - centerX
        val dy = y - centerY
        return dx * dx + dy * dy <= radius * radius
    }

    private fun abs(value: Float): Float = if (value < 0) -value else value
}