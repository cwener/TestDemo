package com.example.test.activity.land

/**
 * @author ChengWen
 * @date：2025/6/3 18:19
 * @desc：
 */
interface ListState {
    companion object {
        // 切歌态
        const val SwitchMusic = 0
        // 进入列表转场
        const val TransToList = 1
        // 列表完全态
        const val ListCompletely = 2
        // 列表退场进入切歌态 - 前期滑动列表
        const val ListTransToSwitchScrollToTarget = 3
        // 列表退场进入切歌态 - 后期列表渐隐彻底蜕变为切歌态
        const val ListTransToSwitchFadeExit = 4
    }
}