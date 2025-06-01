package com.example.test.activity.land

import MusicAdapter
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R

/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity: FragmentActivity() {

    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_land)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        adapter = MusicAdapter { music ->
            // 处理item点击事件
            Toast.makeText(this, "Selected: ${music.title}", Toast.LENGTH_SHORT).show()
        }

        // 设置适配器
        recyclerView.adapter = adapter

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