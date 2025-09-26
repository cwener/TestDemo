package com.example.test.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import java.util.Random
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author chengwen
 * @createTime 2023/5/23
 **/
class FltZanAtmosphereView : FrameLayout {

    private val iconViewCache = LinkedBlockingQueue<ImageView>(10)
    private lateinit var zanIcon: ImageView
    private lateinit var zanCount: TextView
    private var zanCountValue = 128
    private val random = Random()
    private var lastClickTime: Long = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        // 加载自定义布局
        val view = LayoutInflater.from(context).inflate(R.layout.view_zan_custom, this, true)
        zanIcon = view.findViewById(R.id.zanIcon)
        zanCount = view.findViewById(R.id.zanCount)
        
        // 初始化点赞计数
        updateZanCount()
        
        // 设置点击事件
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 计数文本点击事件 - 触发氛围动效
        zanIcon.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 100) {
                lastClickTime = currentTime

                // Add click animation
                val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f)
                val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f)
                ObjectAnimator.ofPropertyValuesHolder(zanIcon, scaleX, scaleY).apply {
                    duration = 100
                    start()
                }

                zanCountValue++
                updateZanCount()
                triggerAtmosphereEffect()
            }
        }
    }

    private fun updateZanCount() {
        zanCount.text = zanCountValue.toString()
    }
    
    private fun createIconView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(36.dpToPx(), 36.dpToPx())
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    private fun triggerAtmosphereEffect() {
        // 从缓存池获取或创建新的ImageView
        val iconView = iconViewCache.poll() ?: createIconView()

        // 重置并设置iconView的属性
        iconView.apply {
            alpha = 0f
            scaleX = 0f
            scaleY = 0f

            // 随机选择图片
            val images = listOf(
                R.drawable.icon_lt_atmosphere_zan_call,
                R.drawable.icon_lt_atmosphere_zan_good,
                R.drawable.icon_lt_atmosphere_zan_heart
            )
            setImageResource(images.random())
        }

        // 添加到布局中
        addView(iconView)

        // 获取图标尺寸
        val iconSize = 36.dpToPx().toFloat()

        // 将图标初始位置设置在 zanIcon 顶部中心
        val startX = zanIcon.x + (zanIcon.width - iconSize) / 2f
        val startY = zanIcon.y
        iconView.translationX = startX
        iconView.translationY = startY

        // 创建动画集
        val animatorSet = AnimatorSet()

        // 定义动画路径
        val endY1 = height / 2f - iconSize
        val endY2 = 0f - iconSize

        // 第一阶段动画：从zanIcon位置上升，淡入并放大
        val translateY1 = ObjectAnimator.ofFloat(iconView, "translationY", startY, endY1)
        val alpha1 = ObjectAnimator.ofFloat(iconView, "alpha", 0f, 1f)
        val scale1X = ObjectAnimator.ofFloat(iconView, "scaleX", 0f, 1f)
        val scale1Y = ObjectAnimator.ofFloat(iconView, "scaleY", 0f, 1f)

        val firstPhaseSet = AnimatorSet().apply {
            playTogether(translateY1, alpha1, scale1X, scale1Y)
            duration = 500
        }

        // 第二阶段动画：继续上升，淡出并缩小，同时随机水平偏移
        val translateY2 = ObjectAnimator.ofFloat(iconView, "translationY", endY1, endY2)
        val alpha2 = ObjectAnimator.ofFloat(iconView, "alpha", 1f, 0f)
        val scale2X = ObjectAnimator.ofFloat(iconView, "scaleX", 1f, 0f)
        val scale2Y = ObjectAnimator.ofFloat(iconView, "scaleY", 1f, 0f)

        val randomOffset = if (random.nextBoolean()) 8.dpToPx().toFloat() else -8.dpToPx().toFloat()
        val translateX = ObjectAnimator.ofFloat(iconView, "translationX", startX, startX + randomOffset)

        val secondPhaseSet = AnimatorSet().apply {
            playTogether(translateY2, alpha2, scale2X, scale2Y, translateX)
            duration = 700
        }

        // 顺序播放两个阶段的动画
        animatorSet.playSequentially(firstPhaseSet, secondPhaseSet)

        // 设置线性插值器
        animatorSet.interpolator = LinearInterpolator()

        // 添加动画监听器，在动画结束后移除图标
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                removeView(iconView)
                iconViewCache.offer(iconView)
            }
        })

        // 启动动画
        animatorSet.start()
    }
    
    // 扩展函数：dp转px
    private fun Int.dpToPx(): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
    
    // 公共方法：设置点赞计数
    fun setZanCount(count: Int) {
        zanCountValue = count
        updateZanCount()
    }
    
    // 公共方法：获取点赞计数
    fun getZanCount(): Int = zanCountValue
    
    // 公共方法：设置图标点击监听器
    fun setOnIconClickListener(listener: OnClickListener) {
        zanIcon.setOnClickListener(listener)
    }
    
    // 公共方法：设置计数点击监听器
    fun setOnCountClickListener(listener: OnClickListener) {
        zanCount.setOnClickListener(listener)
    }
}