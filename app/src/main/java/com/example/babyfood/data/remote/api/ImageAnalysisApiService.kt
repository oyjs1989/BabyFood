package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.ImageAnalysisResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 图像分析 API 服务接口
 * 用于调用后端图像识别代理 API
 */
interface ImageAnalysisApiService {

    /**
     * 分析图像
     *
     * @param image 图像文件（multipart/form-data）
     * @param analysisType 分析类型（RECIPE_RECOGNITION, HEALTH_RECORD_OCR, INGREDIENT_IDENTIFICATION）
     * @param babyId 宝宝 ID（可选）
     * @return 分析结果
     */
    @Multipart
    @POST("/api/v1/images/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part,
        @Part("analysisType") analysisType: RequestBody,
        @Part("babyId") babyId: RequestBody?
    ): ImageAnalysisResult
}
