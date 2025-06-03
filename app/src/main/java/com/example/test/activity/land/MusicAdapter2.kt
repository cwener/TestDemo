package com.example.test.activity.land

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.test.R
import com.example.test.databinding.ItemMusicBinding
import com.example.test.utils.DimensionUtils
import com.facebook.drawee.view.SimpleDraweeView

class MusicAdapter2(val recyclerView: RecyclerView, private val onItemClick: (MusicInfo, Int) -> Unit) : RecyclerView.Adapter<MusicAdapter2.MusicViewHolder>() {

    companion object {
        val BASIC_RIGHT_SPACE = DimensionUtils.getFullScreenWidth() - DimensionUtils.dpToPx(400f)
        val BASIC_LEFT_SPACE = DimensionUtils.dpToPx(100f)
        val BASIC_SPACE = DimensionUtils.dpToPx(10f)
        val BASIC_ITEM_WIDTH = DimensionUtils.dpToPx(300f)
        const val TAG = "MusicAdapter"
    }

    private val list = mutableListOf<MusicInfo>()
    var lastCurrentPosition = -1
    var currentPosition: Int = 0
        get() = field
        set(value) {
            lastCurrentPosition = field
            Log.i("MusicAdapter", "old = ${field}, currentPosition=$value")
            field = value
            recyclerView.invalidateItemDecorations()
        }

    init {
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("MusicAdapt", "onScrolled, dx=$dx")
                recyclerView.invalidateItemDecorations()
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

        } else {
            // 手指向右滑，左侧的VH黑胶做从margin从 LeftEdgeToRight 到 LeftEdgeWidth 的位移动画
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
}