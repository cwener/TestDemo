package com.example.test.activity.land

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_LEFT_SPACE
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_RIGHT_SPACE
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_SPACE
import com.example.test.databinding.ActivityLandBinding


/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity2: FragmentActivity() {

    private lateinit var adapter: MusicAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLandBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.imageDown.setOnClickListener {
            Toast.makeText(this, "下层View响应点击", Toast.LENGTH_SHORT).show()
        }
        val recyclerView = findViewById<TouchInterceptorRecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        adapter = MusicAdapter2(recyclerView, onCircleInItemClick = { music, position ->
            // 处理item点击事件
            Toast.makeText(this, "Selected: ${music.title}", Toast.LENGTH_SHORT).show()
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            viewHolder?.let {
//                animateViewWidth(it.itemView)
            }
        }, onItemCircleOutClick = { music, position ->
            // 处理item点击事件
            Toast.makeText(this, "Selected: ${music.title}", Toast.LENGTH_SHORT).show()
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            viewHolder?.let {
//                animateViewWidth(it.itemView)
            }
        })

        // 设置适配器
        recyclerView.adapter = adapter

        // 应用 SnapHelper
//        val snapHelper = CustomPagerSnapHelper2()
//        snapHelper.setAdapter(adapter)
//        snapHelper.attachToRecyclerView(recyclerView)

        adapter.currentPosition = 0

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position == RecyclerView.NO_POSITION) return
                val scrollState = recyclerView.scrollState
                when(scrollState) {
                    // 拖拽中 目标位置不会改变
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (position == adapter.currentPosition) {
                            val left = view.left
                            if (left < 0) {
                                outRect.right = BASIC_LEFT_SPACE
                            } else if (left < BASIC_LEFT_SPACE) {
                                outRect.right = (BASIC_RIGHT_SPACE * ((left.toFloat()) / BASIC_LEFT_SPACE)).toInt() + BASIC_LEFT_SPACE
                            } else {
                                outRect.right = BASIC_RIGHT_SPACE
                            }
                            outRect.left = BASIC_LEFT_SPACE
                            Log.d("addItemDecoration", "curPos = ${adapter.currentPosition}, targetView.left=${view.left}, outRect.left=${outRect.left}, outRect.right=${outRect.right}")
                        } else if (position < adapter.currentPosition) {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_SPACE
                        } else {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_RIGHT_SPACE
                        }
                    }
                    // 静止状态
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (position > adapter.currentPosition) {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_RIGHT_SPACE
                        } else if (position == adapter.currentPosition) {
                            outRect.left = BASIC_LEFT_SPACE
                            outRect.right = BASIC_RIGHT_SPACE
                        }  else {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_SPACE
                        }
                    }
                    // 惯性滚动中
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        if (position > adapter.currentPosition) {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_RIGHT_SPACE
                        } else if (position == adapter.currentPosition) {
                            outRect.left = BASIC_LEFT_SPACE
                            outRect.right = BASIC_RIGHT_SPACE
                        }  else {
                            outRect.left = BASIC_SPACE
                            outRect.right = BASIC_SPACE
                        }
                    }
                }
            }
        })


        // 加载示例数据
        loadMusicData()
    }


    private fun loadMusicData() {
        // 模拟从网络或数据库加载数据
        val musicList = listOf(
            MusicInfo(
                id = "1",
                coverUrl = "http://p1.music.126.net/xXuvLXSk1RcD1Dx5JInIiw==/109951169612265280.jpg",
                title = "Shape of You",
                artist = "Ed Sheeran"
            ),
            MusicInfo(
                id = "2",
                coverUrl = "http://p1.music.126.net/73Acuw1l7Wc7Q98-FkIwBw==/109951168224246420.jpg",
                title = "Blinding Lights",
                artist = "The Weeknd"
            ),
            MusicInfo(
                id = "3",
                coverUrl = "http://p1.music.126.net/pHD15yOuVKvRy0xmlNaKww==/109951167386107088.jpg",
                title = "Dance Monkey",
                artist = "Tones and I"
            ),
            MusicInfo(
                id = "4",
                coverUrl = "http://p2.music.126.net/5k_BRr3vUfjJq97LQaj3jQ==/109951166583357991.jpg",
                title = "Someone You Loved",
                artist = "Lewis Capaldi"
            ),
            MusicInfo(
                id = "5",
                coverUrl = "http://p2.music.126.net/iN7OUPkcavI2MBSG8pf2jw==/109951170600632372.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            ),
            MusicInfo(
                id = "6",
                coverUrl = "http://p2.music.126.net/oiXnXrU6XJ9cIQe_3ii2YQ==/5923069139090882.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            ),
            MusicInfo(
                id = "7",
                coverUrl = "http://p2.music.126.net/wcLyz1Cf111MWz9Odg0iRw==/109951164975030169.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            )
        )

        // 提交到适配器
        adapter.setList(musicList)
    }

}