package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.growthrecords.*
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
 * GrowthRecordsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
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
                    "date": "2026-01-15",
                    "weight": 9.2,
                    "height": 72.5,
                    "headCircumference": 45.0,
                    "createdAt": "2026-01-15T10:30:00"
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "date": "2026-02-15",
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
        val result = apiService.getGrowthRecords(babyId = 1, startDate = null, endDate = null, limit = 50)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("2026-01-15", result[0].date)
        assertEquals(9.2, result[0].weight, 0.01)
        assertEquals(2L, result[1].id)
        assertEquals(9.8, result[1].weight, 0.01)
    }

    @Test
    fun `getGrowthRecord returns single record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "date": "2026-01-15",
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
        val result = apiService.getGrowthRecord(recordId = 1)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1L, result.babyId)
        assertEquals("2026-01-15", result.date)
        assertEquals(9.2, result.weight, 0.01)
        assertEquals(72.5, result.height, 0.01)
        assertEquals(45.0, result.headCircumference, 0.01)
    }

    @Test
    fun `createGrowthRecord creates and returns new record`() = runBlocking {
        // Arrange
        val request = CreateGrowthRecordRequest(
            babyId = 1,
            date = "2026-03-15",
            weight = 10.2,
            height = 75.0,
            headCircumference = 46.5
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "date": "2026-03-15",
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
        val result = apiService.createGrowthRecord(request)

        // Assert
        assertEquals(3L, result.id)
        assertEquals(10.2, result.weight, 0.01)
        assertEquals(75.0, result.height, 0.01)
        assertEquals(46.5, result.headCircumference, 0.01)
    }

    @Test
    fun `updateGrowthRecord updates and returns record`() = runBlocking {
        // Arrange
        val request = UpdateGrowthRecordRequest(
            weight = 9.5,
            height = 73.0
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "date": "2026-01-15",
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
        val result = apiService.updateGrowthRecord(recordId = 1, request = request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(9.5, result.weight, 0.01)
        assertEquals(73.0, result.height, 0.01)
    }

    @Test
    fun `deleteGrowthRecord returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        // Act - should not throw
        apiService.deleteGrowthRecord(recordId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
    }

    @Test
    fun `getGrowthCurve returns curve data`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "metric": "weight",
                "records": [
                    {"date": "2026-01-15", "value": 9.2, "ageInMonths": 8},
                    {"date": "2026-02-15", "value": 9.8, "ageInMonths": 9}
                ],
                "percentile3": [{"ageInMonths": 8, "value": 7.5}, {"ageInMonths": 9, "value": 8.0}],
                "percentile50": [{"ageInMonths": 8, "value": 9.0}, {"ageInMonths": 9, "value": 9.5}],
                "percentile97": [{"ageInMonths": 8, "value": 11.0}, {"ageInMonths": 9, "value": 11.5}]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getGrowthCurve(babyId = 1, metric = "weight")

        // Assert
        assertEquals("weight", result.metric)
        assertEquals(2, result.records.size)
        assertEquals(9.2, result.records[0].value, 0.01)
        assertEquals(2, result.percentile50.size)
        assertEquals(9.0, result.percentile50[0].value, 0.01)
    }

    @Test
    fun `getGrowthStats returns statistics`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "currentAgeInMonths": 9,
                "currentWeight": 9.8,
                "currentHeight": 74.0,
                "currentHeadCircumference": 46.0,
                "weightPercentile": 60.0,
                "heightPercentile": 55.0,
                "headCircumferencePercentile": 52.0,
                "weightGainLastMonth": 0.6,
                "heightGainLastMonth": 1.5,
                "weightGainVelocity": "正常",
                "heightGainVelocity": "正常"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getGrowthStats(babyId = 1)

        // Assert
        assertEquals(1L, result.babyId)
        assertEquals(9, result.currentAgeInMonths)
        assertEquals(9.8, result.currentWeight, 0.01)
        assertEquals(60.0, result.weightPercentile, 0.01)
        assertEquals("正常", result.weightGainVelocity)
    }

    @Test
    fun `syncFromHealthRecord syncs data from health record`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 4,
                "babyId": 1,
                "date": "2026-03-01",
                "weight": 10.0,
                "height": 74.5,
                "headCircumference": 46.2,
                "createdAt": "2026-03-01T10:00:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.syncFromHealthRecord(healthRecordId = 3, babyId = 1)

        // Assert
        assertEquals(4L, result.id)
        assertEquals(10.0, result.weight, 0.01)
        assertEquals(74.5, result.height, 0.01)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getGrowthRecord throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Growth record not found")
        )

        // Act - should throw HttpException
        apiService.getGrowthRecord(recordId = 999)
    }
}
