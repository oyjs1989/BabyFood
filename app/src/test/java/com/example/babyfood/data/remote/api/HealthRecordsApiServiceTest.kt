package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.health_records.*
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
 * HealthRecordsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
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
                    "recordDate": "2026-01-15",
                    "hemoglobin": 120.5,
                    "iron": 12.5,
                    "notes": "常规体检",
                    "createdAt": "2026-01-15T10:30:00"
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "recordDate": "2026-02-15",
                    "hemoglobin": 118.2,
                    "calcium": 2.2,
                    "notes": "有些贫血倾向",
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
        val result = apiService.getHealthRecords(babyId = 1)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(120.5, result[0].hemoglobin!!, 0.01)
        assertEquals(118.2, result[1].hemoglobin!!, 0.01)
    }

    @Test
    fun `getHealthRecord returns single record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "recordDate": "2026-01-15",
                "hemoglobin": 120.5,
                "iron": 12.5,
                "notes": "常规体检",
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
        val result = apiService.getHealthRecord(babyId = 1, recordId = 1)

        // Assert
        assertEquals(1, result.id)
        assertEquals(1, result.babyId)
        assertEquals(120.5, result.hemoglobin!!, 0.01)
    }

    @Test
    fun `createHealthRecord creates and returns new record`() = runBlocking {
        // Arrange
        val request = HealthRecordCreate(
            recordDate = "2026-03-15",
            hemoglobin = 122.0,
            notes = "一切良好"
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "recordDate": "2026-03-15",
                "hemoglobin": 122.0,
                "notes": "一切良好",
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
        val result = apiService.createHealthRecord(babyId = 1, record = request)

        // Assert
        assertEquals(3, result.id)
        assertEquals(122.0, result.hemoglobin!!, 0.01)
    }

    @Test
    fun `updateHealthRecord updates and returns record`() = runBlocking {
        // Arrange
        val request = HealthRecordCreate(
            recordDate = "2026-01-15",
            hemoglobin = 121.5,
            notes = "已恢复"
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "recordDate": "2026-01-15",
                "hemoglobin": 121.5,
                "notes": "已恢复",
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
        val result = apiService.updateHealthRecord(babyId = 1, recordId = 1, record = request)

        // Assert
        assertEquals(1, result.id)
        assertEquals(121.5, result.hemoglobin!!, 0.01)
    }

    @Test
    fun `deleteHealthRecord returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader("Content-Type", "application/json")
        )

        // Act - should not throw
        apiService.deleteHealthRecord(babyId = 1, recordId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/babies/1/health-records/1", request.path)
    }

    @Test
    fun `getHealthRecord throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Health record not found")
        )

        // Act & Assert
        try {
            apiService.getHealthRecord(babyId = 1, recordId = 999)
            fail("Should throw HttpException")
        } catch (e: retrofit2.HttpException) {
            assertEquals(404, e.code())
        }
    }
}
