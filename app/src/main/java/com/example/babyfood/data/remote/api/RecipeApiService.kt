package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.CloudRecipe
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 食谱 API 服务接口
 */
interface RecipeApiService {

    /**
     * 获取食谱列表
     * @param category 可选，按分类筛选
     * @return 食谱列表
     */
    @GET("recipes")
    suspend fun getRecipes(
        @Query("category") category: String? = null
    ): List<CloudRecipe>

    /**
     * 获取单个食谱
     * @param cloudId 云端食谱 ID
     * @return 食谱详情
     */
    @GET("recipes/{cloudId}")
    suspend fun getRecipe(
        @Path("cloudId") cloudId: String
    ): CloudRecipe

    /**
     * 创建食谱
     * @param recipe 食谱数据
     * @return 创建的食谱
     */
    @POST("recipes")
    suspend fun createRecipe(
        @Body recipe: CloudRecipe
    ): CloudRecipe

    /**
     * 更新食谱
     * @param cloudId 云端食谱 ID
     * @param recipe 食谱数据
     * @return 更新后的食谱
     */
    @PUT("recipes/{cloudId}")
    suspend fun updateRecipe(
        @Path("cloudId") cloudId: String,
        @Body recipe: CloudRecipe
    ): CloudRecipe

    /**
     * 删除食谱
     * @param cloudId 云端食谱 ID
     */
    @DELETE("recipes/{cloudId}")
    suspend fun deleteRecipe(
        @Path("cloudId") cloudId: String
    )
}