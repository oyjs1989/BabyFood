package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.ImageAnalysisResult
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * ImageAnalysisApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
class ImageAnalysisApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ImageAnalysisApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(ImageAnalysisApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `analyzeImage returns recipe recognition result`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "success": true,
                "analysisType": "RECIPE_RECOGNITION",
                "result": {
                    "foodName": "西兰花土豆泥",
                    "ingredients": ["西兰花", "土豆"],
                    "minAgeMonths": 6,
                    "confidence": 0.95
                },
                "skillUsed": "dashscope-recipe",
                "processingTime": 1.5
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Prepare multi-part data
        val imagePart = MultipartBody.Part.createFormData(
            "image", "test.jpg", 
            "dummy content".toRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val typePart = "RECIPE_RECOGNITION".toRequestBody("text/plain".toMediaTypeOrNull())
        val babyIdPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        // Act
        val result = apiService.analyzeImage(imagePart, typePart, babyIdPart)

        // Assert
        assertTrue(result.success)
        assertEquals("RECIPE_RECOGNITION", result.analysisType)
        assertEquals("西兰花土豆泥", result.result?.foodName)
        assertEquals(0.95, result.result?.confidence!!, 0.01)
    }

    @Test
    fun `analyzeImage returns health record OCR result`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "success": true,
                "analysisType": "HEALTH_RECORD_OCR",
                "result": {
                    "recordDate": "2026-03-10",
                    "weight": 9.5,
                    "height": 72.0,
                    "hemoglobin": 120.0
                },
                "skillUsed": "dashscope-ocr",
                "processingTime": 2.0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Prepare multi-part data
        val imagePart = MultipartBody.Part.createFormData(
            "image", "record.jpg", 
            "dummy content".toRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val typePart = "HEALTH_RECORD_OCR".toRequestBody("text/plain".toMediaTypeOrNull())

        // Act
        val result = apiService.analyzeImage(imagePart, typePart, null)

        // Assert
        assertTrue(result.success)
        assertEquals("2026-03-10", result.result?.recordDate)
        assertEquals(9.5, result.result?.weight!!, 0.01)
    }
}
