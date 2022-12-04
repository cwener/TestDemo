package com.example.test.activity.acoptionstest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.example.test.R
import com.example.test.databinding.ActivityABinding

/**
 * @author: chengwen
 * @date: 2022/11/25
 */
class ActivityA: FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityABinding.inflate(LayoutInflater.from(this))
        binding.entrance.setOnClickListener {
//            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.shareImg, "shareElement").toBundle()
//            val intent = Intent(this, ActivityB::class.java)
//            startActivity(intent, bundle)

            val intentB = Intent(this, ActivityB::class.java)
            val intentC = Intent(this, ActivityC::class.java)
            startActivities(arrayOf(intentB, intentC))
            overridePendingTransition(0, 0)
        }
        setContentView(binding.root)
    }
}