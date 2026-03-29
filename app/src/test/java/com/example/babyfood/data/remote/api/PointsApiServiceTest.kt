package com.example.babyfood.data.remote.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit

@RunWith(JUnit4::class)
class PointsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: PointsApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(PointsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPointsInfo maps snake_case payload`() = runBlocking {
        val mockResponse = """
            {
                "success": true,
                "error_message": null,
                "current_balance": 20,
                "last_check_in_date": 1774512511122,
                "today_checked_in": false
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = apiService.getPointsInfo()

        assertEquals(20, result.currentBalance)
        assertFalse(result.todayCheckedIn)
        assertEquals(1774512511122, result.lastCheckInDate)
    }
}
