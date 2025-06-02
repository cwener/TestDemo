package com.example.test.activity.land

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max

class CustomPagerSnapHelper : PagerSnapHelper() {

    companion object {
        const val TAG = "CustomPagerSnapHelper"
    }

    private var adapter: MusicAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var lastPosition = RecyclerView.NO_POSITION

    fun setAdapter(adapter: MusicAdapter) {
        this.adapter = adapter
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val  res = super.calculateDistanceToFinalSnap(layoutManager, targetView)
        val pos = recyclerView?.getChildAdapterPosition(targetView)
        Log.d(TAG, "calculateDistanceToFinalSnap res = ${res.contentToString()}, targetView pos = $pos, adapter curPos=${adapter?.currentPosition}")
        return res
    }

    override fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray? {
        val res = super.calculateScrollDistance(velocityX, velocityY)
        Log.i(TAG, "calculateScrollDistance res = ${res.contentToString()}, adapter curPos=${adapter?.currentPosition}")
        return res
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val targetPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        Log.d("CustomPagerSnapHelper", "findTargetSnapPosition=$targetPosition")
        // 处理滑动方向和当前位置关系
        if (targetPosition != RecyclerView.NO_POSITION && targetPosition != lastPosition) {
            adapter?.currentPosition = targetPosition
            lastPosition = targetPosition
        }

        return targetPosition
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        Log.d("CustomPagerSnapHelper", "onFling, velocityX=$velocityX")
        // 确保只滑动一页
        return super.onFling(velocityX, velocityY)
    }

    override fun createSnapScroller(layoutManager: RecyclerView.LayoutManager?): LinearSmoothScroller? {
        if (recyclerView == null) return super.createSnapScroller(layoutManager)
        return object : LinearSmoothScroller(recyclerView?.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances = calculateDistanceToFinalSnap(recyclerView?.layoutManager!!, targetView)
                val dx = snapDistances!![0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(max(abs(dx.toDouble()), abs(dy.toDouble())).toInt())
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                    Log.d(TAG, "onTargetFound, time=$time, snapDistances=${snapDistances.contentToString()}, dx=$dx, adapter pos= ${adapter?.currentPosition}")
                    adapter?.onTargetFound(dx, time)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                val res = 100f / displayMetrics.densityDpi
                Log.d(TAG, "calculateSpeedPerPixel, time=$res, adapter pos= ${adapter?.currentPosition}")
                return res
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                val res = max(100.toDouble(), super.calculateTimeForScrolling(dx).toDouble()).toInt()
                Log.d(TAG, "calculateTimeForScrolling, time=$res, adapter pos= ${adapter?.currentPosition}")
                return res
            }

            override fun calculateTimeForDeceleration(dx: Int): Int {
                return 1000
            }
        }
    }
}