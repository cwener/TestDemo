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
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.test.activity.ApplicationWrapper
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

fun RecyclerView.smoothScrollToPositionWithOffset(position: Int, offset: Int = 100, orientation: Int = RecyclerView.HORIZONTAL, onScrolled: ((dx: Int) -> Unit)? = null, onScrollFinished: (() -> Unit)? = null) {
    val vh = findViewHolderForAdapterPosition(position)
    vh ?: return
    if ((orientation == RecyclerView.HORIZONTAL && vh.itemView.left == offset) || (orientation == RecyclerView.VERTICAL && vh.itemView.top == offset) || !canScrollHorizontally(-1)) {
        // 当前已经在目标位置和距离
        onScrollFinished?.invoke()
        return
    }
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
            val childStart = if (orientation == RecyclerView.HORIZONTAL) targetView.left else targetView.top
            val parentStart = if (orientation == RecyclerView.HORIZONTAL) layoutManager.paddingLeft else layoutManager.paddingTop
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

/**
 * 计算子View相对于RecyclerView左边缘的距离
 *
 * @param childView 需要计算位置的子View，可以是ItemView里的任意子View
 * @return 子View左边缘相对于RecyclerView左边缘的距离（单位：像素）
 */
fun RecyclerView.getChildViewLeftRelativeToRecyclerView(childView: View): Int {
    val offsetViewBounds = Rect()
    childView.getDrawingRect(offsetViewBounds)
    this.offsetDescendantRectToMyCoords(childView, offsetViewBounds)
    return offsetViewBounds.left
}

/**
 * 计算RecyclerView fling所需的初速度
 * @param distance 需要滚动的距离(像素)，正值表示向右滚动，负值表示向左滚动
 * @param timeMs 期望的滚动时间(毫秒)
 * @return 应用于fling方法的velocityX值，符号与distance一致
 */
fun calculateFlingVelocity(distance: Int, timeMs: Long): Int {
    // 保存距离符号，用于最后确定方向
    val direction = if (distance >= 0) 1 else -1

    // 使用距离的绝对值进行计算
    val absDistance = abs(distance)

    // RecyclerView内部使用的物理常量
    val gravity = 9.8f                   // 重力加速度 (m/s²)
    val inchesToMeters = 0.0254f         // 英寸到米的转换
    val ppi = ApplicationWrapper.instance.resources.displayMetrics.densityDpi.toFloat() // 屏幕像素密度
    val physicalPxPerMeter = ppi / inchesToMeters

    // ViewFlinger中使用的默认摩擦系数
    val friction = 0.015f                // 摩擦系数 (与RecyclerView默认值一致)

    // 计算减速率 (像素/秒²)
    val decelerationRate = gravity * friction * physicalPxPerMeter

    // 转换时间单位
    val timeInSeconds = timeMs / 1000f

    // 基于物理模型计算初速度绝对值
    val absInitialVelocity = absDistance / timeInSeconds + (decelerationRate * timeInSeconds) / 2

    // 应用校正因子 - 通过实测微调
    val correctionFactor = 1.1f

    // 恢复方向，返回带方向的速度值
    return (absInitialVelocity * correctionFactor * direction).toInt()
}