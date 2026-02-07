package com.example.babyfood.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Token 安全存储管理类
 *
 * 使用 EncryptedSharedPreferences 安全存储 JWT Token
 *
 * @property context 应用上下文
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "TokenStorage"

    /**
     * Android Keystore 主密钥
     */
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    /**
     * 加密的 SharedPreferences 实例
     */
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * 保存 Token 信息
     *
     * @param token 访问令牌
     * @param refreshToken 刷新令牌
     * @param expiresIn 过期时间（秒）
     * @param userId 用户 ID
     */
    fun saveToken(
        token: String,
        refreshToken: String,
        expiresIn: Long,
        userId: Long
    ) {
        android.util.Log.d(TAG, "========== 保存 Token 开始 ==========")
        android.util.Log.d(TAG, "用户 ID: $userId")
        android.util.Log.d(TAG, "Token: ${token.take(20)}...")
        android.util.Log.d(TAG, "过期时间: $expiresIn 秒")

        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_EXPIRES_AT, System.currentTimeMillis() + expiresIn * 1000)
            putLong(KEY_USER_ID, userId)
            apply()
        }

        android.util.Log.d(TAG, "✓ Token 保存成功")
        android.util.Log.d(TAG, "========== 保存 Token 结束 ==========")
    }

    /**
     * 获取访问令牌
     *
     * @return 访问令牌，如果已过期或不存在则返回 null
     */
    fun getToken(): String? {
        if (isTokenExpired()) {
            android.util.Log.d(TAG, "Token 已过期，清除 Token")
            clear()
            return null
        }
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    /**
     * 获取刷新令牌
     *
     * @return 刷新令牌
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * 获取用户 ID
     *
     * @return 用户 ID
     */
    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    /**
     * 检查 Token 是否已过期
     *
     * @return true 如果已过期，false 否则
     */
    private fun isTokenExpired(): Boolean {
        val expiresAt = sharedPreferences.getLong(KEY_EXPIRES_AT, 0L)
        val isExpired = System.currentTimeMillis() >= expiresAt
        android.util.Log.d(TAG, "Token 过期检查: ${if (isExpired) "已过期" else "有效"}")
        return isExpired
    }

    /**
     * 清除所有 Token 信息
     */
    fun clear() {
        android.util.Log.d(TAG, "========== 清除 Token ==========")
        sharedPreferences.edit().clear().apply()
        android.util.Log.d(TAG, "✓ Token 已清除")
    }

    /**
     * 检查是否已登录
     *
     * @return true 如果有有效的 Token，false 否则
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EXPIRES_AT = "expires_at"
    }
}