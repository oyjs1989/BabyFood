package com.example.babyfood.data.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.babyfood.data.remote.api.ImageAnalysisApiService
import com.example.babyfood.data.remote.dto.ImageAnalysisResultData
import com.example.babyfood.domain.model.ImageRecognitionRequest
import com.example.babyfood.domain.model.ImageRecognitionResponse
import com.example.babyfood.domain.model.NutritionInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 后端图像识别策略
 * 通过后端 API 代理调用 AI 图像识别服务，避免在前端暴露 API Key
 */
@Singleton
class BackendImageRecognitionStrategy @Inject constructor(
    private val imageAnalysisApiService: ImageAnalysisApiService
) : ImageRecognitionService {

    companion object {
        private const val TAG = "BackendImageRecognition"
        private const val ANALYSIS_TYPE_INGREDIENT = "INGREDIENT_IDENTIFICATION"
        private const val ANALYSIS_TYPE_RECIPE = "RECIPE_RECOGNITION"
    }

    override suspend fun recognizeFood(request: ImageRecognitionRequest): ImageRecognitionResponse {
        Log.d(TAG, "========== Start Backend Image Recognition ==========")
        Log.d(TAG, "Image format: ${request.imageFormat}")
        Log.d(TAG, "Image size: ${request.imageBase64.length} chars")

        return try {
            // 1. 解码 Base64 图像
            val imageBytes = Base64.decode(request.imageBase64, Base64.DEFAULT)
            Log.d(TAG, "Decoded image size: ${imageBytes.size} bytes")

            // 2. 创建临时文件
            val tempFile = createTempFile(imageBytes, request.imageFormat)
            Log.d(TAG, "Created temp file: ${tempFile.absolutePath}")

            // 3. 构建 Multipart 请求
            val mediaType = getMediaType(request.imageFormat)
            val requestFile = tempFile.asRequestBody(mediaType.toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
            val analysisTypePart = ANALYSIS_TYPE_INGREDIENT.toRequestBody("text/plain".toMediaType())
            
            Log.d(TAG, "Calling backend API...")
            
            // 4. 调用后端 API
            val response = imageAnalysisApiService.analyzeImage(
                image = imagePart,
                analysisType = analysisTypePart,
                babyId = null
            )

            // 5. 清理临时文件
            tempFile.delete()

            // 6. 处理响应
            if (!response.success) {
                Log.e(TAG, "Backend analysis failed: ${response.errorMessage}")
                throw ImageRecognitionException(response.errorMessage ?: "Unknown error")
            }

            val result = response.result
            if (result == null) {
                Log.e(TAG, "No result data in response")
                throw ImageRecognitionException("No result data in response")
            }

            // 7. 转换为 ImageRecognitionResponse
            val recognitionResponse = convertToRecognitionResponse(result)

            Log.d(TAG, "Food recognition successful: ${recognitionResponse.foodName}")
            Log.d(TAG, "Confidence: ${recognitionResponse.confidence}")
            Log.d(TAG, "Storage method: ${recognitionResponse.storageMethod}")
            Log.d(TAG, "Shelf life: ${recognitionResponse.estimatedShelfLife} days")
            Log.d(TAG, "========== Recognition Complete ==========")

            recognitionResponse

        } catch (e: ImageRecognitionException) {
            Log.e(TAG, "Image recognition failed: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Image recognition exception: ${e.message}", e)
            throw ImageRecognitionException("Image recognition failed: ${e.message}", e)
        }
    }

    private fun createTempFile(imageBytes: ByteArray, imageFormat: String): File {
        val tempFile = File.createTempFile("image_", ".${imageFormat.lowercase()}")
        FileOutputStream(tempFile).use { output ->
            output.write(imageBytes)
        }
        return tempFile
    }

    private fun getMediaType(imageFormat: String): String {
        return when (imageFormat.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/jpeg"
        }
    }

    private fun convertToRecognitionResponse(result: ImageAnalysisResultData): ImageRecognitionResponse {
        // 根据分析类型转换结果
        // INGREDIENT_IDENTIFICATION 返回食材识别结果
        val foodName = result.ingredientName ?: result.foodName ?: "Unknown"
        val storageMethod = result.storageMethod ?: "REFRIGERATOR"
        val estimatedShelfLife = result.estimatedShelfLifeDays ?: 7
        val confidence = result.confidence?.toFloat() ?: 0.8f

        val nutritionInfo = result.nutrition?.let {
            NutritionInfo(
                calories = it.calories?.toFloat() ?: 0f,
                protein = it.protein?.toFloat() ?: 0f,
                calcium = it.calcium?.toFloat() ?: 0f,
                iron = it.iron?.toFloat() ?: 0f
            )
        }

        return ImageRecognitionResponse(
            success = true,
            errorMessage = null,
            foodName = foodName,
            foodId = 0L,
            foodImageUrl = null,
            storageMethod = storageMethod,
            estimatedShelfLife = estimatedShelfLife,
            defaultUnit = "克",
            quantity = 0f,
            nutritionInfo = nutritionInfo,
            confidence = confidence,
            notes = result.safetyTips ?: result.description ?: result.notes
        )
    }
}
