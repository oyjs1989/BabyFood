package com.example.babyfood.data.ai

import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
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

/**
 * 远程健康分析策略
 * 调用阿里云 DashScope API（通义千问）进行智能健康分析
 */
@Singleton
class RemoteHealthAnalysisStrategy @Inject constructor() : HealthAnalysisService {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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

    private val dashScopeApi: DashScopeApi by lazy {
        retrofit.create(DashScopeApi::class.java)
    }

    /**
     * 分析体检记录
     * 调用通义千问 API 进行智能分析
     */
    override suspend fun analyze(record: HealthRecord, baby: Baby): String? {
        return try {
            // 构建提示词
            val prompt = buildPrompt(record, baby)

            // 构建请求
            val request = ChatCompletionRequest(
                model = "qwen-plus",
                messages = listOf(
                    Message(role = "user", content = prompt)
                )
            )

            // 调用 API
            val response = dashScopeApi.chatCompletions(request)

            // 返回分析结论
            response.choices.firstOrNull()?.message?.content?.trim()

        } catch (e: Exception) {
            // 网络错误或 API 调用失败
            // 抛出异常，由调用方决定是否降级到本地策略
            throw HealthAnalysisException("远程 AI 分析失败: ${e.message}", e)
        }
    }

    /**
     * 构建提示词
     */
    private fun buildPrompt(record: HealthRecord, baby: Baby): String {
        val ageInMonths = baby.ageInMonths
        val promptBuilder = StringBuilder()

        promptBuilder.append("你是一位专业的儿科医生，请根据以下体检数据为一位${ageInMonths}个月大的宝宝进行健康分析：\n\n")
        promptBuilder.append("宝宝信息：\n")
        promptBuilder.append("- 月龄：${ageInMonths}个月\n")
        promptBuilder.append("- 出生日期：${baby.birthDate}\n")

        promptBuilder.append("\n体检数据：\n")
        promptBuilder.append("- 体检日期：${record.recordDate}\n")

        record.weight?.let {
            promptBuilder.append("- 体重：${it} kg\n")
        }

        record.height?.let {
            promptBuilder.append("- 身高：${it} cm\n")
        }

        record.headCircumference?.let {
            promptBuilder.append("- 头围：${it} cm\n")
        }

        record.ironLevel?.let {
            promptBuilder.append("- 铁含量：${it} mg/L\n")
        }

        record.calciumLevel?.let {
            promptBuilder.append("- 钙含量：${it} mg/L\n")
        }

        record.hemoglobin?.let {
            promptBuilder.append("- 血红蛋白：${it} g/L\n")
        }

        record.notes?.let {
            promptBuilder.append("- 备注：${it}\n")
        }

        promptBuilder.append("\n请分析以上数据，给出健康建议。如果发现异常，请：\n")
        promptBuilder.append("1. 指出可能存在的问题\n")
        promptBuilder.append("2. 给出具体的改善建议\n")
        promptBuilder.append("3. 说明是否需要就医\n\n")
        promptBuilder.append("请用简洁明了的语言回答，不要超过200字。")

        return promptBuilder.toString()
    }

    /**
     * 获取 API Key
     * 从环境变量或本地配置中读取
     */
    private fun getApiKey(): String {
        // 优先从环境变量读取
        val envApiKey = System.getenv("DASHSCOPE_API_KEY")
        if (!envApiKey.isNullOrBlank()) {
            return envApiKey
        }

        // 从本地配置读取（用于开发测试）
        // TODO: 在生产环境中应该使用环境变量或安全的密钥管理方案
        return "sk-aa30af1a40a643cbb8f43881c1ebbb49"
    }
}

/**
 * 阿里云 DashScope API 接口
 */
private interface DashScopeApi {
    @retrofit2.http.POST("chat/completions")
    suspend fun chatCompletions(
        @retrofit2.http.Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

/**
 * 聊天完成请求
 */
@Serializable
private data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>
)

/**
 * 消息
 */
@Serializable
private data class Message(
    val role: String,
    val content: String
)

/**
 * 聊天完成响应
 */
@Serializable
private data class ChatCompletionResponse(
    val id: String,
    @SerialName("object")
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
)

/**
 * 选择
 */
@Serializable
private data class Choice(
    val index: Int,
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String
)

/**
 * 使用情况
 */
@Serializable
private data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

/**
 * 健康分析异常
 */
class HealthAnalysisException(message: String, cause: Throwable? = null) : Exception(message, cause)