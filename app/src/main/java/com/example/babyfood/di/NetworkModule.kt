package com.example.babyfood.di

import android.content.Context
import com.example.babyfood.BuildConfig
import com.example.babyfood.data.ai.BackendImageRecognitionStrategy
import com.example.babyfood.data.ai.ImageRecognitionService
import com.example.babyfood.data.local.TokenStorage
import com.example.babyfood.data.remote.api.AiProxyApiService
import com.example.babyfood.data.remote.api.AuthApiService
import com.example.babyfood.data.remote.api.BabyApiService
import com.example.babyfood.data.remote.api.GrowthRecordsApiService
import com.example.babyfood.data.remote.api.HealthRecordsApiService
import com.example.babyfood.data.remote.api.ImageAnalysisApiService
import com.example.babyfood.data.remote.api.IngredientTrialsApiService
import com.example.babyfood.data.remote.api.InventoryApiService
import com.example.babyfood.data.remote.api.NutritionApiService
import com.example.babyfood.data.remote.api.PlanApiService
import com.example.babyfood.data.remote.api.PointsApiService
import com.example.babyfood.data.remote.api.RecipeApiService
import com.example.babyfood.data.remote.api.SyncApiService
import com.example.babyfood.data.remote.interceptor.JwtAuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 网络依赖注入模块
 * 提供 Retrofit、OkHttp 和相关网络组件的依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * API 基础 URL
     * 从 BuildConfig 读取配置文件中的后端服务器 IP 和端口
     */
    private val BASE_URL: String = run {
        val protocol = if (BuildConfig.BACKEND_SERVER_PORT == "443") "https://" else "http://"
        protocol + BuildConfig.BACKEND_SERVER_IP + ":" + BuildConfig.BACKEND_SERVER_PORT + "/"
    }

    /**
     * JSON 序列化配置
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    /**
     * HTTP 日志拦截器
     * 仅在 Debug 模式下启用详细日志
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * JWT 认证拦截器
     * 自动为所有请求添加 JWT Token 到 Authorization 头
     */
    @Provides
    @Singleton
    fun provideJwtAuthInterceptor(tokenStorage: TokenStorage): JwtAuthInterceptor {
        return JwtAuthInterceptor(tokenStorage = tokenStorage)
    }

    /**
     * OkHttp 客户端
     * 连接超时 30 秒，读取超时 30 秒，写入超时 30 秒
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        jwtAuthInterceptor: JwtAuthInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(jwtAuthInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // 如果是 HTTPS 且处于 Debug 模式，忽略 SSL 证书校验（解决 IP 访问时的证书信任问题）
        if (BASE_URL.startsWith("https://") && BuildConfig.DEBUG) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                
                builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return builder.build()
    }

    /**
     * Retrofit 实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * 用于测试的 Retrofit 实例（Mock）
     */
    @Provides
    @Singleton
    @TestRetrofit
    fun provideTestRetrofit(json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("http://localhost:8080/") // Mock Server
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // ==================== API 服务 ====================

    /**
     * Recipe API 服务
     */
    @Provides
    @Singleton
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }

    /**
     * Plan API 服务
     */
    @Provides
    @Singleton
    fun providePlanApiService(retrofit: Retrofit): PlanApiService {
        return retrofit.create(PlanApiService::class.java)
    }

    /**
     * Baby API 服务
     */
    @Provides
    @Singleton
    fun provideBabyApiService(retrofit: Retrofit): BabyApiService {
        return retrofit.create(BabyApiService::class.java)
    }

    /**
     * Sync API 服务
     */
    @Provides
    @Singleton
    fun provideSyncApiService(retrofit: Retrofit): SyncApiService {
        return retrofit.create(SyncApiService::class.java)
    }

    /**
     * Auth API 服务
     */
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    /**
     * Points API 服务
     */
    @Provides
    @Singleton
    fun providePointsApiService(retrofit: Retrofit): PointsApiService {
        return retrofit.create(PointsApiService::class.java)
    }

    /**
     * Image Analysis API 服务
     */
    @Provides
    @Singleton
    fun provideImageAnalysisApiService(retrofit: Retrofit): ImageAnalysisApiService {
        return retrofit.create(ImageAnalysisApiService::class.java)
    }

    /**
     * Nutrition API 服务
     */
    @Provides
    @Singleton
    fun provideNutritionApiService(retrofit: Retrofit): NutritionApiService {
        return retrofit.create(NutritionApiService::class.java)
    }

    /**
     * Health Records API 服务
     */
    @Provides
    @Singleton
    fun provideHealthRecordsApiService(retrofit: Retrofit): HealthRecordsApiService {
        return retrofit.create(HealthRecordsApiService::class.java)
    }

    /**
     * Growth Records API 服务
     */
    @Provides
    @Singleton
    fun provideGrowthRecordsApiService(retrofit: Retrofit): GrowthRecordsApiService {
        return retrofit.create(GrowthRecordsApiService::class.java)
    }

    /**
     * Inventory API 服务
     */
    @Provides
    @Singleton
    fun provideInventoryApiService(retrofit: Retrofit): InventoryApiService {
        return retrofit.create(InventoryApiService::class.java)
    }

    /**
     * Ingredient Trials API 服务
     */
    @Provides
    @Singleton
    fun provideIngredientTrialsApiService(retrofit: Retrofit): IngredientTrialsApiService {
        return retrofit.create(IngredientTrialsApiService::class.java)
    }

    /**
     * AI Proxy API 服务
     * 用于代理调用 AI 服务（健康分析、推荐等），避免在前端暴露 API Key
     */
    @Provides
    @Singleton
    fun provideAiProxyApiService(retrofit: Retrofit): AiProxyApiService {
        return retrofit.create(AiProxyApiService::class.java)
    }

    // ==================== AI 服务 ====================

    /**
     * 图像识别服务
     * 使用后端代理 API 调用 AI 图像识别，避免在前端暴露 API Key
     */
    @Provides
    @Singleton
    fun provideImageRecognitionService(
        backendStrategy: BackendImageRecognitionStrategy
    ): ImageRecognitionService = backendStrategy
}

/**
 * 测试用 Retrofit 限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestRetrofit