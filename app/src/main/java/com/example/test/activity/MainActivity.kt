package com.example.test.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import com.example.test.activity.acoptionstest.ActivityA
import com.example.test.activity.land.LandActivity3
import com.example.test.databinding.ActivityLayerTestBinding
import com.example.test.view.DSLNotificationDialog
import com.facebook.drawee.backends.pipeline.Fresco

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLayerTestBinding.inflate(LayoutInflater.from(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
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

            goDialog.setOnClickListener {
                val dialog = DSLNotificationDialog(this@MainActivity, R.style.pro_dialog)
                dialog.show()
            }

            goFontTest.setOnClickListener {
                startActivity(Intent(this@MainActivity, FontTestActivity::class.java))
            }

            goLandTest.setOnClickListener {
                startActivity(Intent(this@MainActivity, LandActivity3::class.java))
            }
        }
    }
}