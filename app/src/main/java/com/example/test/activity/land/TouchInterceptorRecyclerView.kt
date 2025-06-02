package com.example.test.activity.land

import com.example.test.activity.land.MusicAdapter.MusicViewHolder
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class TouchInterceptorRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var activeImageView: ImageView? = null
    private val tempRect = Rect()

//    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
//        // 检查点击位置是否在当前可见的某个ImageView上
//        val x = e.x
//        val y = e.y
//
//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//            val holder = getChildViewHolder(child) as? MusicViewHolder ?: continue
//
//            // 检查点击位置是否在当前ViewHolder的特定ImageView上
//            val imageView = holder.imgCover
//            imageView.getGlobalVisibleRect(tempRect)
//            offsetDescendantRectToMyCoords(imageView, tempRect)
//
//            if (tempRect.contains(x.toInt(), y.toInt())) {
//                activeImageView = imageView
//                return super.onInterceptTouchEvent(e)
//            }
//        }
//
//        // 如果不在任何ImageView上，则不拦截触摸事件
//        activeImageView = null
//        return false
//    }
//
//    override fun onTouchEvent(e: MotionEvent): Boolean {
//        // 只有在activeImageView不为null时处理触摸事件
//        return if (activeImageView != null) {
//            super.onTouchEvent(e)
//        } else {
//            // 将事件传递给下层View
//            false
//        }
//    }
}