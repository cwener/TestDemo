import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.activity.land.MusicInfo
import com.facebook.drawee.view.SimpleDraweeView

class MusicAdapter(private val onItemClick: (MusicInfo) -> Unit) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private val list = mutableListOf<MusicInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view, onItemClick)
    }

    fun setList(musics: List<MusicInfo>) {
        list.clear()
        list.addAll(musics)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int): MusicInfo {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MusicViewHolder(
        itemView: View,
        private val onItemClick: (MusicInfo) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imgCover: SimpleDraweeView = itemView.findViewById(R.id.imgCover)

        fun bind(music: MusicInfo) {
            // 使用Fresco加载图片
            imgCover.setImageURI(music.coverUrl.toUri())

            // 设置点击事件
            itemView.setOnClickListener {
                onItemClick(music)
            }
        }
    }
}