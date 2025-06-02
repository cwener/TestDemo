package com.example.test.activity.land

import android.util.Log
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CustomPagerSnapHelper : PagerSnapHelper() {

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
//            recyclerView?.let {
//                adapter?.onCurrentPositionChanged(it)
//            }
            lastPosition = targetPosition
        }

        return targetPosition
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        // 确保只滑动一页
        return super.onFling(velocityX, velocityY)
    }
}