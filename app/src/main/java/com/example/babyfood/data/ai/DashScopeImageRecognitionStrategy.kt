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
import java.util.Locale
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

                // 打印请求信息
                Log.d(TAG, "========== HTTP Request ==========")
                Log.d(TAG, "URL: ${originalRequest.url}")
                Log.d(TAG, "Method: ${originalRequest.method}")
                Log.d(TAG, "Headers: ${originalRequest.headers}")

                val requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${getApiKey()}")
                    .header("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                // 打印响应信息
                Log.d(TAG, "========== HTTP Response ==========")
                Log.d(TAG, "Status Code: ${response.code}")
                Log.d(TAG, "Status Message: ${response.message}")
                Log.d(TAG, "Headers: ${response.headers}")

                if (!response.isSuccessful) {
                    // 读取错误响应体
                    val responseBody = response.peekBody(Long.MAX_VALUE)
                    val errorBody = responseBody.string()
                    Log.e(TAG, "Error Response Body: $errorBody")
                }

                response
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

            // 打印请求体
            Log.d(TAG, "Request Body:")
            try {
                val requestJson = json.encodeToString(ImageChatCompletionRequest.serializer(), chatRequest)
                // 只打印部分内容，避免日志过长
                val truncatedJson = if (requestJson.length > 500) {
                    requestJson.take(200) + "...[truncated]..." + requestJson.takeLast(200)
                } else {
                    requestJson
                }
                Log.d(TAG, truncatedJson)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to serialize request: ${e.message}")
            }

                        Log.d(TAG, "Calling DashScope API...")
                        val response = dashScopeApi.chatCompletions(chatRequest)
            
                        // API 返回的 content 是字符串（markdown 格式的 JSON）
                        val content = response.choices.firstOrNull()?.message?.content
                            ?: throw ImageRecognitionException("API returned empty response")
            
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
        } catch (e: retrofit2.HttpException) {
            // 捕获 HTTP 异常并打印详细信息
            Log.e(TAG, "========== HTTP Error ==========")
            Log.e(TAG, "HTTP Code: ${e.code()}")
            Log.e(TAG, "HTTP Message: ${e.message()}")
            try {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Body: $errorBody")
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to read error body: ${ex.message}")
            }
            Log.e(TAG, "Stack trace:", e)
            throw ImageRecognitionException("Image recognition failed: HTTP ${e.code()} - ${e.message()}", e)
        } catch (e: Exception) {
            Log.e(TAG, "Food recognition exception: ${e.message}")
            Log.e(TAG, "Exception type: ${e.javaClass.name}")
            Log.e(TAG, "Stack trace:", e)
            throw ImageRecognitionException("Image recognition failed: ${e.message}", e)
        }
    }

    private fun buildImageContent(request: ImageRecognitionRequest): ImageContent {
        val mimeType = when (request.imageFormat.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }

        // 构造 Base64 data URL 格式: data:image/jpeg;base64,/9j/4AAQ...
        val dataUrl = "data:$mimeType;base64,${request.imageBase64}"

        return ImageContent(
            imageUrl = ImageUrlData(url = dataUrl)
        )
    }

    private fun buildPrompt(): String {
        val locale = Locale.getDefault()
        val isChinese = locale.language == Locale.CHINESE.language || locale.language == "zh"

        return if (isChinese) {
            buildChinesePrompt()
        } else {
            buildEnglishPrompt()
        }
    }

    private fun buildChinesePrompt(): String {
        return """
你是一位专业的食物营养师和食物识别专家。请从上传的图片中识别食物并提取关键信息。

请严格按照JSON格式返回数据，包含以下字段：
{
  "foodName": "食物名称（例如：胡萝卜、牛肉、南瓜）",
  "foodId": 0,
  "foodImageUrl": null,
  "storageMethod": "储存方式（REFRIGERATOR 冷藏/FREEZER 冷冻/ROOM_TEMP 常温）",
  "estimatedShelfLife": "预估保质期天数（整数）",
  "defaultUnit": "默认重量单位（例如：克、千克、毫克），必须使用重量单位",
  "quantity": 0,
  "nutritionInfo": {
    "calories": "每100克热量（千卡）",
    "protein": "每100克蛋白质（克）",
    "calcium": "每100克钙（毫克）",
    "iron": "每100克铁（毫克）"
  },
  "confidence": "识别置信度（0-1之间的小数）",
  "notes": "备注信息（可选）"
}

识别规则：
1. 优先使用具体的食物名称（例如用"胡萝卜"而不是"蔬菜"）
2. 根据食物特性确定储存方式：
   - REFRIGERATOR（冷藏）：蔬菜、水果、肉类、乳制品、蛋类
   - FREEZER（冷冻）：肉类、海鲜、冷冻食品
   - ROOM_TEMP（常温）：干货、调味品、罐头食品
3. 根据食物特性预估保质期：
   - 蔬菜：3-7天
   - 水果：5-14天
   - 肉类（冷藏）：3-5天
   - 肉类（冷冻）：90-180天
   - 蛋类：21天
   - 乳制品：7-14天
   - 干货/调味品：180-365天
4. 营养成分基于常见食物营养表（每100克）
5. 置信度表示识别可信度，清晰图片0.8-1.0，模糊图片0.5-0.8
6. 备注可以包含食物品种、产地等信息

如果图片模糊或无法识别，请返回：
{
  "error": "无法识别，请重新拍摄或手动输入"
}

请只返回JSON格式数据，不要包含其他文字描述。
""".trimIndent()
    }

    private fun buildEnglishPrompt(): String {
        return """
You are a professional food nutritionist and food recognition expert. Please identify the food from the uploaded image and extract key information.

Please return data in strict JSON format with the following fields:
{
  "foodName": "Food name (e.g., carrot, beef, pumpkin)",
  "foodId": 0,
  "foodImageUrl": null,
  "storageMethod": "Storage method (REFRIGERATOR/FREEZER/ROOM_TEMP)",
  "estimatedShelfLife": "Estimated shelf life in days (integer)",
  "defaultUnit": "Default weight unit (e.g., g, kg, mg), must use weight unit",
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
@SerialName("image_url")
private data class ImageContent(
    @SerialName("image_url")
    val imageUrl: ImageUrlData
) : ImageContentBase()

@Serializable
private data class ImageUrlData(
    val url: String
)

@Serializable
@SerialName("text")
private data class ImageTextContent(
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
    val message: ImageResponseMessage,
    @SerialName("finish_reason")
    val finishReason: String
)

@Serializable
private data class ImageResponseMessage(
    val role: String,
    val content: String  // API 返回的是字符串，不是数组
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