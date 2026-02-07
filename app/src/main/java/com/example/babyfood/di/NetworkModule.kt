package com.example.babyfood.di

import android.content.Context
import com.example.babyfood.BuildConfig
import com.example.babyfood.data.ai.DashScopeImageRecognitionStrategy
import com.example.babyfood.data.ai.ImageRecognitionService
import com.example.babyfood.data.local.TokenStorage
import com.example.babyfood.data.remote.api.AuthApiService
import com.example.babyfood.data.remote.api.BabyApiService
import com.example.babyfood.data.remote.api.PlanApiService
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
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

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
    private const val BASE_URL = "http://" + BuildConfig.BACKEND_SERVER_IP + ":" + BuildConfig.BACKEND_SERVER_PORT + "/"

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
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(jwtAuthInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
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

    // ==================== AI 服务 ====================

    /**
     * 图像识别服务
     */
    @Provides
    @Singleton
    fun provideImageRecognitionService(
        dashScopeStrategy: DashScopeImageRecognitionStrategy
    ): ImageRecognitionService = dashScopeStrategy
}

/**
 * 测试用 Retrofit 限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestRetrofit