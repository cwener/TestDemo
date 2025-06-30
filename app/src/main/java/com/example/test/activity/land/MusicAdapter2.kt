package com.example.test.activity.land

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.test.R
import com.example.test.databinding.ItemMusicBinding
import com.example.test.utils.DimensionUtils
import com.example.test.utils.generateRandomHexColor
import com.example.test.utils.setMarginLeft
import kotlin.math.abs


class MusicAdapter2(val recyclerView: TouchInterceptorRecyclerView,
                    private val onCircleInItemClick: (MusicInfo, Int) -> Unit,
                    private val onItemCircleOutClick: (MusicInfo, Int) -> Unit,
                    val onSwitchToTransListByScrollDistance: () -> Unit) : RecyclerView.Adapter<MusicAdapter2.MusicViewHolder>() {

    companion object {
        val BASIC_RIGHT_SPACE = DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(400f)
        val BASIC_LEFT_SPACE = DimensionUtils.dpToPx(100f)
        val BASIC_SPACE = DimensionUtils.dpToPx(10f)
        val BASIC_ITEM_WIDTH = DimensionUtils.dpToPx(300f)
        // 切歌态向右滑触发进入列表转场的距离
        val rightScrollSwitchToTransListLimitDistance = BASIC_ITEM_WIDTH - BASIC_LEFT_SPACE
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
        }
    var smoothDirection = NONE_DIRECTION
    var toLeftOriginLeft = -1

    var interactiveStatus = ListState.SwitchMusic

    init {
        recyclerView.setHandleImageViewId(R.id.clickDelegateSpace)
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
                Log.d("MusicAdapt", "onScrolled, dx=$dx, smoothDirection=$smoothDirection, currentPosition=$currentPosition")
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

                        else -> {
                            for (i in 0 until recyclerView.childCount) {
                                val child = recyclerView.getChildAt(i)
                                val position = recyclerView.getChildAdapterPosition(child)
                                val vh = recyclerView.findViewHolderForAdapterPosition(position) as? MusicViewHolder
                                vh?.let {
                                    Log.d(TAG, "onChildAttachedToWindow, position=$position")
                                    fixWidthAndMargin(position, it.binding, currentPosition)
                                }

                                val curVH = recyclerView.findViewHolderForAdapterPosition(currentPosition) as? MusicViewHolder
                                curVH?.let {
                                    // 切歌态下，向右滑动超出一定距离则触发转场进列表
                                    if (it.binding.root.left > rightScrollSwitchToTransListLimitDistance) {
                                        onSwitchToTransListByScrollDistance.invoke()
                                    }
                                }
                            }
                        }
                    }
                } else if (interactiveStatus == ListState.ListCompletely) {
                    // 遍历所有可见的ViewHolder
                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        setScaleAndAlpha(child)
                    }
                }
            }
        })
    }

    fun setScaleAndAlpha(child: View) {
        val vh = recyclerView.getChildViewHolder(child) as? MusicViewHolder
        vh ?: return
        if (child.left > BASIC_LEFT_SPACE + BASIC_ITEM_WIDTH) {
            vh.binding.imgCover.scaleX = 1f
            vh.binding.imgCover.scaleY = 1f
            vh.binding.root.alpha = 1f
        } else if (child.left >= BASIC_LEFT_SPACE) {
            // 计算当前item相对于RecyclerView左边的位置
            val scale = 0.9f + 0.1f * (BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE - child.left) / BASIC_ITEM_WIDTH
            // 应用缩放
            vh.binding.imgCover.scaleX = 1f
            vh.binding.imgCover.scaleY = 1f
            vh.binding.root.alpha = 1f
        } else if (child.left < BASIC_LEFT_SPACE) {
            var alpha = 1f + 0.4f * (child.left - BASIC_LEFT_SPACE) / (BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE)
            var scale = 1f + 0.1f * (child.left - BASIC_LEFT_SPACE) / (BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE)
            vh.binding.imgCover.scaleX = 1f
            vh.binding.imgCover.scaleY = 1f
            vh.binding.root.alpha = alpha
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        val binding = ItemMusicBinding.bind(view)
        return MusicViewHolder(binding, onCircleInItemClick, onItemCircleOutClick)
    }


    // 第0个在完全列表态时，宽度与其他不一致，因此全程所有态都不参与列表复用机制
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            100
        } else {
            super.getItemViewType(position)
        }
    }


    fun setList(musics: List<MusicInfo>) {
        list.clear()
        list.addAll(musics)
        notifyDataSetChanged()
    }

    fun renderTransToList(animatedWidth: Int, static: Boolean = true) {
        // 目标位置宽度变化时，会自动向左靠，因此需要不断计算左靠的距离，并抵消它
        val ratio = (animatedWidth - BASIC_ITEM_WIDTH) / (DimensionUtils.getFullScreenWidth() - BASIC_ITEM_WIDTH).toFloat()
        var width = animatedWidth

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val vh = recyclerView.findContainingViewHolder(child) as? MusicViewHolder
            vh ?: continue
            val position = recyclerView.getChildAdapterPosition(child)
            if (position == currentPosition) {
                if (position == 0) {
                    if (width < BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE) {
                        vh.binding.root.layoutParams.width = BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE
                    } else {
                        vh.binding.root.layoutParams.width = width
                    }
                    vh.binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                } else {
                    val leftMargin = (BASIC_LEFT_SPACE * ratio).toInt()
                    vh.binding.imgCover.setMarginLeft(leftMargin)
                    if (static) {
                        val scrollDistance = BASIC_LEFT_SPACE - leftMargin
                        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, scrollDistance)
                    } else {
                        if (ratio == 1f) {
                            recyclerView.tag = vh.binding.root.left
                            recyclerView.setTag(R.id.recyclerView, 0)
                        }
                        val supplyInstance = recyclerView.tag as? Int ?: 0
                        if (supplyInstance > 0) {
                            var scrollDistance = BASIC_LEFT_SPACE - leftMargin + supplyInstance + ((1 - ratio) * supplyInstance / 2.5).toInt()
                            val lastScrollDistance = recyclerView.getTag(R.id.recyclerView) as? Int ?: 0
                            if (lastScrollDistance > scrollDistance) {
                                scrollDistance = lastScrollDistance
                            }
                            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, scrollDistance)
                            recyclerView.setTag(R.id.recyclerView, scrollDistance)
                            Log.d("scrollDistance", "ratio:$ratio, supplyInstance=${supplyInstance}, left=${vh.binding.root.left}, scrollDistance:$scrollDistance")
                        }
                    }
                    vh.binding.root.updateLayoutParams { this.width = width  }
                    Log.d("renderTransToList", "ratio: $ratio, newWidth=${width}, rootWidth=${vh.binding.root.width}，leftMargin=$leftMargin, originLeft=${recyclerView.tag}")
                }
            } else {
                if (position != 0) {
                    if (vh.binding.root.width >= BASIC_ITEM_WIDTH) {
                        vh.binding.root.updateLayoutParams { this.width = width  }
                    }
                    if (position < currentPosition) {
                        vh.binding.imgCover.setMarginLeft(width - BASIC_ITEM_WIDTH)
                    } else {
                        if (vh.binding.imgCover.marginLeft != 0) {
                            vh.binding.imgCover.setMarginLeft(0)
                        }
                    }
                } else {
                    if (vh.binding.root.width != (BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE)) {
                        vh.binding.root.layoutParams.width = BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE
                    }
                    if (vh.binding.imgCover.marginLeft != BASIC_LEFT_SPACE) {
                        vh.binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                    }
                }
            }
            // 遍历所有可见的ViewHolder
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val position = recyclerView.getChildAdapterPosition(child)
                if (position != currentPosition) {
                    setScaleAndAlpha(child)
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
                binding.imgCover.scaleX = 1f
                binding.imgCover.scaleY = 1f
                binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                if (position == curPosition) {
                    binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                } else if (position < curPosition) {
                    binding.imgCover.setMarginLeft(DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(300f))
                } else {
                    binding.imgCover.setMarginLeft(0)
                }
            }
            ListState.ListCompletely -> {
                binding.root.alpha = 1f
                if (position != 0) {
                    // 第0个始终保持进入列表完全态的样式。宽度略大，内部有marginLeft
                    if (binding.root.width != BASIC_ITEM_WIDTH) {
                        binding.root.layoutParams.width = BASIC_ITEM_WIDTH
                    }
                    if (binding.imgCover.marginLeft != BASIC_SPACE) {
                        binding.imgCover.setMarginLeft(BASIC_SPACE)
                    }
                } else {
                    if (binding.root.width != (BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE)) {
                        binding.root.layoutParams.width = BASIC_ITEM_WIDTH + BASIC_LEFT_SPACE
                    }
                    if (binding.imgCover.marginLeft != BASIC_LEFT_SPACE) {
                        binding.imgCover.setMarginLeft(BASIC_LEFT_SPACE)
                    }
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
            ListState.ListTransToSwitchSmoothScrollToTarget -> {
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
                binding.imgCover.scaleX = 1f
                binding.imgCover.scaleY = 1f
                (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, 0)
            }
        }
        Log.d("fixWidthAndMargin", "fixWidthAndMargin, position=$position, curPosition=$curPosition, interactiveStatus=$interactiveStatus")
    }

    fun isRecyclerViewEnable(): Boolean {
        return interactiveStatus != ListState.TransToList
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onCircleInItemClick: (MusicInfo, Int) -> Unit,
        onItemCircleOutClick: (MusicInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val colorMap = mutableMapOf<String, String>()

        init {
            binding.clickDelegateSpace.updateLayoutParams {
                width = BASIC_ITEM_WIDTH
                height = BASIC_ITEM_WIDTH
            }
        }

        fun bind(music: MusicInfo, position: Int) {
            fixWidthAndMargin(position, binding, currentPosition)
            if (!colorMap.contains(music.id)) {
                colorMap.put(music.id, generateRandomHexColor(music.id.toLong()))
            }
            binding.root.setBackgroundColor(colorMap[music.id]?.toColorInt() ?: "#66605A7C".toColorInt())
            binding.name.text = (music.id.toInt() - 1).toString()
            binding.clickDelegateSpace.onCircleInClickListener = {
                onCircleInItemClick.invoke(music, position)
            }
            binding.clickDelegateSpace.onCircleInLongClickListener = {
                onCircleInItemClick.invoke(music, position)
            }
            binding.clickDelegateSpace.onCircleOutClickListener = {
                onItemCircleOutClick.invoke(music, position)
            }
        }
    }
}