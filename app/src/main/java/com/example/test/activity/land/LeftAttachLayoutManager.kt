package com.example.test.activity.land

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class DynamicAttachLayoutManager(
    context: Context,
    private val attachDistancePx: Int = 100
) : LinearLayoutManager(context, HORIZONTAL, false) {

    private var isAttachEnabled = false
    private val targetOffset = attachDistancePx
    private var recyclerViewRef: RecyclerView? = null

    // 平滑滚动器缓存
    private val attachScroller by lazy { AttachSmoothScroller(context) }

    /**
     * 动态设置是否启用吸附功能
     */
    fun setAttachEnabled(enabled: Boolean) {
        if (isAttachEnabled != enabled) {
            isAttachEnabled = enabled
            // 立即执行吸附检查（如果从非吸附状态切换到吸附状态）
            if (enabled) {
                recyclerViewRef?.post {
                    if (recyclerViewRef?.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        snapToTargetPosition()
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        recyclerViewRef = view
    }

    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)
        recyclerViewRef = null
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (isAttachEnabled && state == RecyclerView.SCROLL_STATE_IDLE) {
            snapToTargetPosition()
        }
    }

    private fun snapToTargetPosition() {
        if (!isAttachEnabled) return

        val recyclerView = recyclerViewRef ?: return
        val closestChild = findClosestChildToPosition() ?: return

        val closestPosition = getPosition(closestChild)
        val closestLeft = getDecoratedLeft(closestChild)
        val offsetToTarget = targetOffset - closestLeft

        if (abs(offsetToTarget) > 10) {
            recyclerView.smoothScrollToPosition(closestPosition)
        }
    }

    private fun findClosestChildToPosition(): View? {
        if (childCount == 0) return null

        var closestChild: View? = null
        var closestDistance = Int.MAX_VALUE

        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childLeft = getDecoratedLeft(child)
            val distance = abs(childLeft - targetOffset)

            if (distance < closestDistance) {
                closestDistance = distance
                closestChild = child
            }
        }

        return closestChild
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        if (isAttachEnabled) {
            attachScroller.targetPosition = position
            startSmoothScroll(attachScroller)
        } else {
            super.smoothScrollToPosition(recyclerView, state, position)
        }
    }

    private inner class AttachSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
        ): Int = targetOffset - viewStart

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 100f / displayMetrics.densityDpi
        }

        override fun getHorizontalSnapPreference(): Int = SNAP_TO_ANY
    }
}