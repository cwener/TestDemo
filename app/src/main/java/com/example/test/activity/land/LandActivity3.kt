package com.example.test.activity.land

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import com.example.test.activity.ApplicationWrapper
import com.example.test.databinding.ActivityLandBinding


/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity3: FragmentActivity() {

    private lateinit var adapter: MusicAdapter2
    private lateinit var recyclerView: TouchInterceptorRecyclerView
    private val pageSnapHelper = CustomPagerSnapHelper2()
    private val leftOffsetSnapHelper = LeftOffsetSnapHelper(MusicAdapter2.BASIC_LEFT_SPACE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLandBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.imageDown.setOnClickListener {
            Toast.makeText(this, "下层View响应点击", Toast.LENGTH_SHORT).show()
        }
        recyclerView = findViewById<TouchInterceptorRecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        adapter = MusicAdapter2(recyclerView) { music, position ->
            // 处理item点击事件
            Toast.makeText(ApplicationWrapper.instance, "Selected: ${music.title}", Toast.LENGTH_SHORT).show()
            if (position == adapter.currentPosition) {
                transStatus(ListState.TransToList)
            }
        }

        // 设置适配器
        recyclerView.adapter = adapter

        // 应用 SnapHelper
        pageSnapHelper.setAdapter(adapter)
        pageSnapHelper.attachToRecyclerView(recyclerView)

        adapter.currentPosition = 0

        // 加载示例数据
        loadMusicData()
    }

    // 列表转场
    fun transStatus(status: Int) {
        adapter.interactiveStatus = status

        pageSnapHelper.attachToRecyclerView(null)
        leftOffsetSnapHelper.attachToRecyclerView(null)

        when(status) {
            ListState.SwitchMusic -> {
                pageSnapHelper.attachToRecyclerView(recyclerView)
            }
            ListState.TransToList -> {
                pageSnapHelper.attachToRecyclerView(null)
                val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                    // 设置动画时长为1秒
                    this.duration = duration
                    // 使用AccelerateDecelerateInterpolator实现先加速后减速效果
                    interpolator = AccelerateDecelerateInterpolator()
                    // 添加更新监听器，在动画的每一帧更新视图位置
                    addUpdateListener { animator ->
                        // 获取当前动画值
                        val animatedValue = animator.animatedValue as Float
                        adapter.renderTransToList(animatedValue)
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            transStatus(ListState.ListCompletely)
                        }
                    })
                }
                // 启动动画
                animator.start()
            }
            ListState.ListCompletely -> {
                leftOffsetSnapHelper.attachToRecyclerView(recyclerView)
            }
            ListState.ListTransToSwitch -> {

            }
        }
        Log.d(MusicAdapter2.TAG, "transStatus=$status")
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
            ),
            MusicInfo(
                id = "8",
                coverUrl = "http://p1.music.126.net/xXuvLXSk1RcD1Dx5JInIiw==/109951169612265280.jpg",
                title = "Shape of You",
                artist = "Ed Sheeran"
            ),
            MusicInfo(
                id = "9",
                coverUrl = "http://p1.music.126.net/73Acuw1l7Wc7Q98-FkIwBw==/109951168224246420.jpg",
                title = "Blinding Lights",
                artist = "The Weeknd"
            ),
            MusicInfo(
                id = "10",
                coverUrl = "http://p1.music.126.net/pHD15yOuVKvRy0xmlNaKww==/109951167386107088.jpg",
                title = "Dance Monkey",
                artist = "Tones and I"
            ),
            MusicInfo(
                id = "11",
                coverUrl = "http://p2.music.126.net/5k_BRr3vUfjJq97LQaj3jQ==/109951166583357991.jpg",
                title = "Someone You Loved",
                artist = "Lewis Capaldi"
            ),
            MusicInfo(
                id = "12",
                coverUrl = "http://p2.music.126.net/iN7OUPkcavI2MBSG8pf2jw==/109951170600632372.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            ),
            MusicInfo(
                id = "13",
                coverUrl = "http://p2.music.126.net/oiXnXrU6XJ9cIQe_3ii2YQ==/5923069139090882.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            ),
            MusicInfo(
                id = "14",
                coverUrl = "http://p2.music.126.net/wcLyz1Cf111MWz9Odg0iRw==/109951164975030169.jpg",
                title = "Bad Guy",
                artist = "Billie Eilish"
            )
        )

        // 提交到适配器
        adapter.setList(musicList)
    }

}