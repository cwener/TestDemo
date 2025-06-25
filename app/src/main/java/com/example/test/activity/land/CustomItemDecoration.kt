package com.example.test.activity.land

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author ChengWen
 * @date：2025/6/24 19:02
 * @desc：
 */
class CustomItemDecoration() : RecyclerView.ItemDecoration() {
    // 存储特定位置的间距
    private val specialSpacingMap = mutableMapOf<Int, Pair<Int, Int>>()
    private val defaultSpacing = Pair(0, 0) // 默认间距

    fun setSpecialSpacing(position: Int, leftSpacing: Int, rightSpacing: Int) {
        specialSpacingMap[position] = Pair(leftSpacing, rightSpacing)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        // 为Item设置左右间距
        val pair = specialSpacingMap[position] ?: defaultSpacing
        outRect.left = pair.first
        outRect.right = pair.second

        // 确保第一个item左侧无间距
        if (position == 0) {
            outRect.left = 0
        }
    }
}