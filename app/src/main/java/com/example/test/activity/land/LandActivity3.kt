package com.example.test.activity.land

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.test.R
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_ITEM_WIDTH
import com.example.test.activity.land.MusicAdapter2.Companion.BASIC_LEFT_SPACE
import com.example.test.activity.land.MusicAdapter2.MusicViewHolder
import com.example.test.databinding.ActivityLandBinding
import com.example.test.utils.DimensionUtils
import com.example.test.utils.calculateFlingVelocity
import com.example.test.utils.getChildViewLeftRelativeToRecyclerView
import com.example.test.utils.smoothScrollToPositionWithOffset


/**
 * @author ChengWen
 * @date：2025/6/1 19:13
 * @desc：
 */
class LandActivity3 : FragmentActivity() {

    companion object {
        const val TRANS_TO_LIST_ANIM_TIME = 600L
    }

    private lateinit var adapter: MusicAdapter2
    private lateinit var recyclerView: TouchInterceptorRecyclerView
    private val pageSnapHelper = CustomPagerSnapHelper2()
    private val leftOffsetLayoutManager by lazy {
        DynamicAttachLayoutManager(context = this, BASIC_LEFT_SPACE, binding!!.recyclerView)
    }
    private val handler = Handler(Looper.getMainLooper())
    private var binding: ActivityLandBinding? = null
    private val customItemDecoration = CustomItemDecoration()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val local = ActivityLandBinding.inflate(LayoutInflater.from(this))
        binding = local
        setContentView(local.root)
        local.imageDown.setOnClickListener {
            Toast.makeText(this, "下层View响应点击", Toast.LENGTH_SHORT).show()
        }
        recyclerView = findViewById<TouchInterceptorRecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = leftOffsetLayoutManager


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
        recyclerView.onLongTouchListener = {
            if (adapter.interactiveStatus == ListState.SwitchMusic) {
                transStatus(ListState.TransToList, adapter.currentPosition)
            }
        }

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
        binding?.root?.setOnClickListener(null)
        binding?.root?.isClickable = false
        leftOffsetLayoutManager.setAttachEnabled(false)
        recyclerView.removeItemDecoration(customItemDecoration)
        when (status) {
            ListState.SwitchMusic -> {
                pageSnapHelper.attachToRecyclerView(recyclerView)
            }

            ListState.TransToList -> {
                pageSnapHelper.attachToRecyclerView(null)

                // 开启转场时，增加一个加速度让列表自然滚动
                val endMargin = recyclerView.getChildViewLeftRelativeToRecyclerView((recyclerView.findViewHolderForAdapterPosition(adapter.currentPosition) as MusicViewHolder).binding.imgCover)
                val distance = endMargin - BASIC_LEFT_SPACE
                var velocityX = calculateFlingVelocity(distance, 500)
                Log.d("renderTransToList", "onAnimationEnd distance:${distance}, velocityX:${velocityX}")
                if (distance != 0) {
                    recyclerView.fling(-velocityX, 0)
                }

                // 创建SpringAnimation动画
                val valueAnimator = ValueAnimator.ofInt(DimensionUtils.getFullScreenWidth(), BASIC_ITEM_WIDTH)
                valueAnimator.duration = TRANS_TO_LIST_ANIM_TIME
                // 监听动画更新
                valueAnimator.interpolator = DecelerateInterpolator()

                // 添加动画更新监听器
                valueAnimator.addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Int
                    adapter.renderTransToList(animatedValue, distance == 0)
                }

                // 添加动画生命周期监听器
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        transStatus(ListState.ListCompletely, clickPos)
                    }
                })

                // 启动动画
                valueAnimator.start()
            }

            ListState.ListCompletely -> {
                leftOffsetLayoutManager.setAttachEnabled(false)
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