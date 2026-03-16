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
    @GET("api/v1/recipes")
    suspend fun getRecipes(
        @Query("category") category: String? = null
    ): List<CloudRecipe>

    /**
     * 获取单个食谱
     * @param cloudId 云端食谱 ID
     * @return 食谱详情
     */
    @GET("api/v1/recipes/{cloudId}")
    suspend fun getRecipe(
        @Path("cloudId") cloudId: String
    ): CloudRecipe

    /**
     * 创建食谱
     * @param recipe 食谱数据
     * @return 创建的食谱
     */
    @POST("api/v1/recipes")
    suspend fun createRecipe(
        @Body recipe: CloudRecipe
    ): CloudRecipe

    /**
     * 更新食谱
     * @param cloudId 云端食谱 ID
     * @param recipe 食谱数据
     * @return 更新后的食谱
     */
    @PUT("api/v1/recipes/{cloudId}")
    suspend fun updateRecipe(
        @Path("cloudId") cloudId: String,
        @Body recipe: CloudRecipe
    ): CloudRecipe

    /**
     * 删除食谱
     * @param cloudId 云端食谱 ID
     */
    @DELETE("api/v1/recipes/{cloudId}")
    suspend fun deleteRecipe(
        @Path("cloudId") cloudId: String
    )
    
    /**
     * 获取候选食谱
     * 根据宝宝年龄、过敏食材、偏好等条件筛选候选食谱
     * @param request 候选食谱请求
     * @return 按餐次分类的候选食谱
     */
    @POST("api/v1/recipes/candidates")
    suspend fun getCandidates(
        @Body request: CandidateRecipesRequest
    ): CandidateRecipesResponse
}

/**
 * 候选食谱请求
 */
@kotlinx.serialization.Serializable
data class CandidateRecipesRequest(
    val ageInMonths: Int,
    val allergies: List<String> = emptyList(),
    val preferences: List<String> = emptyList(),
    val availableIngredients: List<String> = emptyList(),
    val avoidIngredients: List<String> = emptyList(),
    val useAvailableIngredientsOnly: Boolean = false
)

/**
 * 候选食谱响应
 */
@kotlinx.serialization.Serializable
data class CandidateRecipesResponse(
    val breakfast: List<CandidateRecipeItem> = emptyList(),
    val lunch: List<CandidateRecipeItem> = emptyList(),
    val dinner: List<CandidateRecipeItem> = emptyList(),
    val snack: List<CandidateRecipeItem> = emptyList(),
    val totalCount: Int,
    val filteredCount: Int
)

/**
 * 候选食谱项
 */
@kotlinx.serialization.Serializable
data class CandidateRecipeItem(
    val cloudId: String,
    val name: String,
    val minAgeMonths: Int,
    val maxAgeMonths: Int,
    val category: String,
    val isIronRich: Boolean = false,
    val ironContent: Double? = null,
    val textureType: String? = null
)