package com.example.test.activity.touch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton

/**
 * @author: chengwen
 * @date: 2023/6/1
 * @desc: 自定义Button，重写触摸事件方法，用于研究事件分发机制
 */
class CustomTouchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private val TAG = "CustomTouchButton"
    private var buttonName: String = "未命名按钮"

    fun setButtonName(name: String) {
        buttonName = name
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "[$buttonName] dispatchTouchEvent: ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "[$buttonName] dispatchTouchEvent: ACTION_MOVE x=${event.x}, y=${event.y}")
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "[$buttonName] dispatchTouchEvent: ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "[$buttonName] dispatchTouchEvent: ACTION_CANCEL")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "[$buttonName] onTouchEvent: ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "[$buttonName] onTouchEvent: ACTION_MOVE x=${event.x}, y=${event.y}")
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "[$buttonName] onTouchEvent: ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "[$buttonName] onTouchEvent: ACTION_CANCEL")
            }
        }
        return super.onTouchEvent(event)
    }
}