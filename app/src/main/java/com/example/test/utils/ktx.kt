package com.example.test.utils

/**
 * @author ChengWen
 * @date：2025/6/4 15:36
 * @desc：
 */

/**
 * 平滑滚动到指定位置，并使该位置距离RecyclerView左侧有指定的偏移量
 *
 * @param recyclerView RecyclerView实例
 * @param position 目标位置
 * @param offset 目标位置距离左侧的偏移量(像素)
 */
/**
 * RecyclerView扩展函数，平滑滚动到指定位置并设置偏移量
 *
 * @param position 目标位置
 * @param offset 目标位置距离左侧的偏移量(像素)
 */
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.random.Random

fun RecyclerView.smoothScrollToPositionWithOffset(position: Int, offset: Int = 100, onScrollStarted: (() -> Unit)? = null, onScrolled: ((dx: Int) -> Unit)? = null, onScrollFinished: (() -> Unit)? = null) {
    // 创建自定义的SmoothScroller
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        private val MILLISECONDS_PER_INCH = 25f // 控制滚动速度

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return (layoutManager as LinearLayoutManager).computeScrollVectorForPosition(targetPosition)
        }

        override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
            val layoutManager = layoutManager as LinearLayoutManager
            val childStart = targetView.left
            val parentStart = layoutManager.paddingLeft
            val dx = childStart - parentStart - offset
            val time = calculateTimeForDeceleration(dx.absoluteValue)
            if (time > 0) {
                action.update(dx, 0, time, mDecelerateInterpolator)
            }
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
        }
    }

    // 设置滚动监听器以检测滚动结束
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 滚动静止，调用回调
                onScrollFinished?.invoke()
                // 移除滚动监听器
                recyclerView.removeOnScrollListener(this)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScrolled?.invoke(dx)
            Log.d("smoothScrollToPositionWithOffset", "dx=$dx")
        }
    }

    // 添加滚动监听器
    this.addOnScrollListener(scrollListener)

    // 设置目标位置
    smoothScroller.targetPosition = position

    // 开始平滑滚动
    layoutManager?.startSmoothScroll(smoothScroller)
}


fun generateRandomHexColor(seed: Long, previousColor: String? = null): String {
    val random = Random(seed)
    var newColor: String

    // 定义一个生成随机颜色的方法
    fun generateColor(): String {
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        return String.format("#%02X%02X%02X", r, g, b)
    }

    // 如果没有之前的颜色，直接生成一个新的颜色
    if (previousColor == null) {
        newColor = generateColor()
    } else {
        // 否则，确保生成的颜色与之前的颜色不同
        do {
            newColor = generateColor()
        } while (newColor == previousColor)
    }

    return newColor
}