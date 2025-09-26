package com.example.test.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import java.util.Random

/**
 * @author chengwen
 * @createTime 2023/5/23
 **/
class FltZanAtmosphereView : FrameLayout {
    
    private lateinit var zanIcon: ImageView
    private lateinit var zanCount: TextView
    private var zanCountValue = 128
    private val random = Random()
    
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
            zanCountValue++
            updateZanCount()
            triggerAtmosphereEffect()
        }
    }
    
    private fun updateZanCount() {
        zanCount.text = zanCountValue.toString()
    }
    
    private fun triggerAtmosphereEffect() {
        // 创建36x36的图标
        val iconView = ImageView(context).apply {
            layoutParams = LayoutParams(36.dpToPx(), 36.dpToPx())
            scaleType = ImageView.ScaleType.FIT_CENTER
            
            // 随机选择call、good、heart中的一个图片
            val images = listOf(
                R.drawable.icon_lt_atmosphere_zan_call,
                R.drawable.icon_lt_atmosphere_zan_good,
                R.drawable.icon_lt_atmosphere_zan_heart
            )
            setImageResource(images.random())
            
            // 设置初始状态
            alpha = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        // 添加到布局中，位置在zanCount的顶部
        addView(iconView)
        
//        // 获取容器高度
        val containerHeight = height.toFloat()
//
//        // 创建动画集合
        val animatorSet = AnimatorSet()
//
//        // 0-500ms: 从底部到50%位置，透明度0到1，大小从0到1
        val translateY1 = ObjectAnimator.ofFloat(iconView, "translationY", 0f, -containerHeight * 0.5f)
        val alpha1 = ObjectAnimator.ofFloat(iconView, "alpha", 0f, 1f)
        val scale1X = ObjectAnimator.ofFloat(iconView, "scaleX", 0f, 1f)
        val scale1Y = ObjectAnimator.ofFloat(iconView, "scaleY", 0f, 1f)
//
//        // 500-1200ms: 从50%到100%位置，透明度1到0，大小从1到0，随机左右偏移
        val translateY2 = ObjectAnimator.ofFloat(iconView, "translationY", -containerHeight * 0.5f, -containerHeight)
//        val alpha2 = ObjectAnimator.ofFloat(iconView, "alpha", 1f, 0f)
//        val scale2X = ObjectAnimator.ofFloat(iconView, "scaleX", 1f, 0f)
//        val scale2Y = ObjectAnimator.ofFloat(iconView, "scaleY", 1f, 0f)
//
//        // 随机左右偏移8dp
//        val randomOffset = if (random.nextBoolean()) 8.dpToPx().toFloat() else -8.dpToPx().toFloat()
//        val translateX = ObjectAnimator.ofFloat(iconView, "translationX", 0f, randomOffset)
//
//        // 设置动画时长
        val firstPhaseSet = AnimatorSet().apply {
//            playTogether(translateY1, alpha1, scale1X, scale1Y)
            playTogether(alpha1, scale1X, scale1Y)
            duration = 500
        }
//
//        val secondPhaseSet = AnimatorSet().apply {
//            playTogether(translateY2, alpha2, scale2X, scale2Y, translateX)
//            duration = 700
//        }
//
//        // 组合动画
//        animatorSet.playSequentially(firstPhaseSet, secondPhaseSet)
        animatorSet.playSequentially(firstPhaseSet)

        // 设置线性加速器
        animatorSet.interpolator = LinearInterpolator()

        // 添加动画监听器
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 动画结束后移除图标
                removeView(iconView)
            }
        })

        // 开始动画
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