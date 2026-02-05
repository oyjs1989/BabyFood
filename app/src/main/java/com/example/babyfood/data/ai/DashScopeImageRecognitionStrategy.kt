package com.example.babyfood.data.ai

import android.util.Log
import com.example.babyfood.domain.model.ImageRecognitionRequest
import com.example.babyfood.domain.model.ImageRecognitionResponse
import com.example.babyfood.domain.model.NutritionInfo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashScopeImageRecognitionStrategy @Inject constructor() : ImageRecognitionService {

    companion object {
        private const val TAG = "ImageRecognitionStrategy"
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${getApiKey()}")
                    .header("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val dashScopeApi: ImageDashScopeApi by lazy {
        retrofit.create(ImageDashScopeApi::class.java)
    }

    override suspend fun recognizeFood(request: ImageRecognitionRequest): ImageRecognitionResponse {
        Log.d(TAG, "========== Start Food Recognition ==========")
        Log.d(TAG, "Image format: ${request.imageFormat}")
        Log.d(TAG, "Image size: ${request.imageBase64.length} chars")

        return try {
            val prompt = buildPrompt()
            val imageContent = buildImageContent(request)
            val textContent = ImageTextContent(text = prompt)

            val chatRequest = ImageChatCompletionRequest(
                model = "qwen-vl-plus",
                messages = listOf(
                    ImageMessage(
                        role = "user",
                        content = listOf(imageContent, textContent)
                    )
                )
            )

            Log.d(TAG, "Calling DashScope API...")
            val response = dashScopeApi.chatCompletions(chatRequest)

            val contentList = response.choices.firstOrNull()?.message?.content
                ?: throw ImageRecognitionException("API returned empty response")
            
            val content = contentList
                .filterIsInstance<ImageTextContent>()
                .firstOrNull()?.text
                ?: throw ImageRecognitionException("API response does not contain text")

            Log.d(TAG, "AI response content: $content")

            val result = parseJsonResponse(content)

            Log.d(TAG, "Food recognition successful: ${result.foodName}")
            Log.d(TAG, "Confidence: ${result.confidence}")
            Log.d(TAG, "Storage method: ${result.storageMethod}")
            Log.d(TAG, "Shelf life: ${result.estimatedShelfLife} days")
            Log.d(TAG, "========== Recognition Complete ==========")

            result

        } catch (e: ImageRecognitionException) {
            Log.e(TAG, "Food recognition failed: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Food recognition exception: ${e.message}")
            throw ImageRecognitionException("Image recognition failed: ${e.message}", e)
        }
    }

    private fun buildImageContent(request: ImageRecognitionRequest): ImageContent {
        val mimeType = when (request.imageFormat.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }

        return ImageContent(
            type = "image",
            mimeType = mimeType,
            data = request.imageBase64
        )
    }

    private fun buildPrompt(): String {
        return """
You are a professional food nutritionist and food recognition expert. Please identify the food from the uploaded image and extract key information.

Please return data in strict JSON format with the following fields:
{
  "foodName": "Food name (e.g., carrot, beef, pumpkin)",
  "foodId": 0,
  "foodImageUrl": null,
  "storageMethod": "Storage method (REFRIGERATOR/FREEZER/ROOM_TEMP)",
  "estimatedShelfLife": "Estimated shelf life in days (integer)",
  "defaultUnit": "Default unit (e.g., g, kg, piece)",
  "quantity": 0,
  "nutritionInfo": {
    "calories": "Calories per 100g (kcal)",
    "protein": "Protein per 100g (g)",
    "calcium": "Calcium per 100g (mg)",
    "iron": "Iron per 100g (mg)"
  },
  "confidence": "Recognition confidence (decimal between 0-1)",
  "notes": "Notes (optional)"
}

Recognition rules:
1. Prioritize specific food names (e.g., "carrot" instead of "vegetable")
2. Determine storage method based on food characteristics:
   - REFRIGERATOR: vegetables, fruits, meat, dairy, eggs
   - FREEZER: meat, seafood, frozen foods
   - ROOM_TEMP: dry goods, seasonings, canned goods
3. Estimate shelf life based on food characteristics:
   - Vegetables: 3-7 days
   - Fruits: 5-14 days
   - Meat (refrigerated): 3-5 days
   - Meat (frozen): 90-180 days
   - Eggs: 21 days
   - Dairy: 7-14 days
   - Dry goods/seasonings: 180-365 days
4. Nutrition information based on common food nutrition tables (per 100g)
5. confidence value indicates recognition confidence, 0.8-1.0 for clear images, 0.5-0.8 for blurry images
6. notes can include food variety, origin, etc.

If the image is blurry or cannot be recognized, return:
{
  "error": "Cannot recognize, please retake or enter manually"
}

Please return only JSON format, do not include other text descriptions.
""".trimIndent()
    }

    private fun parseJsonResponse(content: String): ImageRecognitionResponse {
        return try {
            val jsonContent = extractJson(content)
            val parsed = json.decodeFromString<RecognitionJsonResponse>(jsonContent)

            if (parsed.error != null) {
                throw ImageRecognitionException(parsed.error)
            }

            ImageRecognitionResponse(
                success = true,
                errorMessage = null,
                foodName = parsed.foodName,
                foodId = parsed.foodId,
                foodImageUrl = parsed.foodImageUrl,
                storageMethod = parsed.storageMethod,
                estimatedShelfLife = parsed.estimatedShelfLife,
                defaultUnit = parsed.defaultUnit,
                quantity = parsed.quantity,
                nutritionInfo = parsed.nutritionInfo?.let {
                    NutritionInfo(
                        calories = it.calories,
                        protein = it.protein,
                        calcium = it.calcium,
                        iron = it.iron
                    )
                },
                confidence = parsed.confidence,
                notes = parsed.notes
            )
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing failed: ${e.message}")
            Log.e(TAG, "Original content: $content")
            throw ImageRecognitionException("Failed to parse recognition result: ${e.message}", e)
        }
    }

    private fun extractJson(content: String): String {
        var jsonContent = content.trim()
        
        if (jsonContent.startsWith("```json")) {
            jsonContent = jsonContent.substring(7)
        } else if (jsonContent.startsWith("```")) {
            jsonContent = jsonContent.substring(3)
        }
        
        if (jsonContent.endsWith("```")) {
            jsonContent = jsonContent.substring(0, jsonContent.length - 3)
        }
        
        return jsonContent.trim()
    }

    private fun getApiKey(): String {
        val envApiKey = System.getenv("DASHSCOPE_API_KEY")
        if (!envApiKey.isNullOrBlank()) {
            return envApiKey
        }

        return "sk-aa30af1a40a643cbb8f43881c1ebbb49"
    }
}

private interface ImageDashScopeApi {
    @retrofit2.http.POST("chat/completions")
    suspend fun chatCompletions(
        @retrofit2.http.Body request: ImageChatCompletionRequest
    ): ImageChatCompletionResponse
}

@Serializable
private data class ImageChatCompletionRequest(
    val model: String,
    val messages: List<ImageMessage>
)

@Serializable
private data class ImageMessage(
    val role: String,
    val content: List<ImageContentBase>
)

@Serializable
private sealed class ImageContentBase

@Serializable
private data class ImageContent(
    val type: String = "image",
    val mimeType: String,
    val data: String
) : ImageContentBase()

@Serializable
private data class ImageTextContent(
    val type: String = "text",
    val text: String
) : ImageContentBase()

@Serializable
private data class ImageChatCompletionResponse(
    val id: String,
    @SerialName("object")
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<ImageChoice>,
    val usage: ImageUsage?
)

@Serializable
private data class ImageChoice(
    val index: Int,
    val message: ImageMessage,
    @SerialName("finish_reason")
    val finishReason: String
)

@Serializable
private data class ImageUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
private data class RecognitionJsonResponse(
    val foodName: String,
    val foodId: Long = 0L,
    val foodImageUrl: String? = null,
    val storageMethod: String,
    val estimatedShelfLife: Int,
    val defaultUnit: String,
    val quantity: Float = 0f,
    val nutritionInfo: NutritionInfoJson? = null,
    val confidence: Float,
    val notes: String? = null,
    val error: String? = null
)

@Serializable
private data class NutritionInfoJson(
    val calories: Float,
    val protein: Float,
    val calcium: Float,
    val iron: Float
)