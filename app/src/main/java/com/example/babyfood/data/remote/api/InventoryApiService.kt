package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.inventory.InventoryItemCreate
import com.example.babyfood.data.remote.dto.inventory.InventoryItemResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 库存管理 API 服务接口
 */
interface InventoryApiService {

    /**
     * 获取库存物品列表
     * @return 库存物品列表
     */
    @GET("/api/v1/inventory-items")
    suspend fun getInventoryItems(): List<InventoryItemResponse>

    /**
     * 创建库存物品
     * @param item 库存物品数据
     * @return 创建的库存物品
     */
    @POST("/api/v1/inventory-items")
    suspend fun createInventoryItem(
        @Body item: InventoryItemCreate
    ): InventoryItemResponse

    /**
     * 获取单个库存物品
     * @param cloudId 云端 ID
     * @return 库存物品详情
     */
    @GET("/api/v1/inventory-items/{cloudId}")
    suspend fun getInventoryItem(
        @Path("cloudId") cloudId: String
    ): InventoryItemResponse

    /**
     * 更新库存物品
     * @param cloudId 云端 ID
     * @param item 库存物品数据
     * @return 更新后的库存物品
     */
    @PUT("/api/v1/inventory-items/{cloudId}")
    suspend fun updateInventoryItem(
        @Path("cloudId") cloudId: String,
        @Body item: InventoryItemCreate
    ): InventoryItemResponse

    /**
     * 删除库存物品
     * @param cloudId 云端 ID
     */
    @DELETE("/api/v1/inventory-items/{cloudId}")
    suspend fun deleteInventoryItem(
        @Path("cloudId") cloudId: String
    )

    /**
     * 获取过期和紧急物品
     * @return 过期和紧急物品列表
     */
    @GET("/api/v1/inventory-items/expired-and-urgent")
    suspend fun getExpiredAndUrgentItems(): List<InventoryItemResponse>
}
