package com.example.test.activity

import android.app.Application

/**
 * @author: chengwen
 * @date: 2022/11/20
 */

object ApplicationWrapper: Application() {
    @JvmField
    var instance: Application = this
}