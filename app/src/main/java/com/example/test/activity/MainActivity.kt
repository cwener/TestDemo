package com.example.test.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.test.activity.acoptionstest.ActivityA
import com.example.test.databinding.ActivityLayerTestBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLayerTestBinding.inflate(LayoutInflater.from(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            layerAc.setOnClickListener {
                startActivity(Intent(this@MainActivity, LayerTestActivity::class.java))
            }
            memoryAc.setOnClickListener {
                startActivity(Intent(this@MainActivity, MemoryActivity::class.java))
            }

            goVolume.setOnClickListener {
                startActivity(Intent(this@MainActivity, VolumeTestActivity::class.java))
            }

            goTrans.setOnClickListener {
                startActivity(Intent(this@MainActivity, ActivityA::class.java))
            }
        }
    }
}