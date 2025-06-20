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
    private val maxFlingItems: Int = 10,
    val context: Context
) : SnapHelper() {

    private var recyclerView: RecyclerView? = null
    private val decelerateInterpolator = DecelerateInterpolator(3f)
    private val minFlingVelocity = 200 // 最小触发滑动的速度(dp/s)
    private var scroller: SnapScroller? = null
    private val maxVelocity = 40000f

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        scroller = SnapScroller(context)
    }

    private inner class SnapScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 50f / displayMetrics.densityDpi
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
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = targetView.left - leftOffsetPx
        }
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager !is LinearLayoutManager ||
            !layoutManager.canScrollHorizontally()) {
            return null
        }
        return findClosestViewToLeftOffset(layoutManager)
    }

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

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is LinearLayoutManager ||
            !layoutManager.canScrollHorizontally()) {
            return RecyclerView.NO_POSITION
        }

        val itemCount = layoutManager.itemCount
        if (itemCount == 0) return RecyclerView.NO_POSITION

        if (abs(velocityX) < minFlingVelocity) {
            return RecyclerView.NO_POSITION
        }

        val closestView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(closestView)

        val absVelocity = abs(velocityX).toFloat()
        val maxVelocity = maxVelocity
        val normalizedVelocity = (absVelocity / maxVelocity).coerceIn(0f, 1f)
        val interpolated = decelerateInterpolator.getInterpolation(normalizedVelocity)
        val logScaled = ln(interpolated * 9 + 1) / ln(10f)
        val scrollDistance = (logScaled * maxFlingItems).toInt() *
                if (velocityX > 0) 1 else -1

        var targetPosition = currentPosition + scrollDistance
        targetPosition = targetPosition.coerceIn(0, itemCount - 1)

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