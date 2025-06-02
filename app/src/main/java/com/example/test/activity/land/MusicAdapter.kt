package com.example.test.activity.land

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
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
                Log.i("MusicAdapt", "onScrolled, dx=$dx")
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

    fun resetLeftAndRight() {

        fun updateParams(view: View, gravity: Int, marginLeft: Int) {
            val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
            var requestLayout = false
            if (params.gravity != gravity) {
                params.gravity = gravity
                requestLayout = true
            }
            if (params.leftMargin != marginLeft) {
                params.setMargins(marginLeft, 0, 0, 0)
                requestLayout = true
            }
            if (requestLayout) {
                view.requestLayout()
            }
        }

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val vh = recyclerView.getChildViewHolder(child) as MusicViewHolder
            val position = recyclerView.getChildAdapterPosition(child)
            if (position < currentPosition) {
                updateParams(vh.binding.imgCover, Gravity.END or Gravity.CENTER_VERTICAL, 0)
            } else if (position == currentPosition) {
                updateParams(vh.binding.imgCover, Gravity.START or Gravity.CENTER_VERTICAL, LeftEdgeWidth)
            } else {
                updateParams(vh.binding.imgCover, Gravity.START or Gravity.CENTER_VERTICAL, 0)
            }
            vh.binding.imgCover.requestLayout()
        }
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onItemClick: (MusicInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgCover: SimpleDraweeView = itemView.findViewById(R.id.imgCover)

        fun bind(music: MusicInfo, position: Int) {
            resetLeftAndRight()
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
}