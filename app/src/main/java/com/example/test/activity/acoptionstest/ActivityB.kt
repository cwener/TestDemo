package com.example.test.activity.acoptionstest

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.test.R

/**
 * @author: chengwen
 * @date: 2022/11/25
 */
class ActivityB: FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}