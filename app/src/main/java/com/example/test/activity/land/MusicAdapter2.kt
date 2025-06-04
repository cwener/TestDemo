package com.example.test.activity.land

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.test.R
import com.example.test.databinding.ItemMusicBinding
import com.example.test.utils.DimensionUtils
import com.example.test.utils.setMarginLeft
import com.example.test.utils.setMarginRight
import kotlin.math.abs

class MusicAdapter2(val recyclerView: TouchInterceptorRecyclerView, private val onItemClick: (MusicInfo, Int) -> Unit) : RecyclerView.Adapter<MusicAdapter2.MusicViewHolder>() {

    companion object {
        val BASIC_RIGHT_SPACE = DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(400f)
        val BASIC_LEFT_SPACE = DimensionUtils.dpToPx(100f)
        val BASIC_SPACE = DimensionUtils.dpToPx(10f)
        val BASIC_ITEM_WIDTH = DimensionUtils.dpToPx(300f)
        const val TAG = "MusicAdapter"

        const val TO_LEFT = 1
        const val TO_RIGHT = 2
        const val NONE_DIRECTION = 0
        const val INVALID_LEFT = -1
    }

    private val list = mutableListOf<MusicInfo>()
    var lastCurrentPosition = RecyclerView.NO_POSITION
    var currentPosition: Int = 0
        get() = field
        set(value) {
            lastCurrentPosition = field
            Log.i("MusicAdapter", "old = ${field}, currentPosition=$value")
            field = value
            recyclerView.invalidateItemDecorations()
        }
    var smoothDirection = NONE_DIRECTION
    var toLeftOriginLeft = -1

    val adapterItemClick : (MusicInfo, Int) -> Unit = { music, pos ->
        onItemClick.invoke(music, pos)
    }

    var interactiveStatus = ListState.SwitchMusic

    init {
        recyclerView.onChildAttachedToWindow = { child ->
            val position = recyclerView.getChildAdapterPosition(child)
            Log.d(TAG, "onChildAttachedToWindow, position=$position")
        }
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged=$newState")
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("MusicAdapt", "onScrolled, dx=$dx, smoothDirection=$smoothDirection")
                // smoothDirection 大于 0 时才是真正的改变了目标位置
                if (interactiveStatus == ListState.SwitchMusic) {
                    when(smoothDirection) {
                        TO_LEFT -> {
                            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
                            vh ?: return
                            val left = vh.binding.root.left
                            if (toLeftOriginLeft == INVALID_LEFT) {
                                toLeftOriginLeft = left
                            }
                            val margin = (BASIC_LEFT_SPACE * (1.0 - left.toFloat() / toLeftOriginLeft)).toInt()
                            vh.binding.imgCover.setMarginLeft(margin)
                            Log.d(TAG, "调整目标pos的marginLeft=$margin")
                        }
                        TO_RIGHT -> {
                            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
                            vh ?: return
                            val left = abs(vh.binding.root.left)
                            if (toLeftOriginLeft == INVALID_LEFT) {
                                toLeftOriginLeft = left
                            }
                            val margin = (BASIC_RIGHT_SPACE * (left.toFloat() / toLeftOriginLeft)).toInt() + BASIC_LEFT_SPACE
                            vh.binding.imgCover.setMarginLeft(margin)
                            Log.d(TAG, "调整目标pos的marginLeft=$margin")
                        }
                        else ->  {
                            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
                            vh?.let {
                                renderMargin(currentPosition, it.binding, currentPosition, RecyclerView.SCROLL_STATE_IDLE)
                            }

                            val pre = currentPosition - 1
                            val vhPre = recyclerView.findViewHolderForAdapterPosition(pre) as? MusicViewHolder
                            vhPre?.let {
                                renderMargin(position = pre, it.binding, curPosition = currentPosition, RecyclerView.SCROLL_STATE_IDLE)
                            }

                            val next = currentPosition + 1
                            val vhNext = recyclerView.findViewHolderForAdapterPosition(next) as? MusicViewHolder
                            vhNext?.let {
                                renderMargin(position = next, it.binding, curPosition = currentPosition, RecyclerView.SCROLL_STATE_IDLE)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        val binding = ItemMusicBinding.bind(view)
        return MusicViewHolder(binding, adapterItemClick)
    }

    fun setList(musics: List<MusicInfo>) {
        list.clear()
        list.addAll(musics)
        notifyDataSetChanged()
    }

    fun renderTransToList(animatedValue: Float) {
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val vh = recyclerView.findContainingViewHolder(child) as? MusicViewHolder
            vh ?: continue
            val position = recyclerView.getChildAdapterPosition(child)
            if (position == currentPosition) {
                val width = (DimensionUtils.getFullScreenWidth() * animatedValue).toInt()
                if (width < BASIC_ITEM_WIDTH) {
                    vh.binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                } else {
                    vh.binding.root.layoutParams.width = width
                }
                val leftMargin = (BASIC_LEFT_SPACE * animatedValue).toInt()
                vh.binding.imgCover.setMarginLeft(if (leftMargin < BASIC_SPACE) BASIC_SPACE else leftMargin)
                vh.binding.imgCover.setMarginRight(BASIC_SPACE)
                val scrollDistance = BASIC_LEFT_SPACE - leftMargin
                (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, scrollDistance)
                Log.d(TAG, "scrollDistance=${scrollDistance}")
            } else {
                if (vh.binding.root.width != BASIC_ITEM_WIDTH) {
                    vh.binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                }
                if (vh.binding.imgCover.marginLeft != BASIC_SPACE) {
                    vh.binding.imgCover.setMarginLeft(BASIC_SPACE)
                }
            }
        }
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
            smoothDirection= TO_LEFT
        } else {
            // 手指向右滑，左侧的VH黑胶做从margin从 LeftEdgeToRight 到 LeftEdgeWidth 的位移动画
            smoothDirection= TO_RIGHT
        }
    }

    // 这里才是列表真正的静止状态
    fun calculateDistanceToFinalSnap(res: IntArray?) {
        if (res?.get(0) == 0) {
            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
            vh ?: return
            renderMargin(currentPosition, vh.binding, currentPosition, RecyclerView.SCROLL_STATE_IDLE)
            smoothDirection = NONE_DIRECTION
            toLeftOriginLeft = INVALID_LEFT
        }
    }

    fun renderMargin(position: Int, binding: ItemMusicBinding, curPosition: Int, scrollState: Int) {
        if (interactiveStatus == ListState.SwitchMusic) {
            when(scrollState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    if (position == curPosition) {
                        binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                        binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    } else if (position < curPosition) {
                        binding.imgCover.setMarginLeft(DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(300f))
                    } else {
                        binding.imgCover.setMarginLeft(0)
                        binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {

                }
                RecyclerView.SCROLL_STATE_DRAGGING -> {

                }
            }
        } else if (interactiveStatus == ListState.ListCompletely) {
            if (binding.root.width != BASIC_ITEM_WIDTH) {
                binding.root.layoutParams.width = BASIC_ITEM_WIDTH
            }
            if (binding.imgCover.marginLeft != BASIC_SPACE) {
                binding.imgCover.setMarginLeft(BASIC_SPACE)
            }
        }
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onItemClick: (MusicInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgCover: View = itemView.findViewById(R.id.imgCover)

        fun bind(music: MusicInfo, position: Int) {
            renderMargin(position, binding, currentPosition, RecyclerView.SCROLL_STATE_IDLE)
            if (position % 2 == 0) {
                binding.root.setBackgroundColor("#66FCFCFF".toColorInt())
            } else {
                binding.root.setBackgroundColor("#66605A7C".toColorInt())
            }
            binding.cover.setImageURI(music.coverUrl)
            binding.name.text = music.id
            // 使用Fresco加载图片
            imgCover.setOnClickListener {
                onItemClick.invoke(music, position)
            }
        }
    }
}