package com.example.test.activity.land

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class TouchInterceptorRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var handleImageViewId: Int = 0
    private var interceptedByHandle: Boolean = false

    // 长按监听回调
    var onLongTouchListener: (() -> Unit)? = null
    var onChildAttachedToWindow: ((View) -> Unit)? = null

    // 长按阈值时间 - 500ms
    private val LONG_TOUCH_THRESHOLD = 500L

    private val longTouchHandler = Handler(Looper.getMainLooper())
    private val longTouchRunnable = Runnable {
        // 检查当前触摸的把手视图
        if ((abs(currentX - initX)) > 10) {
            onLongTouchListener?.invoke()
            Log.d("TouchInterceptorRecyclerView", "Long touch detected on handle view")
        }
    }

    // 存储当前触摸点的位置
    private var initX = 0f
    private var currentX = 0f

    fun setHandleImageViewId(imageViewId: Int) {
        this.handleImageViewId = imageViewId
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                interceptedByHandle = isTouchOnHandle(e.x, e.y)

                if (interceptedByHandle) {
                    // 保存当前触摸点
                    initX = e.x
                    currentX = e.x
                    // 设置长按检测
                    longTouchHandler.postDelayed(longTouchRunnable, LONG_TOUCH_THRESHOLD)
                }

                if (!interceptedByHandle) {
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // 移动过程中保持更新触摸点位置
                if (interceptedByHandle) {
                    currentX = e.x
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 移除长按检测
                longTouchHandler.removeCallbacks(longTouchRunnable)
                initX = 0f
                currentX = 0f
            }
        }

        return if (interceptedByHandle) super.onInterceptTouchEvent(e) else false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                if (interceptedByHandle) {
                    currentX = e.x
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 移除长按检测
                longTouchHandler.removeCallbacks(longTouchRunnable)
                initX = 0f
                currentX = 0f
            }
        }

        val gestureEnable = (adapter as MusicAdapter2).isRecyclerViewEnable()

        return if (interceptedByHandle && gestureEnable) super.onTouchEvent(e) else false
    }

    private fun isTouchOnHandle(x: Float, y: Float): Boolean {
        val childView = findChildViewUnder(x, y) ?: return false
        val handleView = childView.findViewById<View>(handleImageViewId) ?: return false

        val rvLocation = IntArray(2)
        val handleLocation = IntArray(2)

        this.getLocationOnScreen(rvLocation)
        handleView.getLocationOnScreen(handleLocation)

        val handleRect = Rect(
            handleLocation[0] - rvLocation[0],
            handleLocation[1] - rvLocation[1],
            handleLocation[0] - rvLocation[0] + handleView.width,
            handleLocation[1] - rvLocation[1] + handleView.height
        )

        return handleRect.contains(x.toInt(), y.toInt())
    }

    fun getHandleViewAt(x: Float, y: Float): ImageView? {
        val childView = findChildViewUnder(x, y) ?: return null
        val handleView = childView.findViewById<View>(handleImageViewId)

        return handleView as? ImageView
    }

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        onChildAttachedToWindow?.invoke(child)
    }

    // 当View被移除时确保清理Handler避免内存泄漏
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        longTouchHandler.removeCallbacks(longTouchRunnable)
    }
}