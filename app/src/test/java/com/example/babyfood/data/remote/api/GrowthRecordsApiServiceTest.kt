package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.growth_records.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
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
 * GrowthRecordsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
class GrowthRecordsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GrowthRecordsApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(GrowthRecordsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getGrowthRecords returns list of records`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "babyId": 1,
                    "recordDate": "2026-01-15",
                    "weight": 9.2,
                    "height": 72.5,
                    "headCircumference": 45.0,
                    "createdAt": "2026-01-15T10:30:00"
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "recordDate": "2026-02-15",
                    "weight": 9.8,
                    "height": 74.0,
                    "headCircumference": 46.0,
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
        val result = apiService.getGrowthRecords(babyId = 1)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("2026-01-15", result[0].recordDate)
        assertEquals(9.2, result[0].weight, 0.01)
        assertEquals(2, result[1].id)
        assertEquals(9.8, result[1].weight, 0.01)
    }

    @Test
    fun `getGrowthRecord returns single record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "recordDate": "2026-01-15",
                "weight": 9.2,
                "height": 72.5,
                "headCircumference": 45.0,
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
        val result = apiService.getGrowthRecord(babyId = 1, recordId = 1)

        // Assert
        assertEquals(1, result.id)
        assertEquals(1, result.babyId)
        assertEquals("2026-01-15", result.recordDate)
        assertEquals(9.2, result.weight, 0.01)
        assertEquals(72.5, result.height, 0.01)
        assertEquals(45.0, result.headCircumference!!, 0.01)
    }

    @Test
    fun `createGrowthRecord creates and returns new record`() = runBlocking {
        // Arrange
        val request = GrowthRecordCreate(
            recordDate = "2026-03-15",
            weight = 10.2,
            height = 75.0,
            headCircumference = 46.5
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "recordDate": "2026-03-15",
                "weight": 10.2,
                "height": 75.0,
                "headCircumference": 46.5,
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
        val result = apiService.createGrowthRecord(babyId = 1, record = request)

        // Assert
        assertEquals(3, result.id)
        assertEquals(10.2, result.weight, 0.01)
        assertEquals(75.0, result.height, 0.01)
        assertEquals(46.5, result.headCircumference!!, 0.01)
    }

    @Test
    fun `updateGrowthRecord updates and returns record`() = runBlocking {
        // Arrange
        val request = GrowthRecordCreate(
            recordDate = "2026-01-15",
            weight = 9.5,
            height = 73.0,
            headCircumference = 45.0
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "recordDate": "2026-01-15",
                "weight": 9.5,
                "height": 73.0,
                "headCircumference": 45.0,
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
        val result = apiService.updateGrowthRecord(babyId = 1, recordId = 1, record = request)

        // Assert
        assertEquals(1, result.id)
        assertEquals(9.5, result.weight, 0.01)
        assertEquals(73.0, result.height, 0.01)
    }

    @Test
    fun `deleteGrowthRecord returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader("Content-Type", "application/json")
        )

        // Act - should not throw
        apiService.deleteGrowthRecord(babyId = 1, recordId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/babies/1/growth-records/1", request.path)
    }

    @Test
    fun `getGrowthAssessment returns assessment data`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "assessmentDate": "2026-02-15",
                "weightPercentile": 60.0,
                "heightPercentile": 55.0,
                "headCircumferencePercentile": 52.0,
                "weightStatus": "正常",
                "heightStatus": "正常",
                "recommendations": ["继续保持均衡饮食"]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getGrowthAssessment(babyId = 1)

        // Assert
        assertEquals(1, result.babyId)
        assertEquals(60.0, result.weightPercentile, 0.01)
        assertEquals("正常", result.weightStatus)
        assertEquals(1, result.recommendations.size)
    }

    @Test
    fun `getGrowthRecord throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Growth record not found")
        )

        // Act & Assert
        try {
            apiService.getGrowthRecord(babyId = 1, recordId = 999)
            fail("Should throw HttpException")
        } catch (e: retrofit2.HttpException) {
            assertEquals(404, e.code())
        }
    }
}
