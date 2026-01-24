package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.CloudBaby
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 宝宝 API 服务接口
 */
interface BabyApiService {

    /**
     * 获取宝宝列表
     * @return 宝宝列表
     */
    @GET("babies")
    suspend fun getBabies(): List<CloudBaby>

    /**
     * 获取单个宝宝
     * @param cloudId 云端宝宝 ID
     * @return 宝宝详情
     */
    @GET("babies/{cloudId}")
    suspend fun getBaby(
        @Path("cloudId") cloudId: String
    ): CloudBaby

    /**
     * 创建宝宝
     * @param baby 宝宝数据
     * @return 创建的宝宝
     */
    @POST("babies")
    suspend fun createBaby(
        @Body baby: CloudBaby
    ): CloudBaby

    /**
     * 更新宝宝
     * @param cloudId 云端宝宝 ID
     * @param baby 宝宝数据
     * @return 更新后的宝宝
     */
    @PUT("babies/{cloudId}")
    suspend fun updateBaby(
        @Path("cloudId") cloudId: String,
        @Body baby: CloudBaby
    ): CloudBaby

    /**
     * 删除宝宝
     * @param cloudId 云端宝宝 ID
     */
    @DELETE("babies/{cloudId}")
    suspend fun deleteBaby(
        @Path("cloudId") cloudId: String
    )
}