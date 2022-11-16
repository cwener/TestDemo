package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.test.databinding.AcMemoryBinding


/**
 * @author: chengwen
 * @date: 2022/11/16
 */
class MemoryActivity: FragmentActivity(), CallBack {

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //模拟内存抖动的场景，每隔10毫秒执行一次，循环执行100次，每次通过new分配大内存
            for (i in 0..99) {
                val obj = arrayOfNulls<String>(100000)
            }
            this.sendEmptyMessageDelayed(0, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = AcMemoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.btnMemory.setOnClickListener {
//            mHandler.sendEmptyMessage(0)
        }
        CallBackManager.addCallBack(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null);
    }

    override fun dpOperate() {

    }
}

//模拟回调处理某些业务场景
interface CallBack {
    fun dpOperate()
}

//统一管理Callback
object CallBackManager {
    var sCallBacks: ArrayList<CallBack> = ArrayList()
    fun addCallBack(callBack: CallBack) {
        sCallBacks.add(callBack)
    }

    fun removeCallBack(callBack: CallBack) {
        sCallBacks.remove(callBack)
    }
}