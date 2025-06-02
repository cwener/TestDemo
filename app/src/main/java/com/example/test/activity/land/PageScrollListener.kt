package com.example.test.activity.land

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PageScrollListener(
    private val adapter: MusicAdapter,
//    private val animator: PageTransitionAnimator
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // 处理实时滑动效果
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

            if (currentPosition != RecyclerView.NO_POSITION && currentPosition != adapter.currentPosition) {
                // 位置变化了，处理动画
                adapter.currentPosition = currentPosition
                adapter.onCurrentPositionChanged(recyclerView)
            }
        }
    }
}