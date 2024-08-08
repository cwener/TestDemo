package com.example.test.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.test.R

/**
 * @author chengwen
 * @createTime 2024/7/15
 **/
class DSLNotificationDialog(context: Context, themeResId: Int) :
    Dialog(context) {
    private var mStartY: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes.dimAmount = 0f
        }
        setCanceledOnTouchOutside(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_memory)
        findViewById<View>(R.id.container)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isOutOfBounds(event)) {
                    mStartY = event.y
                }
            }

            MotionEvent.ACTION_UP -> {
//                if (mStartY > 0 && isOutOfBounds(event)) {
//                    val moveY = event.y
//                    if (abs(mStartY - moveY) >= 200) {  //滑动超过20认定为滑动事件
//                        //Dialog消失
//                        dismiss()
//                    } else {                //认定为点击事件
//                        //Dialog的点击事件
////                        mListener?.onClick()
//                    }
//                }
            }
        }
        return false
    }

    /**
     * 点击是否在范围外
     */
    private fun isOutOfBounds(event: MotionEvent): Boolean {
//        val yValue = event.y
//        if (yValue > 0 && yValue <= (mHeight ?: (0 + 40))) {
//            return true
//        }
//        return false
        return true
    }
}