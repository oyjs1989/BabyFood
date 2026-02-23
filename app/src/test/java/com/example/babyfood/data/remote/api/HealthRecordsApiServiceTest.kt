package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.healthrecords.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * HealthRecordsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
class HealthRecordsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: HealthRecordsApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(HealthRecordsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getHealthRecords returns list of records`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "babyId": 1,
                    "date": "2026-01-15",
                    "weight": 9.2,
                    "height": 72.5,
                    "headCircumference": 45.0,
                    "hemoglobin": 115.0,
                    "iron": 12.0,
                    "calcium": 2.4,
                    "notes": "8月龄体检",
                    "createdAt": "2026-01-15T10:30:00"
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "date": "2026-02-15",
                    "weight": 9.8,
                    "height": 74.0,
                    "headCircumference": 46.0,
                    "hemoglobin": null,
                    "iron": null,
                    "calcium": null,
                    "notes": "9月龄体检",
                    "createdAt": "2026-02-15T14:20:00"
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
        val result = apiService.getHealthRecords(babyId = 1, startDate = null, endDate = null, limit = 50)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("2026-01-15", result[0].date)
        assertEquals(9.2, result[0].weight, 0.01)
        assertEquals(2L, result[1].id)
        assertEquals(9.8, result[1].weight, 0.01)
    }

    @Test
    fun `getHealthRecord returns single record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "date": "2026-01-15",
                "weight": 9.2,
                "height": 72.5,
                "headCircumference": 45.0,
                "hemoglobin": 115.0,
                "iron": 12.0,
                "calcium": 2.4,
                "notes": "8月龄体检",
                "createdAt": "2026-01-15T10:30:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getHealthRecord(recordId = 1)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1L, result.babyId)
        assertEquals("2026-01-15", result.date)
        assertEquals(9.2, result.weight, 0.01)
        assertEquals(72.5, result.height, 0.01)
        assertEquals(115.0, result.hemoglobin, 0.01)
    }

    @Test
    fun `createHealthRecord creates and returns new record`() = runBlocking {
        // Arrange
        val request = CreateHealthRecordRequest(
            babyId = 1,
            date = "2026-03-15",
            weight = 10.2,
            height = 75.0,
            headCircumference = 46.5,
            hemoglobin = 118.0,
            iron = 12.5,
            calcium = 2.5,
            notes = "10月龄体检"
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "date": "2026-03-15",
                "weight": 10.2,
                "height": 75.0,
                "headCircumference": 46.5,
                "hemoglobin": 118.0,
                "iron": 12.5,
                "calcium": 2.5,
                "notes": "10月龄体检",
                "createdAt": "2026-03-15T09:00:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.createHealthRecord(request)

        // Assert
        assertEquals(3L, result.id)
        assertEquals(10.2, result.weight, 0.01)
        assertEquals(75.0, result.height, 0.01)
        assertEquals(118.0, result.hemoglobin, 0.01)
    }

    @Test
    fun `updateHealthRecord updates and returns record`() = runBlocking {
        // Arrange
        val request = UpdateHealthRecordRequest(
            weight = 9.5,
            notes = "8月龄体检 - 更新"
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "date": "2026-01-15",
                "weight": 9.5,
                "height": 72.5,
                "headCircumference": 45.0,
                "hemoglobin": 115.0,
                "iron": 12.0,
                "calcium": 2.4,
                "notes": "8月龄体检 - 更新",
                "createdAt": "2026-01-15T10:30:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateHealthRecord(recordId = 1, request = request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(9.5, result.weight, 0.01)
        assertEquals("8月龄体检 - 更新", result.notes)
    }

    @Test
    fun `deleteHealthRecord returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        // Act - should not throw
        apiService.deleteHealthRecord(recordId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
    }

    @Test
    fun `analyzeHealth returns analysis result`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "weightAssessment": "正常",
                "heightAssessment": "正常",
                "headCircumferenceAssessment": "正常",
                "hemoglobinAssessment": "正常",
                "ironAssessment": "正常",
                "calciumAssessment": "正常",
                "overallAssessment": "健康",
                "riskLevel": "LOW",
                "suggestions": ["继续保持均衡饮食"],
                "percentile": {
                    "weight": 50.0,
                    "height": 55.0,
                    "headCircumference": 52.0
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.analyzeHealth(recordId = 1, babyId = 1)

        // Assert
        assertEquals("正常", result.weightAssessment)
        assertEquals("健康", result.overallAssessment)
        assertEquals("LOW", result.riskLevel)
        assertEquals(1, result.suggestions.size)
        assertEquals(50.0, result.percentile.weight, 0.01)
    }

    @Test
    fun `getLatestHealthRecord returns most recent record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 2,
                "babyId": 1,
                "date": "2026-02-15",
                "weight": 9.8,
                "height": 74.0,
                "headCircumference": 46.0,
                "hemoglobin": null,
                "iron": null,
                "calcium": null,
                "notes": "9月龄体检",
                "createdAt": "2026-02-15T14:20:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getLatestHealthRecord(babyId = 1)

        // Assert
        assertEquals(2L, result.id)
        assertEquals("2026-02-15", result.date)
        assertEquals(9.8, result.weight, 0.01)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getHealthRecord throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Health record not found")
        )

        // Act - should throw HttpException
        apiService.getHealthRecord(recordId = 999)
    }
}
