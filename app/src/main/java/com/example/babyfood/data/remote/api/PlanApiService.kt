package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.CloudPlan
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 餐单计划 API 服务接口
 */
interface PlanApiService {

    /**
     * 获取计划列表
     * @param cloudBabyId 可选，按宝宝 ID 筛选
     * @return 计划列表
     */
    @GET("api/v1/meal-plans")
    suspend fun getPlans(
        @Query("cloudBabyId") cloudBabyId: String? = null
    ): List<CloudPlan>

    /**
     * 获取单个计划
     * @param cloudId 云端计划 ID
     * @return 计划详情
     */
    @GET("api/v1/meal-plans/{cloudId}")
    suspend fun getPlan(
        @Path("cloudId") cloudId: String
    ): CloudPlan

    /**
     * 创建计划
     * @param plan 计划数据
     * @return 创建的计划
     */
    @POST("api/v1/meal-plans")
    suspend fun createPlan(
        @Body plan: CloudPlan
    ): CloudPlan

    /**
     * 更新计划
     * @param cloudId 云端计划 ID
     * @param plan 计划数据
     * @return 更新后的计划
     */
    @PUT("api/v1/meal-plans/{cloudId}")
    suspend fun updatePlan(
        @Path("cloudId") cloudId: String,
        @Body plan: CloudPlan
    ): CloudPlan

    /**
     * 删除计划
     * @param cloudId 云端计划 ID
     */
    @DELETE("api/v1/meal-plans/{cloudId}")
    suspend fun deletePlan(
        @Path("cloudId") cloudId: String
    )
}