package com.example.test.activity.land

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sqrt

/**
 * @author ChengWen
 * @date：2025/6/20 14:01
 * @desc：弹簧效果的插值器
 */
class HorizontalSpringScroller(
    context: Context,
    private val targetOffset: Int,  // 目标水平偏移量
    private val stiffness: Float = 154f,
    private val dampingRatio: Float = 0.93f
) : LinearSmoothScroller(context) {

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        // 只计算水平滚动距离
        val dx = calculateDtToFit(
            targetView.left,
            targetView.right,
            targetOffset,
            targetOffset + targetView.width,
            SNAP_TO_START
        )

        if (dx != 0) {
            val distance = abs(dx)
            val duration = calculateTimeForDeceleration(distance)

            action.update(
                -dx,
                0,  // dy设为0，不处理垂直滚动
                duration,
                HighDampingSpringInterpolator(stiffness, dampingRatio)
            )
        }
    }

    // 精确定位逻辑
    override fun calculateDtToFit(
        viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
    ): Int = targetOffset - viewStart

    // 控制滚动速度
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return 100f / displayMetrics.densityDpi  // 比之前稍快
    }

    override fun calculateTimeForScrolling(dx: Int): Int {
        val res = 100
        Log.d("HorizontalSpringScroller", "calculateTimeForScrolling=$res")
        return res
    }

    // 只处理水平滚动
    override fun getVerticalSnapPreference(): Int = SNAP_TO_START
    override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
}

// 正常吸附插值器
class AttachSmoothScroller(context: Context, private val targetOffset: Int) : LinearSmoothScroller(context) {
    override fun calculateDtToFit(
        viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
    ): Int = targetOffset - viewStart

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return 100f / displayMetrics.densityDpi
    }

    override fun getHorizontalSnapPreference(): Int = SNAP_TO_ANY
}


class HighDampingSpringInterpolator(
    private val stiffness: Float = 154f,
    private val dampingRatio: Float = 0.93f
) : Interpolator {

    // 转换 stiffness 为频率参数
    private val frequency = 0.1f + (stiffness / 1000f) * 0.9f

    override fun getInterpolation(input: Float): Float {
        return if (dampingRatio >= 1f) {
            // 过阻尼情况 - 指数衰减曲线
            1f - exp(-3f * input)
        } else {
            // 阻尼振荡计算
            val omega = frequency * PI.toFloat() * 2f
            val dampedOmega = omega * sqrt(1f - dampingRatio * dampingRatio)

            val clampedInput = min(input, 1.5f) // 限制输入范围

            val decay = exp(-dampingRatio * omega * clampedInput)
            val oscillation = if (dampingRatio > 0.8f) {
                1f - dampingRatio * clampedInput // 高阻尼简化计算
            } else {
                cos(dampedOmega * clampedInput)
            }

            // 归一化结果
            1f - abs(decay * oscillation)
        }
    }

    // 可选：添加参数验证
    init {
        require(stiffness > 0) { "Stiffness must be positive" }
        require(dampingRatio >= 0) { "Damping ratio cannot be negative" }
    }
}


class SpringSmoothScroller(
    context: Context,
    val recyclerView: RecyclerView,
    private val stiffness: Float,
    private val dampingRatio: Float,
    private val targetOffset: Int
) : LinearSmoothScroller(context) {

    private var lastVelocity = 0f

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        val targetLeft = recyclerView.layoutManager?.getDecoratedLeft(targetView) ?: 0
        val distanceToTarget = targetOffset - targetLeft

        // 计算动画持续时间（基于距离和速度的估算）
        val time = calculateTimeForDeceleration(abs(distanceToTarget))

        // 创建并启动弹簧动画
        SpringAnimation(recyclerView, SpringAnimation.SCROLL_X).apply {
            setSpring(
                SpringForce()
                    .setFinalPosition(0f) // 最终位置为0，因为我们会通过scrollBy来移动
                    .setStiffness(stiffness)
                    .setDampingRatio(dampingRatio)
            )
            setStartVelocity(lastVelocity)
            addUpdateListener { _, value, _ ->
                // 根据弹簧动画的当前值滚动RecyclerView
                recyclerView.scrollBy((value * distanceToTarget).toInt(), 0)
            }
            start()
        }

        // 通知系统动画将在指定时间内完成
        if (time > 0) {
            action.update(-distanceToTarget, 0, time, mDecelerateInterpolator)
        }
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        return targetOffset - viewStart
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        // 返回一个较大的值，让默认滚动更快，然后由弹簧动画接管
        return 100f / displayMetrics.densityDpi
    }

    override fun onSeekTargetStep(
        dx: Int,
        dy: Int,
        state: RecyclerView.State,
        action: Action
    ) {
        // 保存当前速度，用于弹簧动画的初始速度
        lastVelocity = if (dx != 0) dx.toFloat() / 10f else 0f
        super.onSeekTargetStep(dx, dy, state, action)
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_ANY
    }
}