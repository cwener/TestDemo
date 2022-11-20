package com.example.test.activity

import android.app.Application

/**
 * @author: chengwen
 * @date: 2022/11/20
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ApplicationWrapper.instance = this
    }
}