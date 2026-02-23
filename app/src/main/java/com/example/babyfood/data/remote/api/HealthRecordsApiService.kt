package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.health_records.HealthRecordCreate
import com.example.babyfood.data.remote.dto.health_records.HealthRecordResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 健康记录 API 服务接口
 */
interface HealthRecordsApiService {

    /**
     * 获取宝宝体检记录列表
     * @param babyId 宝宝 ID
     * @return 体检记录列表
     */
    @GET("/api/v1/babies/{babyId}/health-records")
    suspend fun getHealthRecords(
        @Path("babyId") babyId: Int
    ): List<HealthRecordResponse>

    /**
     * 创建体检记录
     * @param babyId 宝宝 ID
     * @param record 体检记录数据
     * @return 创建的体检记录
     */
    @POST("/api/v1/babies/{babyId}/health-records")
    suspend fun createHealthRecord(
        @Path("babyId") babyId: Int,
        @Body record: HealthRecordCreate
    ): HealthRecordResponse

    /**
     * 获取单个体检记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     * @return 体检记录详情
     */
    @GET("/api/v1/babies/{babyId}/health-records/{recordId}")
    suspend fun getHealthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int
    ): HealthRecordResponse

    /**
     * 更新体检记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     * @param record 体检记录数据
     * @return 更新后的体检记录
     */
    @PUT("/api/v1/babies/{babyId}/health-records/{recordId}")
    suspend fun updateHealthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int,
        @Body record: HealthRecordCreate
    ): HealthRecordResponse

    /**
     * 删除体检记录
     * @param babyId 宝宝 ID
     * @param recordId 记录 ID
     */
    @DELETE("/api/v1/babies/{babyId}/health-records/{recordId}")
    suspend fun deleteHealthRecord(
        @Path("babyId") babyId: Int,
        @Path("recordId") recordId: Int
    )
}
