package com.example.test.activity.land

import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.test.R
import com.example.test.databinding.ItemMusicBinding
import com.example.test.utils.DimensionUtils
import com.facebook.drawee.view.SimpleDraweeView

class MusicAdapter(val recyclerView: RecyclerView, private val onItemClick: (MusicInfo, Int) -> Unit) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    companion object {
        val LeftEdgeWidth = DimensionUtils.dpToPx(100f)
        val LeftEdge0 = 0
        val LeftEdgeToRight = DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(300f)
        const val TAG = "MusicAdapter"
    }

    private val list = mutableListOf<MusicInfo>()
    var currentPosition: Int = 0
        get() = field
        set(value) {
            Log.i("MusicAdapter", "old = ${field}, currentPosition=$value")
            field = value
        }

    init {
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i(TAG, "onScrolled, IDLE")
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val currentPositionTmp = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (currentPositionTmp != RecyclerView.NO_POSITION && currentPositionTmp != currentPosition) {
                        // 位置变化了，处理动画
                        currentPosition = currentPositionTmp
                        resetLeftAndRight()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("MusicAdapt", "onScrolled, dx=$dx")
                resetLeftAndRight()
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        val binding = ItemMusicBinding.bind(view)
        return MusicViewHolder(binding, onItemClick)
    }

    fun setList(musics: List<MusicInfo>) {
        list.clear()
        list.addAll(musics)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private fun getItem(position: Int): MusicInfo {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun onTargetFound(dx: Int, time: Int) {
        if (dx == 0) {
            return
        }
        if (dx > 0) {
            // 手指向左滑，右侧的VH黑胶做margin从 0 到 LeftEdgeWidth 的位移动画
            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
            vh ?: return
            animateMarginStartWithSpring(vh.binding.imgCover, startMargin = LeftEdge0, targetMargin = LeftEdgeWidth, duration = time.toLong())
        } else {
            // 手指向右滑，左侧的VH黑胶做从margin从 LeftEdgeToRight 到 LeftEdgeWidth 的位移动画
            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
            vh ?: return
            animateMarginStartWithSpring(vh.binding.imgCover, startMargin = LeftEdgeToRight, targetMargin = LeftEdgeWidth, duration = time.toLong())
        }
    }


    fun resetLeftAndRight() {

        fun updateParams(view: View, marginLeft: Int, pos: Int) {
            val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
            var requestLayout = false
            if (params.leftMargin != marginLeft) {
                params.setMargins(marginLeft, 0, 0, 0)
                requestLayout = true
            }
            if (requestLayout) {
                view.requestLayout()
                Log.d(TAG, "updateParams pos=$pos, leftMargin=${params.leftMargin}, adapter curPos=${currentPosition}")
            }
        }

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val vh = recyclerView.getChildViewHolder(child) as MusicViewHolder
            val position = recyclerView.getChildAdapterPosition(child)
            if (position < currentPosition) {
                // 左侧
                updateParams(vh.binding.imgCover, LeftEdgeToRight, position)
            } else if (position == currentPosition) {
                // 当前
                updateParams(vh.binding.imgCover, LeftEdgeWidth, position)
            } else {
                // 右侧
                updateParams(vh.binding.imgCover, LeftEdge0, position)
            }
        }
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onItemClick: (MusicInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgCover: SimpleDraweeView = itemView.findViewById(R.id.imgCover)

        fun bind(music: MusicInfo, position: Int) {
            if (position % 2 == 0) {
                binding.root.setBackgroundColor("#66FCFCFF".toColorInt())
            } else {
                binding.root.setBackgroundColor("#66605A7C".toColorInt())
            }
            // 使用Fresco加载图片
            imgCover.setImageURI(music.coverUrl.toUri())
            imgCover.setOnClickListener {
                onItemClick.invoke(music, position)
            }
        }
    }

    fun animateMarginStartWithSpring(view: View, startMargin: Int, targetMargin: Int, duration: Long = 300, tension: Float = 1f, onEnd: (() -> Unit)? = null) {
        Log.d(TAG, "animateMarginStartWithSpring, adapter curPos=${currentPosition}")
        // 创建ValueAnimator
        ValueAnimator.ofInt(startMargin, targetMargin).apply {
            this.duration = duration

            // 使用OvershootInterpolator实现弹性效果
            interpolator = OvershootInterpolator(tension)

            // 在动画更新时修改margin
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    MarginLayoutParamsCompat.setMarginStart(this, animatedValue)
                }
            }

            // 动画结束回调
            if (onEnd != null) {
                doOnEnd { onEnd.invoke() }
            }

            // 开始动画
            start()
        }
    }
}