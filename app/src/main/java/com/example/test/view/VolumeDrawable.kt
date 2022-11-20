package com.example.test.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.test.R
import com.example.test.activity.ApplicationWrapper
import com.example.test.utils.dp

/**
 * @author: chengwen
 * @date: 2022/11/20
 */
class VolumeDrawable : Drawable() {

    companion object {
        private const val TIME_FAST = 280
        private const val TIME_MIDDLE = 500
        private const val TIME_SLOW = 1000
        private const val TAG = "VolumeDrawable"
    }

    var volume = 0f
        set(value) {
            field = value
            anim(value)
        }
    var animationListener: AnimatorListenerAdapter? = null
        set(value) {
            field = value
            animation.addListener(value)
        }
    private var currentTime = TIME_SLOW
    private var baseRatio = 0.72f
    private var leftBaseRatio = 1 - baseRatio
    private var currentRadius = baseRatio

    //渐变颜色
    private val mRightGradientColors = intArrayOf(
        ApplicationWrapper.instance.getColor(R.color.white_25),
        0x00ffffff,
        0x00ffffff,
        ApplicationWrapper.instance.getColor(R.color.white_25)
    )

    //渐变位置
    private val mRightGradientPosition = floatArrayOf(0f, 0.2f, 0.8f, 1f)
    private var rightShader: SweepGradient? = null

    private var leftShader: SweepGradient? = null

    //渐变颜色
    private val mLeftGradientColors = intArrayOf(
        0x00ffffff,
        ApplicationWrapper.instance.getColor(R.color.white_25),
        ApplicationWrapper.instance.getColor(R.color.white_25),
        0x00ffffff
    )

    //渐变位置
    private val mLeftGradientPosition = floatArrayOf(0.3f, 0.5f, 0.5f, 0.7f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 7.dp().toFloat()
    }

    private val animation = ValueAnimator.ofInt(0, TIME_SLOW)

    init {
        animation.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {

                }
            })
            addUpdateListener {
                val time = it.animatedValue as Int
                val ratio = (time.toFloat() / currentTime)
                paint.alpha = ((1 - ratio) * 255).toInt()
                currentRadius = baseRatio + (ratio * leftBaseRatio)
                Log.d(TAG, "time = $time, alpha = ${paint.alpha}, radius = $currentRadius")
                invalidateSelf()
            }
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.apply {
            rightShader = SweepGradient(
                (width() / 2).toFloat(),
                (height() / 2).toFloat(),
                mRightGradientColors,
                mRightGradientPosition
            )
            leftShader = SweepGradient(
                (width() / 2).toFloat(),
                (height() / 2).toFloat(),
                mLeftGradientColors,
                mLeftGradientPosition
            )
        }
    }

    override fun draw(canvas: Canvas) {
        paint.shader = rightShader
        canvas.drawCircle(
            (bounds.width() / 2).toFloat(),
            (bounds.height() / 2).toFloat(),
            (bounds.width() / 2) * currentRadius,
            paint
        )
        paint.shader = leftShader
        canvas.drawCircle(
            (bounds.width() / 2).toFloat(),
            (bounds.height() / 2).toFloat(),
            (bounds.width() / 2) * currentRadius,
            paint
        )
    }

    private fun anim(volume: Float) {
        if (animation.isRunning) {
            return
        }
        currentTime = when {
            volume < 0.3 -> {
                TIME_SLOW
            }
            volume < 0.6 -> {
                TIME_MIDDLE
            }
            else -> {
                TIME_FAST
            }
        }
        animation.duration = currentTime.toLong()
        animation.setIntValues(0, currentTime)
        animation.start()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}