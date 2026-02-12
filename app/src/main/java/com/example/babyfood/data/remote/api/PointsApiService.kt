package com.example.babyfood.data.remote.api

import com.example.babyfood.domain.model.CheckInResponse
import com.example.babyfood.domain.model.PointsHistoryResponse
import com.example.babyfood.domain.model.PointsInfo
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 积分系统 API 服务接口
 */
interface PointsApiService {

    /**
     * 每日签到
     * @return 签到响应
     */
    @POST("auth/check-in")
    suspend fun dailyCheckIn(): CheckInResponse

    /**
     * 获取用户积分信息
     * @return 积分信息响应
     */
    @GET("api/v1/users/points")
    suspend fun getPointsInfo(): PointsInfo

    /**
     * 获取积分交易历史
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 积分历史响应
     */
    @GET("api/v1/users/points/history")
    suspend fun getPointsHistory(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PointsHistoryResponse
}