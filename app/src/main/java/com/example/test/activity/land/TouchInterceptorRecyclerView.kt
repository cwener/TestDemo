package com.example.test.activity.land

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TouchInterceptorRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var onChildAttachedToWindow: ((View) -> Unit)? = null

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return  super.onInterceptTouchEvent(e)
    }

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        onChildAttachedToWindow?.invoke(child)
    }
}