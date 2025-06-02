package com.example.test.activity.land

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ItemMusicBinding
import com.example.test.utils.DimensionUtils
import com.facebook.drawee.view.SimpleDraweeView

class MusicAdapter(private val onItemClick: (MusicInfo) -> Unit) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    companion object {
        val LeftEdgeWidth = DimensionUtils.dpToPx(100f)
    }

    private val list = mutableListOf<MusicInfo>()
    var currentPosition: Int = 0
        get() = field
        set(value) {
            Log.i("MusicAdapter", "old = ${field}, currentPosition=$value")
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        val binding = ItemMusicBinding.bind(view)
        binding.root.layoutParams.width = (DimensionUtils.getFullScreenWidth() - LeftEdgeWidth).toInt()
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

    // 更新ImageView的位置
    fun updateImagePosition(imageView: ImageView, position: Int) {
        val params = imageView.layoutParams as FrameLayout.LayoutParams
        if (position < currentPosition) {
            // 当前页之前的ViewHolder的ImageView居右对齐，且距右100dp
            params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            params.marginEnd = DimensionUtils.dpToPx(100f)
        } else {
            // 当前页及之后的ViewHolder的ImageView居左对齐
            params.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            params.marginEnd = 0
        }
        imageView.layoutParams = params
    }

    // 当当前位置变化时，更新所有可见的ViewHolder
    fun onCurrentPositionChanged(recyclerView: RecyclerView) {
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val viewHolder = recyclerView.getChildViewHolder(child) as MusicViewHolder
            val position = recyclerView.getChildAdapterPosition(child)
            if (position != RecyclerView.NO_POSITION) {
                updateImagePosition(viewHolder.imgCover, position)
            }
        }
    }

    inner class MusicViewHolder(
        val binding: ItemMusicBinding,
        private val onItemClick: (MusicInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgCover: SimpleDraweeView = itemView.findViewById(R.id.imgCover)

        fun bind(music: MusicInfo, position: Int) {
            updateImagePosition(binding.imgCover, position)
            if (position % 2 == 0) {
                binding.root.setBackgroundColor("#66FCFCFF".toColorInt())
            } else {
                binding.root.setBackgroundColor("#66605A7C".toColorInt())
            }
            // 使用Fresco加载图片
            imgCover.setImageURI(music.coverUrl.toUri())
            imgCover.setOnClickListener {
                onItemClick.invoke(music)
            }
        }
    }
}