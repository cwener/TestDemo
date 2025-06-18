package com.example.test.activity.land

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_LEFT_SPACE
import com.example.test.databinding.ActivityLandBinding
import com.example.test.utils.smoothScrollToPositionWithOffset


/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity3 : FragmentActivity() {

    private lateinit var adapter: MusicAdapter2
    private lateinit var recyclerView: TouchInterceptorRecyclerView
    private val pageSnapHelper = CustomPagerSnapHelper2()
    private val leftOffsetSnapHelper = LeftOffsetSnapHelper(BASIC_LEFT_SPACE)
    private val handler = Handler(Looper.getMainLooper())
    private var binding: ActivityLandBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val local = ActivityLandBinding.inflate(LayoutInflater.from(this))
        binding = local
        setContentView(local.root)
        local.imageDown.setOnClickListener {
            Toast.makeText(this, "下层View响应点击", Toast.LENGTH_SHORT).show()
        }
        recyclerView = findViewById<TouchInterceptorRecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        adapter = MusicAdapter2(recyclerView, onCircleInItemClick = { music, position ->
            // 处理item点击事件
//            Toast.makeText(
//                ApplicationWrapper.instance,
//                "Selected: ${music.title}",
//                Toast.LENGTH_SHORT
//            ).show()
            adapter.currentPosition = position
            if (adapter.interactiveStatus == ListState.SwitchMusic) {
                if (position == adapter.currentPosition) {
                    transStatus(ListState.TransToList, position)
                }
            } else if (adapter.interactiveStatus == ListState.ListCompletely) {
                transStatus(ListState.ListTransToSwitchSmoothScrollToTarget, position)
            }

        }, onItemCircleOutClick = { music, position ->
            transStatus(ListState.ListTransToSwitchScrollToTarget, adapter.currentPosition)
        })

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
    fun transStatus(status: Int, clickPos: Int) {
        adapter.interactiveStatus = status

        pageSnapHelper.attachToRecyclerView(null)
        leftOffsetSnapHelper.attachToRecyclerView(null)
        binding?.root?.setOnClickListener(null)
        binding?.root?.isClickable = false
        when (status) {
            ListState.SwitchMusic -> {
                pageSnapHelper.attachToRecyclerView(recyclerView)
            }

            ListState.TransToList -> {
                pageSnapHelper.attachToRecyclerView(null)
                val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                    addUpdateListener { animator ->
                        val currentWidth = animator.animatedValue as Float
                        adapter.renderTransToList(currentWidth)
                    }
                    duration = 600
                    interpolator = EaseCubicInterpolator3(0.33f, 0.52f, 0.64f, 1f)
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            transStatus(ListState.ListCompletely, clickPos)
                        }
                    })
                }

                animator.start()
            }

            ListState.ListCompletely -> {
                leftOffsetSnapHelper.attachToRecyclerView(recyclerView)
                binding?.root?.setOnClickListener {
                    transStatus(ListState.ListTransToSwitchScrollToTarget, adapter.currentPosition)
                }
            }

            ListState.ListTransToSwitchSmoothScrollToTarget -> {
                Log.d(MusicAdapter2.TAG, "smoothScrollToPositionWithOffset started")
                recyclerView.smoothScrollToPositionWithOffset(
                    clickPos,
                    BASIC_LEFT_SPACE,
                    onScrolled = {
                        for (i in 0 until recyclerView.childCount) {
                            val child = recyclerView.getChildAt(i)
                            adapter.setScaleAndAlpha(child)
                        }
                    },
                    onScrollFinished = {
                        Log.d(MusicAdapter2.TAG, "smoothScrollToPositionWithOffset finished")
                        transStatus(ListState.ListTransToSwitchFadeExit, adapter.currentPosition)
                    })
            }

            ListState.ListTransToSwitchScrollToTarget -> {
                binding?.let { binding ->
                    binding.recyclerView.scrollToPosition(adapter.currentPosition)
                    transStatus(ListState.ListTransToSwitchFadeExit, adapter.currentPosition)
                }
            }

            ListState.ListTransToSwitchFadeExit -> {
                adapter.renderTransToSwitch(onScrollFinished = {
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            transStatus(ListState.SwitchMusic, clickPos)
                        }

                    }, 500)
                })
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