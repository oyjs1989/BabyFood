package com.example.babyfood.data.remote.interceptor

import android.util.Log
import com.example.babyfood.data.local.TokenStorage
import com.example.babyfood.data.remote.api.AuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Token 刷新认证器
 *
 * 当服务端返回 401 时，尝试使用 refresh token 自动换取新的 access token，
 * 成功后重放原请求；失败时清理本地会话状态，由 UI 跳转登录页。
 */
class TokenRefreshAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApiService: AuthApiService
) : Authenticator {

    companion object {
        private const val TAG = "TokenRefreshAuth"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Log.w(TAG, "⚠️ 已达到最大重试次数，停止自动刷新")
            tokenStorage.clear()
            return null
        }

        val path = response.request.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
            Log.w(TAG, "⚠️ 当前为认证相关接口，不执行自动刷新: $path")
            return null
        }

        val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()
        val latestToken = tokenStorage.getToken()
        if (!requestToken.isNullOrBlank() && !latestToken.isNullOrBlank() && requestToken != latestToken) {
            Log.d(TAG, "检测到 token 已被其他请求刷新，直接重试原请求")
            return response.request.newBuilder()
                .header("Authorization", "Bearer $latestToken")
                .build()
        }

        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Log.w(TAG, "⚠️ 未找到 refresh token，清理本地会话")
            tokenStorage.clear()
            return null
        }

        return synchronized(this) {
            val currentToken = tokenStorage.getToken()
            if (!requestToken.isNullOrBlank() && !currentToken.isNullOrBlank() && requestToken != currentToken) {
                Log.d(TAG, "进入同步块后发现 token 已刷新，直接重试原请求")
                return@synchronized response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshResponse = runBlocking {
                runCatching { authApiService.refreshToken(refreshToken) }
            }.getOrElse { error ->
                Log.e(TAG, "❌ 自动刷新 token 失败: ${error.message}", error)
                tokenStorage.clear()
                return@synchronized null
            }

            if (!refreshResponse.success || refreshResponse.token.isNullOrBlank() || refreshResponse.user == null) {
                Log.w(TAG, "⚠️ 刷新接口返回失败，清理本地会话")
                tokenStorage.clear()
                return@synchronized null
            }

            tokenStorage.saveToken(
                token = refreshResponse.token,
                refreshToken = refreshResponse.refreshToken ?: refreshToken,
                expiresIn = refreshResponse.expiresIn,
                userId = refreshResponse.user.id
            )

            Log.d(TAG, "✓ 自动刷新 token 成功，重试原请求")
            response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.token}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
