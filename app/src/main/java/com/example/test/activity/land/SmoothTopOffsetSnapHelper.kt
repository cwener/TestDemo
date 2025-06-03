package com.example.test.activity.land

import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 自定义左侧偏移SnapHelper
 * - 不会在初始布局时自动调整位置
 * - 支持更长距离的惯性滑动
 * - 滑动停止时，确保有一个ViewHolder位于左侧指定距离
 */
class LeftOffsetSnapHelper(
    private val leftOffsetPx: Int
) : LinearSnapHelper() {

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager is LinearLayoutManager &&
            layoutManager.orientation == RecyclerView.HORIZONTAL) {
            out[0] = targetView.left - leftOffsetPx
        }
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager !is LinearLayoutManager ||
            layoutManager.orientation != RecyclerView.HORIZONTAL) {
            return null
        }

        return findClosestViewToLeftOffset(layoutManager)
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is LinearLayoutManager ||
            layoutManager.orientation != RecyclerView.HORIZONTAL) {
            return RecyclerView.NO_POSITION
        }

        val itemCount = layoutManager.itemCount
        if (itemCount == 0) return RecyclerView.NO_POSITION

        // 在滑动方向上查找目标视图
        val isForwardDirection = velocityX > 0

        // 根据滑动速度决定滚动的距离（项数）
        val scrollDistance = calculateScrollDistance(velocityX)

        // 获取当前对齐的位置
        val closestView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(closestView)

        // 计算目标位置
        val targetPosition = if (isForwardDirection) {
            (currentPosition + scrollDistance).coerceAtMost(itemCount - 1)
        } else {
            (currentPosition - scrollDistance).coerceAtLeast(0)
        }

        return targetPosition
    }

    /**
     * 查找离指定左侧偏移最近的视图
     */
    private fun findClosestViewToLeftOffset(layoutManager: LinearLayoutManager): View? {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return null

        var closestView: View? = null
        var minDistance = Int.MAX_VALUE

        for (i in firstVisible..lastVisible) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            val distance = abs(view.left - leftOffsetPx)

            if (distance < minDistance) {
                minDistance = distance
                closestView = view
            }
        }

        return closestView
    }

    /**
     * 根据滑动速度计算应该滚动的项数
     */
    private fun calculateScrollDistance(velocity: Int): Int {
        // 滑动速度绝对值
        val absVelocity = abs(velocity)

        // 基于速度计算滚动项数
        return when {
            absVelocity > 15000 -> 6    // 非常快的滑动
            absVelocity > 10000 -> 4    // 快速滑动
            absVelocity > 8000 -> 3
            absVelocity > 4000 -> 2    // 中等速度滑动
            absVelocity > 1000 -> 1    // 慢速滑动
            else -> 1                  // 默认滚动1项
        }
    }

    private fun dpToPx(dp: Int, recyclerView: RecyclerView): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            recyclerView.context.resources.displayMetrics
        ).toInt()
    }
}