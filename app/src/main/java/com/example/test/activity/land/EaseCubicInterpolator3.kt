package com.example.test.activity.land

import android.animation.TimeInterpolator
import kotlin.math.pow

class EaseCubicInterpolator3(
    private val p1x: Float,
    private val p1y: Float,
    private val p2x: Float,
    private val p2y: Float
) : TimeInterpolator {

    companion object {
        // 预定义的一些常见贝塞尔曲线
        val EASE = EaseCubicInterpolator3(0.25f, 0.1f, 0.25f, 1f)
        val EASE_IN = EaseCubicInterpolator3(0.42f, 0f, 1f, 1f)
        val EASE_OUT = EaseCubicInterpolator3(0f, 0f, 0.58f, 1f)
        val EASE_IN_OUT = EaseCubicInterpolator3(0.42f, 0f, 0.58f, 1f)
    }

    init {
        // 确保控制点在[0,1]范围内
        require(p1x in 0f..1f && p1y in 0f..1f && p2x in 0f..1f && p2y in 0f..1f) {
            "Bezier control points must be in [0,1] range"
        }
    }

    override fun getInterpolation(input: Float): Float {
        return getBezierCoordinateY(getXForTime(input))
    }

    /**
     * 计算给定时间t对应的x坐标
     */
    private fun getXForTime(time: Float): Float {
        // 使用牛顿迭代法求解t值
        var t = time
        for (i in 0 until 4) {
            val currentSlope = getSlope(t)
            if (currentSlope == 0f) return t
            val currentX = getBezierCoordinateX(t) - time
            t -= currentX / currentSlope
        }
        return t
    }

    /**
     * 计算贝塞尔曲线在t时刻的x坐标
     */
    private fun getBezierCoordinateX(t: Float): Float {
        // 三阶贝塞尔曲线公式: (1-t)^3 * p0x + 3*(1-t)^2 * t * p1x + 3*(1-t)*t^2 * p2x + t^3 * p3x
        // 这里p0x=0, p3x=1
        return 3 * (1 - t).pow(2) * t * p1x + 3 * (1 - t) * t.pow(2) * p2x + t.pow(3)
    }

    /**
     * 计算贝塞尔曲线在t时刻的y坐标
     */
    private fun getBezierCoordinateY(t: Float): Float {
        // 三阶贝塞尔曲线公式: (1-t)^3 * p0y + 3*(1-t)^2 * t * p1y + 3*(1-t)*t^2 * p2y + t^3 * p3y
        // 这里p0y=0, p3y=1
        return 3 * (1 - t).pow(2) * t * p1y + 3 * (1 - t) * t.pow(2) * p2y + t.pow(3)
    }

    /**
     * 计算贝塞尔曲线在t时刻的斜率(导数)
     */
    private fun getSlope(t: Float): Float {
        // 三阶贝塞尔曲线的导数公式
        return 3 * (1 - t).pow(2) * p1x + 6 * (1 - t) * t * (p2x - p1x) + 3 * t.pow(2) * (1 - p2x)
    }
}