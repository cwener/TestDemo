package com.example.test.activity.land

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_ITEM_WIDTH
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_LEFT_SPACE
import com.example.test.databinding.ActivityLandBinding
import com.example.test.utils.DimensionUtils
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

                // 创建一个FloatValueHolder来保存当前值
                val valueHolder = FloatValueHolder(DimensionUtils.getFullScreenWidth().toFloat())

                // 创建SpringAnimation动画
                val springAnimation = SpringAnimation(valueHolder)

                // 配置SpringForce
                val springForce = SpringForce(BASIC_ITEM_WIDTH.toFloat()).apply {
                    // 设置阻尼比 - 控制振荡
                    dampingRatio = 0.783f

                    // 设置刚度 - 控制速度
                    stiffness = 132f
                }

                // 为动画设置SpringForce
                springAnimation.spring = springForce

                // 监听动画更新
                springAnimation.addUpdateListener { _, value, _ ->
                    // 更新UI显示当前值
                    val roundedValue = value.toInt()
                    Log.d("SpringAnimation", "Current value: $value")
                    adapter.renderTransToList(roundedValue)
                }

                // 动画结束监听
                springAnimation.addEndListener { _, _, _, _ ->
                    Log.d("SpringAnimation", "Animation ended")
                    transStatus(ListState.ListCompletely, clickPos)
                }

                // 启动动画
                springAnimation.start()
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
        val musicList = mutableListOf<MusicInfo>()
        for (i in 1 until 100) {
            musicList.add(MusicInfo(id = "$i", title = "Music $i", artist = "Artist $i", coverUrl = "http://p1.music.126.net/xXuvLXSk1RcD1Dx5JInIiw==/109951169612265280.jpg"))
        }
        // 提交到适配器
        adapter.setList(musicList)
    }

}