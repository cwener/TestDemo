package com.example.test.activity.land

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import kotlin.math.abs
import kotlin.math.ln

class NaturalSnapHelper(
    private val leftOffsetPx: Int = 0,
    private val maxFlingItems: Int = 5,
    context: Context
) : SnapHelper() {

    private var recyclerView: RecyclerView? = null
    private val decelerateInterpolator = DecelerateInterpolator(1.8f)  // 优化减速曲线
    private val minFlingVelocity: Int = dpToPx(context, 100)          // 合理的最小触发速度
    private val maxVelocity = 20000f                                  // 安全的速度上限
    private var scroller: SnapScroller? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView?.apply {
            scroller = SnapScroller(context)
        }
    }

    private inner class SnapScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 35f / displayMetrics.densityDpi  // 优化滚动速度
        }

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + leftOffsetPx - viewStart
        }

        override fun onTargetFound(
            targetView: View,
            state: RecyclerView.State,
            action: Action
        ) {
            val snapDistances = calculateDistanceToFinalSnap(
                recyclerView?.layoutManager!!,
                targetView
            )
            if (snapDistances[0] != 0 || snapDistances[1] != 0) {
                val dx = snapDistances[0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(maxOf(abs(dx), abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, decelerateInterpolator)
                }
            }
        }
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        return IntArray(2).apply {
            if (layoutManager.canScrollHorizontally()) {
                this[0] = targetView.left - leftOffsetPx
            }
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager !is LinearLayoutManager || !layoutManager.canScrollHorizontally()) {
            return null
        }
        return findClosestViewToLeftOffset(layoutManager)
    }

    private fun findClosestViewToLeftOffset(layoutManager: LinearLayoutManager): View? {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        if (firstVisible == RecyclerView.NO_POSITION) return null

        return (firstVisible..layoutManager.findLastVisibleItemPosition())
            .mapNotNull { layoutManager.findViewByPosition(it) }
            .minByOrNull { abs(it.left - leftOffsetPx) }
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is LinearLayoutManager || !layoutManager.canScrollHorizontally()) {
            return RecyclerView.NO_POSITION
        }

        val itemCount = layoutManager.itemCount
        if (itemCount == 0) return RecyclerView.NO_POSITION

        // 优化速度检测逻辑
        val absVelocity = abs(velocityX)
        if (absVelocity < minFlingVelocity) {
            return RecyclerView.NO_POSITION
        }

        val closestView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(closestView)

        // 优化速度映射计算
        val normalizedVelocity = (absVelocity.coerceAtMost(maxVelocity.toInt()) / maxVelocity)
            .coerceIn(0f, 1f)
        val interpolated = decelerateInterpolator.getInterpolation(normalizedVelocity)
        val scrollDistance = (ln(interpolated * 9 + 1) / ln(10f) * maxFlingItems).toInt() *
                if (velocityX > 0) 1 else -1

        val targetPosition = (currentPosition + scrollDistance)
            .coerceIn(0, itemCount - 1)

        // 使用平滑滚动
        scroller?.targetPosition = targetPosition
        layoutManager.startSmoothScroll(scroller)

        return targetPosition
    }

    companion object {
        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}