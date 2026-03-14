package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.nutrition.FreshnessAdviceResponse
import com.example.babyfood.data.remote.dto.nutrition.NutritionDataResponse
import com.example.babyfood.data.remote.dto.nutrition.NutritionGoalsResponse
import com.example.babyfood.data.remote.dto.nutrition.NutritionGoalsUpdate
import com.example.babyfood.data.remote.dto.nutrition.NutritionIntakeResponse
import com.example.babyfood.data.remote.dto.nutrition.RuleVersionResponse
import com.example.babyfood.data.remote.dto.nutrition.SafetyRiskResponse
import com.example.babyfood.data.remote.dto.nutrition.TextureAdviceResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 营养指导 API 服务接口
 * 用于调用后端营养指导相关 API
 */
interface NutritionApiService {

    // ==================== 规则版本 ====================

    /**
     * 获取规则版本
     */
    @GET("api/v1/nutrition/rules/version")
    suspend fun getRuleVersion(): RuleVersionResponse

    // ==================== 安全风险 ====================

    /**
     * 获取食材安全风险
     * @param ingredientName 食材名称
     * @param ageInMonths 宝宝月龄
     * @return 安全风险信息
     */
    @GET("api/v1/nutrition/safety-risks/{ingredientName}")
    suspend fun getSafetyRisk(
        @Path("ingredientName") ingredientName: String,
        @Query("age_in_months") ageInMonths: Int
    ): SafetyRiskResponse

    /**
     * 获取所有安全风险
     * @param ageInMonths 宝宝月龄（可选）
     * @return 安全风险列表
     */
    @GET("api/v1/nutrition/safety-risks")
    suspend fun getAllSafetyRisks(
        @Query("age_in_months") ageInMonths: Int? = null
    ): List<SafetyRiskResponse>

    /**
     * 获取高风险食材
     */
    @GET("api/v1/nutrition/safety-risks/high-risk")
    suspend fun getHighRiskIngredients(
        @Query("age_in_months") ageInMonths: Int? = null
    ): List<SafetyRiskResponse>

    // ==================== 营养数据 ====================

    /**
     * 获取食材营养数据
     * @param ingredientName 食材名称
     * @return 营养数据
     */
    @GET("api/v1/nutrition/nutrition-data/{ingredientName}")
    suspend fun getNutritionData(
        @Path("ingredientName") ingredientName: String
    ): NutritionDataResponse

    /**
     * 获取所有营养数据
     * @return 营养数据列表
     */
    @GET("api/v1/nutrition/nutrition-data")
    suspend fun getAllNutritionData(): List<NutritionDataResponse>

    /**
     * 获取高铁食材
     * @param threshold 铁含量阈值
     * @return 高铁食材列表
     */
    @GET("api/v1/nutrition/nutrition-data/high-iron")
    suspend fun getHighIronIngredients(
        @Query("threshold") threshold: Double = 2.0
    ): List<NutritionDataResponse>

    // ==================== 营养目标 ====================

    /**
     * 获取宝宝营养目标
     * @param babyId 宝宝 ID
     * @return 营养目标
     */
    @GET("api/v1/nutrition/nutrition-goals/{babyId}")
    suspend fun getNutritionGoals(
        @Path("babyId") babyId: Int
    ): NutritionGoalsResponse

    /**
     * 更新宝宝营养目标
     * @param babyId 宝宝 ID
     * @param goals 营养目标数据
     * @return 更新后的营养目标
     */
    @PUT("api/v1/nutrition/nutrition-goals/{babyId}")
    suspend fun updateNutritionGoals(
        @Path("babyId") babyId: Int,
        @Body goals: NutritionGoalsUpdate
    ): NutritionGoalsResponse

    // ==================== 营养摄入 ====================

    /**
     * 计算营养摄入
     * @param babyId 宝宝 ID
     * @param intake 摄入数据
     * @return 营养摄入分析结果
     */
    @POST("api/v1/nutrition/nutrition-intake/{babyId}")
    suspend fun calculateNutritionIntake(
        @Path("babyId") babyId: Int,
        @Body intake: Map<String, Double>
    ): NutritionIntakeResponse

    // ==================== 质地建议 ====================

    /**
     * 获取质地建议
     * @param ageInMonths 宝宝月龄
     * @return 质地建议
     */
    @GET("api/v1/nutrition/texture-advice/{ageInMonths}")
    suspend fun getTextureAdvice(
        @Path("ageInMonths") ageInMonths: Int
    ): TextureAdviceResponse

    // ==================== 新鲜度建议 ====================

    /**
     * 获取新鲜度建议
     * @param ingredientName 食材名称
     * @param ageInMonths 宝宝月龄
     * @return 新鲜度建议
     */
    @GET("api/v1/nutrition/freshness-advice")
    suspend fun getFreshnessAdvice(
        @Query("ingredient_name") ingredientName: String,
        @Query("age_in_months") ageInMonths: Int
    ): FreshnessAdviceResponse
}
