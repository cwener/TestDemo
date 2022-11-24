package com.example.test.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.test.R
import com.example.test.activity.ApplicationWrapper

/**
 * @author: chengwen
 * @date: 2022/11/23
 */
class GoRoomCountDownDrawable: Drawable() {

    var total = -1
    set(value) {
        field = value
        animation.setIntValues(0, value)
        animation.duration = value.toLong()
        animation.start()
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val colorArray = intArrayOf(ApplicationWrapper.instance.getColor(R.color.white_30), ApplicationWrapper.instance.getColor(R.color.white_10))
    private val positionArray = floatArrayOf(0f, 1f)
    private val animation = ValueAnimator.ofInt().apply {
        addUpdateListener(object :ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(it: ValueAnimator) {
                val tmp = it.animatedValue as Int
                val progress = if (total == 0) {
                    0f
                } else {
                    tmp.toFloat() / total
                }
                positionArray[0] = progress
                positionArray[1] = progress
                Log.d("GoRoomCountDownDrawable", "progress = ${positionArray[0]}, ${positionArray[1]}")
                paint.shader = LinearGradient(0f, 0f, bounds.width().toFloat(), 0f, colorArray, positionArray, Shader.TileMode.CLAMP)
                invalidateSelf()
            }
        })
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {

            }
        })
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat(), bounds.height() / 2f, bounds.height() / 2f, paint)

    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}