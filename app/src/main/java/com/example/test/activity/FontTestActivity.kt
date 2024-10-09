package com.example.test.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.test.databinding.ActivityFontTestBinding
import com.example.test.utils.DimensionUtils

/**
 * @author: chengwen
 * @date: 2022/11/1
 */
class FontTestActivity: FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFontTestBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.left.setCustomText("神秘歌友推荐你听《Cant Get Your Love》")
//        binding.left.setLineSpacing(DimensionUtils.dpToPxF(5f), 1f)
        binding.right.setCustomText("神秘歌友推荐你听《Cant Get Your Love》")
//        binding.right.setLineSpacing(DimensionUtils.dpToPxF(5f), 1f)

        binding.abTestLeft.text = "神秘歌友推荐你听《Cant Get Your Love》"
        binding.abTestRight.text = "神秘歌友推荐你听《Cant Get Your Love》"
    }

}
