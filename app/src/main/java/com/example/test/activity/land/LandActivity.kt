package com.example.test.activity.land

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ActivityLandBinding
import com.example.test.utils.DimensionUtils


/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity: FragmentActivity() {

    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLandBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.imageDown.setOnClickListener {
            Toast.makeText(this, "下层View响应点击", Toast.LENGTH_SHORT).show()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 初始化适配器
        adapter = MusicAdapter { music, position ->
            // 处理item点击事件
            Toast.makeText(this, "Selected: ${music.title}", Toast.LENGTH_SHORT).show()
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            viewHolder?.let {
//                animateViewWidth(it.itemView)
            }
        }

        // 设置适配器
        recyclerView.adapter = adapter

        // 应用 SnapHelper
        val snapHelper = CustomPagerSnapHelper()
        snapHelper.setAdapter(adapter)
        snapHelper.attachToRecyclerView(recyclerView)

        // 添加自定义滚动监听器
        recyclerView.addOnScrollListener(PageScrollListener(adapter))
        recyclerView.setPadding(MusicAdapter.LeftEdgeWidth, 0, 0, 0)
        recyclerView.clipToPadding = false


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

    fun animateViewWidth(view: View) {
        // 将dp转为像素

        // 获取当前宽度（屏幕宽度）
        val startWidth = view.width

        val animator = ValueAnimator.ofInt(startWidth, DimensionUtils.dpToPx(300f))


        // 设置弹性插值器
        // 可以选择BounceInterpolator或SpringInterpolator实现弹性效果
        animator.interpolator = OvershootInterpolator(0.7f)

        // 或者使用弹簧效果
        // animator.setInterpolator(new SpringInterpolator(0.4f)); // 需自定义
        animator.addUpdateListener(object : AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.getAnimatedValue() as Int
                // 重要：从右侧缩减宽度，需要调整layout参数
                val layoutParams = view.layoutParams
                layoutParams.width = value
                view.setLayoutParams(layoutParams)
                // 如果是在ConstraintLayout中，还可以设置右对齐
                // 这样缩减时会保持右边界不变
            }
        })

        animator.setDuration(500) // 动画持续时间，单位毫秒
        animator.start()
    }
}