package com.example.test.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.test.databinding.ActivityVolumeBinding
import com.example.test.view.VolumeDrawable

/**
 * @author: chengwen
 * @date: 2022/11/20
 */
class VolumeTestActivity : FragmentActivity() {

    private val volumeDrawable = VolumeDrawable()

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityVolumeBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.volumeImg.background = volumeDrawable
        binding.volumeImg.setOnClickListener {
            volumeDrawable.volume = 0.1f
        }
    }
}