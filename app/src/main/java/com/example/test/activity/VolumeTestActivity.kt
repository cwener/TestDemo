package com.example.test.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.test.databinding.ActivityVolumeBinding
import com.example.test.view.CircularVolumeDrawable
import com.example.test.view.GoRoomCountDownDrawable
import com.example.test.view.VolumeDrawable

/**
 * @author: chengwen
 * @date: 2022/11/20
 */
class VolumeTestActivity : FragmentActivity() {

    private val volumeDrawable = VolumeDrawable()
    private val volumeDrawable2 = CircularVolumeDrawable()
    private val goRoomCountDownDrawable = GoRoomCountDownDrawable()

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityVolumeBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.volumeImg.background = volumeDrawable2
        binding.volumeImg.setOnClickListener {
            volumeDrawable2.volume = 0.1f
        }
        goRoomCountDownDrawable.total = 0
        binding.gotoLiveRoom.background = goRoomCountDownDrawable
        binding.gotoLiveRoom.setOnClickListener {
            goRoomCountDownDrawable.total = 10 * 1000
        }
//        val drawable = DrawableCompat.wrap(binding.mask.drawable).mutate()
//        DrawableCompat.setTint(drawable, getColor(R.color.white))
//        binding.mask.setImageDrawable(drawable)
    }
}