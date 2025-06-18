package com.example.test.activity.land

import android.view.animation.Interpolator

/**
 * 三阶贝塞尔曲线插值器
 *
 * @param x1 第一个控制点的 x 坐标
 * @param y1 第一个控制点的 y 坐标
 * @param x2 第二个控制点的 x 坐标
 * @param y2 第二个控制点的 y 坐标
 */
class EaseCubicInterpolator(
    private val x1: Float,
    private val y1: Float,
    private val x2: Float,
    private val y2: Float
) : Interpolator {

    // 精度
    private val PRECISION = 0.001f

    override fun getInterpolation(input: Float): Float {
        // 快速处理边界情况
        if (input <= 0f) return 0f
        if (input >= 1f) return 1f

        // 使用二分法查找对应的 t 值
        var t = input
        var start = 0f
        var end = 1f

        while (true) {
            // 计算当前 t 对应的 x 值
            val x = calculateBezierPoint(t, 0f, x1, x2, 1f)

            // 如果精度足够，则返回对应的 y 值
            if (Math.abs(x - input) < PRECISION) {
                return calculateBezierPoint(t, 0f, y1, y2, 1f)
            }

            // 二分法调整 t
            if (x < input) {
                start = t
                t = (t + end) / 2
            } else {
                end = t
                t = (start + t) / 2
            }
        }
    }

    /**
     * 计算贝塞尔曲线上的点
     *
     * @param t 参数 t，取值范围 [0,1]
     * @param p0 起点
     * @param p1 第一个控制点
     * @param p2 第二个控制点
     * @param p3 终点
     * @return 曲线上 t 位置的值
     */
    private fun calculateBezierPoint(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val u = 1 - t
        val tt = t * t
        val uu = u * u
        val uuu = uu * u
        val ttt = tt * t

        // 三阶贝塞尔曲线公式: (1-t)³P₀ + 3(1-t)²tP₁ + 3(1-t)t²P₂ + t³P₃
        return uuu * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * p3
    }

    companion object {
        /**
         * 常用的贝塞尔曲线预设
         */
        // 线性
        val LINEAR = EaseCubicInterpolator(0.0f, 0.0f, 1.0f, 1.0f)
        // 标准减速
        val EASE_OUT = EaseCubicInterpolator(0.0f, 0.0f, 0.58f, 1.0f)
        // 标准加速
        val EASE_IN = EaseCubicInterpolator(0.42f, 0.0f, 1.0f, 1.0f)
        // 标准加速减速
        val EASE_IN_OUT = EaseCubicInterpolator(0.42f, 0.0f, 0.58f, 1.0f)
        // 快速加速减速
        val EASE = EaseCubicInterpolator(0.25f, 0.1f, 0.25f, 1.0f)
    }
}