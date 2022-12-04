package com.example.test.activity.acoptionstest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import com.example.test.activity.ApplicationWrapper

/**
 * @author: chengwen
 * @date: 2022/11/25
 */
class ActivityB: FragmentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        val viewModel = ViewModelProvider(this)[ActivityBViewModel::class.java]
        handler.postDelayed(Runnable {
            viewModel.delayLiveDate.value = 10
        }, 10 * 1000L)
        viewModel.delayLiveDate.observe(this, Observer {
            Toast.makeText(ApplicationWrapper.instance, "${viewModel.delayLiveDate.value}", Toast.LENGTH_SHORT).show()
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}