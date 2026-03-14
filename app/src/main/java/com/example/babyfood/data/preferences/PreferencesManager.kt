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
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
    }

    /**
     * 保存选择的语言代码 (zh, en, auto)
     */
    fun saveLanguage(languageCode: String) {
        Log.d(TAG, "========== 保存语言设置 ==========")
        Log.d(TAG, "语言代码: $languageCode")
        prefs.edit().putString(KEY_SELECTED_LANGUAGE, languageCode).apply()
        Log.d(TAG, "✓ 保存成功")
        Log.d(TAG, "========== 保存完成 ==========")
    }

    /**
     * 获取选择的语言代码
     * @return 语言代码，默认为 "auto"
     */
    fun getLanguageCode(): String {
        return prefs.getString(KEY_SELECTED_LANGUAGE, "auto") ?: "auto"
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

    // ==================== 同步相关 ====================

    /**
     * 保存最后同步时间
     * @param time 同步时间戳（毫秒）
     */
    fun saveLastSyncTime(time: Long) {
        Log.d(TAG, "========== 保存最后同步时间 ==========")
        Log.d(TAG, "同步时间: $time")
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, time).apply()
        Log.d(TAG, "✓ 保存成功")
        Log.d(TAG, "========== 保存完成 ==========")
    }

    /**
     * 获取最后同步时间
     * @return 同步时间戳（毫秒），如果没有同步过则返回 null
     */
    fun getLastSyncTime(): Long? {
        val time = prefs.getLong(KEY_LAST_SYNC_TIME, -1L)
        Log.d(TAG, "========== 获取最后同步时间 ==========")
        Log.d(TAG, "同步时间: ${if (time == -1L) "从未同步" else time}")
        Log.d(TAG, "========== 获取完成 ==========")
        return if (time == -1L) null else time
    }

    /**
     * 清除最后同步时间
     */
    fun clearLastSyncTime() {
        Log.d(TAG, "========== 清除最后同步时间 ==========")
        prefs.edit().remove(KEY_LAST_SYNC_TIME).apply()
        Log.d(TAG, "✓ 清除成功")
        Log.d(TAG, "========== 清除完成 ==========")
    }
}