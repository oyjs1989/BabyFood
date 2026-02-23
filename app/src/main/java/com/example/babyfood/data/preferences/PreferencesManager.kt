package com.example.babyfood.data.preferences

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPreferences 管理类
 * 用于持久化存储应用的配置和状态信息
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("BabyFoodPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "PreferencesManager"
        private const val KEY_SELECTED_BABY_ID = "selected_baby_id"
    }

    /**
     * 保存当前选中的宝宝 ID
     */
    fun saveSelectedBabyId(babyId: Long) {
        Log.d(TAG, "========== 保存选中的宝宝 ID ==========")
        Log.d(TAG, "宝宝 ID: $babyId")
        prefs.edit().putLong(KEY_SELECTED_BABY_ID, babyId).apply()
        Log.d(TAG, "✓ 保存成功")
        Log.d(TAG, "========== 保存完成 ==========")
    }

    /**
     * 获取当前选中的宝宝 ID
     * @return 宝宝 ID，如果没有保存则返回 -1
     */
    fun getSelectedBabyId(): Long {
        val babyId = prefs.getLong(KEY_SELECTED_BABY_ID, -1L)
        Log.d(TAG, "========== 获取选中的宝宝 ID ==========")
        Log.d(TAG, "宝宝 ID: $babyId")
        Log.d(TAG, "========== 获取完成 ==========")
        return babyId
    }

    /**
     * 清除选中的宝宝 ID
     */
    fun clearSelectedBabyId() {
        Log.d(TAG, "========== 清除选中的宝宝 ID ==========")
        prefs.edit().remove(KEY_SELECTED_BABY_ID).apply()
        Log.d(TAG, "✓ 清除成功")
        Log.d(TAG, "========== 清除完成 ==========")
    }
}