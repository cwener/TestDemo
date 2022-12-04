package com.example.test.activity.acoptionstest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R

/**
 * @author: chengwen
 * @date: 2022/12/4
 */
class ActivityC: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}