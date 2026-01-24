package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.SyncPullResponse
import com.example.babyfood.data.remote.dto.SyncPushRequest
import com.example.babyfood.data.remote.dto.SyncPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 同步 API 服务接口
 */
interface SyncApiService {

    /**
     * 拉取更新
     * @param lastSyncTime 上次同步时间（毫秒时间戳）
     * @return 同步拉取响应
     */
    @GET("sync/pull")
    suspend fun pull(
        @Query("lastSyncTime") lastSyncTime: Long?
    ): SyncPullResponse

    /**
     * 推送更新
     * @param request 同步推送请求
     * @return 同步推送响应
     */
    @POST("sync/push")
    suspend fun push(
        @Body request: SyncPushRequest
    ): SyncPushResponse
}