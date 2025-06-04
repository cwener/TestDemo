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
import com.example.test.utils.generateRandomHexColor
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
            val vh = recyclerView.findViewHolderForAdapterPosition(position) as? MusicViewHolder
            vh?.let {
                Log.d(TAG, "onChildAttachedToWindow, position=$position")
                fixWidthAndMargin(position, it.binding, currentPosition)
            }
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

    fun renderTransToSwitch(onScrollFinished: (() -> Unit)? = null) {
        var curBinding: ItemMusicBinding? = null
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val vh = recyclerView.findContainingViewHolder(child) as? MusicViewHolder
            vh ?: continue
            val position = recyclerView.getChildAdapterPosition(child)
            if (position != currentPosition) {
                fixWidthAndMargin(position, vh.binding, currentPosition)
            } else {
                curBinding = vh.binding
            }
        }
        curBinding?.let {
            fixWidthAndMargin(currentPosition, it, currentPosition)
        }
        onScrollFinished?.invoke()
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

    fun onTargetFound(dx: Int) {
        smoothDirection = if (dx == 0) {
            NONE_DIRECTION
        } else if (dx > 0) {
            // 手指向左滑，右侧的VH黑胶做margin从 0 到 LeftEdgeWidth 的位移动画
            TO_LEFT
        } else {
            // 手指向右滑，左侧的VH黑胶做从margin从 LeftEdgeToRight 到 LeftEdgeWidth 的位移动画
            TO_RIGHT
        }
    }

    // 这里才是列表真正的静止状态
    fun calculateDistanceToFinalSnap(res: IntArray?) {
        if (res?.get(0) == 0) {
            val vh = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
            vh ?: return
            fixWidthAndMargin(currentPosition, vh.binding, currentPosition)
            smoothDirection = NONE_DIRECTION
            toLeftOriginLeft = INVALID_LEFT
        }
    }

    fun fixWidthAndMargin(position: Int, binding: ItemMusicBinding, curPosition: Int) {
        when(interactiveStatus) {
            ListState.SwitchMusic -> {
                binding.root.alpha = 1f
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
            ListState.ListCompletely -> {
                binding.root.alpha = 1f
                if (binding.root.width != BASIC_ITEM_WIDTH) {
                    binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                }
                if (binding.imgCover.marginLeft != BASIC_SPACE) {
                    binding.imgCover.setMarginLeft(BASIC_SPACE)
                }
            }
            ListState.TransToList -> {
                binding.root.alpha = 1f
                if (position != curPosition) {
                    if (binding.root.width != BASIC_ITEM_WIDTH) {
                        binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                    }
                    if (binding.imgCover.marginLeft != BASIC_SPACE) {
                        binding.imgCover.setMarginLeft(BASIC_SPACE)
                    }
                }
            }
            ListState.ListTransToSwitchScrollToTarget -> {
                if (binding.root.width != BASIC_ITEM_WIDTH) {
                    binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                }
                if (binding.imgCover.marginLeft != BASIC_SPACE) {
                    binding.imgCover.setMarginLeft(BASIC_SPACE)
                }
            }
            ListState.ListTransToSwitchFadeExit -> {
                if (position == curPosition) {
                    binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                    binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    binding.root.alpha = 1f
                } else {
                    binding.root.alpha = 0f
                }
                (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, 0)
            }
        }
        Log.d(TAG, "fixWidthAndMargin, position=$position, curPosition=$curPosition, interactiveStatus=$interactiveStatus")
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onItemClick: (MusicInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgCover: View = itemView.findViewById(R.id.imgCover)
        val colorMap = mutableMapOf<String, String>()
        fun bind(music: MusicInfo, position: Int) {
            fixWidthAndMargin(position, binding, currentPosition)
            if (!colorMap.contains(music.id)) {
                colorMap.put(music.id, generateRandomHexColor(music.id.toLong()))
            }
            binding.cover.setBackgroundColor(colorMap[music.id]?.toColorInt() ?: "#66605A7C".toColorInt())
            binding.cover.setImageURI(music.coverUrl)
            binding.name.text = (music.id.toInt() - 1).toString()
            // 使用Fresco加载图片
            imgCover.setOnClickListener {
                onItemClick.invoke(music, position)
            }
        }
    }
}