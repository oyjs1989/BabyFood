package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.imageanalysis.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * ImageAnalysisApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
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
    fun `analyzeImage returns successful result for recipe recognition`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "success": true,
                "analysisType": "RECIPE_RECOGNITION",
                "title": "南瓜米糊",
                "description": "营养丰富的南瓜米糊",
                "ingredients": [
                    {"name": "南瓜", "amount": "50g"},
                    {"name": "大米", "amount": "30g"}
                ],
                "steps": ["将南瓜蒸熟", "与大米一起打成泥"],
                "nutritionInfo": {"calories": 120, "protein": 2.5},
                "confidence": 0.95,
                "errorMessage": null,
                "alternativeResults": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Create a fake multipart request
        val buffer = Buffer()
        buffer.writeUtf8("fake_image_data")

        // Act
        val result = apiService.analyzeImage(
            image = okhttp3.MultipartBody.Part.createFormData("image", "test.jpg", okhttp3.RequestBody.create("image/jpeg".toMediaType(), buffer.readByteArray())),
            type = okhttp3.RequestBody.create("text/plain".toMediaType(), "RECIPE_RECOGNITION"),
            babyId = okhttp3.RequestBody.create("text/plain".toMediaType(), "1")
        )

        // Assert
        assertTrue(result.success)
        assertEquals("RECIPE_RECOGNITION", result.analysisType)
        assertEquals("南瓜米糊", result.title)
        assertEquals(0.95, result.confidence, 0.01)
        assertEquals(2, result.ingredients.size)
        assertEquals("南瓜", result.ingredients[0].name)
        assertNull(result.errorMessage)
    }

    @Test
    fun `analyzeImage returns successful result for health record OCR`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "success": true,
                "analysisType": "HEALTH_RECORD_OCR",
                "title": "体检报告",
                "description": "8月龄体检",
                "extractedData": {
                    "height": 72.5,
                    "weight": 9.2,
                    "headCircumference": 45.0,
                    "date": "2026-01-15"
                },
                "confidence": 0.92,
                "errorMessage": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.analyzeImage(
            image = okhttp3.MultipartBody.Part.createFormData("image", "health.jpg", okhttp3.RequestBody.create("image/jpeg".toMediaType(), byteArrayOf())),
            type = okhttp3.RequestBody.create("text/plain".toMediaType(), "HEALTH_RECORD_OCR"),
            babyId = okhttp3.RequestBody.create("text/plain".toMediaType(), "1")
        )

        // Assert
        assertTrue(result.success)
        assertEquals("HEALTH_RECORD_OCR", result.analysisType)
        assertNotNull(result.extractedData)
        assertEquals(72.5, result.extractedData?.height, 0.01)
        assertEquals(9.2, result.extractedData?.weight, 0.01)
    }

    @Test
    fun `analyzeImage returns error result on failure`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "success": false,
                "analysisType": "RECIPE_RECOGNITION",
                "title": null,
                "description": null,
                "ingredients": [],
                "steps": [],
                "nutritionInfo": null,
                "confidence": 0.0,
                "errorMessage": "Image too blurry, please try again",
                "alternativeResults": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.analyzeImage(
            image = okhttp3.MultipartBody.Part.createFormData("image", "blurry.jpg", okhttp3.RequestBody.create("image/jpeg".toMediaType(), byteArrayOf())),
            type = okhttp3.RequestBody.create("text/plain".toMediaType(), "RECIPE_RECOGNITION"),
            babyId = null
        )

        // Assert
        assertFalse(result.success)
        assertEquals("Image too blurry, please try again", result.errorMessage)
        assertEquals(0.0, result.confidence, 0.01)
    }

    @Test
    fun `getAnalysisHistory returns list of analyses`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "analysisType": "RECIPE_RECOGNITION",
                    "title": "南瓜米糊",
                    "description": "营养丰富的南瓜米糊",
                    "createdAt": "2026-01-15T10:30:00",
                    "thumbnailUrl": "https://example.com/thumb1.jpg"
                },
                {
                    "id": 2,
                    "analysisType": "HEALTH_RECORD_OCR",
                    "title": "体检报告",
                    "description": "8月龄体检",
                    "createdAt": "2026-01-10T14:20:00",
                    "thumbnailUrl": null
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getAnalysisHistory(babyId = 1, limit = 10)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("RECIPE_RECOGNITION", result[0].analysisType)
        assertEquals("南瓜米糊", result[0].title)
        assertEquals(2L, result[1].id)
        assertEquals("HEALTH_RECORD_OCR", result[1].analysisType)
    }

    @Test
    fun `getAnalysisDetail returns single analysis`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "analysisType": "RECIPE_RECOGNITION",
                "title": "南瓜米糊",
                "description": "营养丰富的南瓜米糊",
                "ingredients": [
                    {"name": "南瓜", "amount": "50g"}
                ],
                "steps": ["将南瓜蒸熟"],
                "nutritionInfo": {"calories": 120},
                "confidence": 0.95,
                "createdAt": "2026-01-15T10:30:00",
                "imageUrl": "https://example.com/image.jpg"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getAnalysisDetail(analysisId = 1)

        // Assert
        assertEquals(1L, result.id)
        assertEquals("RECIPE_RECOGNITION", result.analysisType)
        assertEquals("南瓜米糊", result.title)
        assertEquals(0.95, result.confidence, 0.01)
    }

    @Test
    fun `deleteAnalysis returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        // Act - should not throw
        apiService.deleteAnalysis(analysisId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertTrue(request.path?.contains("/api/v1/images/analysis/1") == true)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `analyzeImage throws exception on server error`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // Act - should throw HttpException
        apiService.analyzeImage(
            image = okhttp3.MultipartBody.Part.createFormData("image", "test.jpg", okhttp3.RequestBody.create("image/jpeg".toMediaType(), byteArrayOf())),
            type = okhttp3.RequestBody.create("text/plain".toMediaType(), "RECIPE_RECOGNITION"),
            babyId = null
        )
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getAnalysisDetail throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Analysis not found")
        )

        // Act - should throw HttpException
        apiService.getAnalysisDetail(analysisId = 999)
    }
}
