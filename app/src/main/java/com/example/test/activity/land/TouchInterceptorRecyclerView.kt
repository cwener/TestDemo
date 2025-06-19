package com.example.test.activity.land

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class TouchInterceptorRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var handleImageViewId: Int = 0
    private var interceptedByHandle: Boolean = false

    var onChildAttachedToWindow: ((View) -> Unit)? = null

    fun setHandleImageViewId(imageViewId: Int) {
        this.handleImageViewId = imageViewId
    }


    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            interceptedByHandle = isTouchOnHandle(e.x, e.y)

            if (!interceptedByHandle) {
                return false
            }
        }

        return if (interceptedByHandle) super.onInterceptTouchEvent(e) else false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return if (interceptedByHandle) super.onTouchEvent(e) else false
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        // 修改滑动速度
        val tmp = velocityX * 0.5f
        return super.fling(tmp.toInt(), velocityY);
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
}