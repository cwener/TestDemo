package com.example.test.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityZanBinding

class ZanActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityZanBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置标题
        title = "点赞功能测试"
        
        // 初始化点赞功能
        setupZanFeature()
        binding.zanView.setZanCount(20)
        binding.zanView.setZanCount(30)
    }
    
    private fun setupZanFeature() {
        binding.apply {
            // 返回按钮
            backButton.setOnClickListener {
                finish()
            }
        }
    }
}