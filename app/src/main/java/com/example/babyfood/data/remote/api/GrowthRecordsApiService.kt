package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.growth_records.GrowthAssessmentResponse
import com.example.babyfood.data.remote.dto.growth_records.GrowthRecordCreate
import com.example.babyfood.data.remote.dto.growth_records.GrowthRecordResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 生长记录 API 服务接口
 */
interface GrowthRecordsApiService {

    /**
     * 获取宝宝生长记录列表
     * @param babyId 宝宝 ID
     * @return 生长记录列表
     */
    @GET("/api/v1/babies/{babyId}/growth-records")
    suspend fun getGrowthRecords(
        @Path("babyId") babyId: Int
    ): List<GrowthRecordResponse>

    /**
     * 创建生长记录
     * @param babyId 宝宝 ID
     * @param record 生长记录数据
     * @return 创建的生长记录
     */
    @POST("/api/v1/babies/{babyId}/growth-records")
    suspend fun createGrowthRecord(
        @Path("babyId") babyId: Int,
        @Body record: GrowthRecordCreate
    ): GrowthRecordResponse

    /**
     * 获取单个生长记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     * @return 生长记录详情
     */
    @GET("/api/v1/babies/{babyId}/growth-records/{recordId}")
    suspend fun getGrowthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int
    ): GrowthRecordResponse

    /**
     * 更新生长记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     * @param record 生长记录数据
     * @return 更新后的生长记录
     */
    @PUT("/api/v1/babies/{babyId}/growth-records/{recordId}")
    suspend fun updateGrowthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int,
        @Body record: GrowthRecordCreate
    ): GrowthRecordResponse

    /**
     * 删除生长记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     */
    @DELETE("/api/v1/babies/{babyId}/growth-records/{recordId}")
    suspend fun deleteGrowthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int
    )

    /**
     * 获取生长评估
     * @param babyId 宝宝 ID
     * @return 生长评估结果
     */
    @GET("/api/v1/babies/{babyId}/growth-assessment")
    suspend fun getGrowthAssessment(
        @Path("babyId") babyId: Int
    ): GrowthAssessmentResponse
}
