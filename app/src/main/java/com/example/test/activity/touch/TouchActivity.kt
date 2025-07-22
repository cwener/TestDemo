package com.example.test.activity.touch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.fragment.app.FragmentActivity
import com.example.test.databinding.ActivityTouchBinding

/**
 * @author: chengwen
 * @date: 2023/6/1
 * @desc: 触摸事件分发测试Activity
 */
class TouchActivity : FragmentActivity() {

    private val TAG = "TouchActivity"
    private lateinit var binding: ActivityTouchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouchBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // 设置按钮名称，用于日志输出时区分
        binding.button1.setButtonName("按钮1")
        binding.button2.setButtonName("按钮2")

        // 设置点击监听器
        binding.button1.setOnClickListener {
            Log.d(TAG, "按钮1被点击")
        }

        binding.button2.setOnClickListener {
            Log.d(TAG, "按钮2被点击")
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "Activity dispatchTouchEvent: ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "Activity dispatchTouchEvent: ACTION_MOVE x=${event.x}, y=${event.y}")
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "Activity dispatchTouchEvent: ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "Activity dispatchTouchEvent: ACTION_CANCEL")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "Activity onTouchEvent: ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "Activity onTouchEvent: ACTION_MOVE x=${event.x}, y=${event.y}")
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "Activity onTouchEvent: ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "Activity onTouchEvent: ACTION_CANCEL")
            }
        }
        return super.onTouchEvent(event)
    }
}