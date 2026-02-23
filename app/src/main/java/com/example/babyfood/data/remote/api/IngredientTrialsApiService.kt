package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.ingredient_trials.FlavorDiversityResponse
import com.example.babyfood.data.remote.dto.ingredient_trials.IngredientTrialCreate
import com.example.babyfood.data.remote.dto.ingredient_trials.IngredientTrialResponse
import com.example.babyfood.data.remote.dto.ingredient_trials.IngredientTrialUpdate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 食材尝试 API 服务接口
 */
interface IngredientTrialsApiService {

    /**
     * 创建食材尝试记录
     * @param trial 尝试记录数据
     * @return 创建的尝试记录
     */
    @POST("/api/v1/ingredient-trials")
    suspend fun createTrial(@Body trial: IngredientTrialCreate): IngredientTrialResponse

    /**
     * 获取单个食材尝试记录
     * @param trialId 记录 ID
     * @return 尝试记录详情
     */
    @GET("/api/v1/ingredient-trials/{trialId}")
    suspend fun getTrial(@Path("trialId") trialId: Int): IngredientTrialResponse

    /**
     * 更新食材尝试记录
     * @param trialId 记录 ID
     * @param update 更新数据
     * @return 更新后的尝试记录
     */
    @PUT("/api/v1/ingredient-trials/{trialId}")
    suspend fun updateTrial(
        @Path("trialId") trialId: Int,
        @Body update: IngredientTrialUpdate
    ): IngredientTrialResponse

    /**
     * 删除食材尝试记录
     * @param trialId 记录 ID
     */
    @DELETE("/api/v1/ingredient-trials/{trialId}")
    suspend fun deleteTrial(@Path("trialId") trialId: Int)

    /**
     * 获取宝宝的所有食材尝试记录
     * @param babyId 宝宝 ID
     * @return 尝试记录列表
     */
    @GET("/api/v1/babies/{babyId}/ingredient-trials")
    suspend fun getTrialsByBaby(@Path("babyId") babyId: Int): List<IngredientTrialResponse>

    /**
     * 获取口味多样性评分
     * @param babyId 宝宝 ID
     * @param totalIngredientsDb 数据库中食材总数（可选）
     * @return 口味多样性响应
     */
    @GET("/api/v1/babies/{babyId}/flavor-diversity")
    suspend fun getFlavorDiversity(
        @Path("babyId") babyId: Int,
        @Query("total_ingredients_db") totalIngredientsDb: Int = 500
    ): FlavorDiversityResponse

    /**
     * 获取过敏食材列表
     * @param babyId 宝宝 ID
     * @return 过敏食材名称列表
     */
    @GET("/api/v1/babies/{babyId}/allergens")
    suspend fun getAllergens(@Path("babyId") babyId: Int): List<String>

    /**
     * 获取已尝试食材列表
     * @param babyId 宝宝 ID
     * @return 已尝试食材名称列表
     */
    @GET("/api/v1/babies/{babyId}/tried-ingredients")
    suspend fun getTriedIngredients(@Path("babyId") babyId: Int): List<String>
}
