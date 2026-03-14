package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.ingredient_trials.*
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
 * IngredientTrialsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
class IngredientTrialsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: IngredientTrialsApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(IngredientTrialsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTrialsByBaby returns list`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "babyId": 1,
                    "ingredientName": "胡萝卜",
                    "trialDate": "2026-03-10",
                    "reaction": "NONE",
                    "notes": "宝宝很喜欢",
                    "createdAt": "2026-03-10T10:00:00"
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
        val result = apiService.getTrialsByBaby(babyId = 1)

        // Assert
        assertEquals(1, result.size)
        assertEquals("胡萝卜", result[0].ingredientName)
    }

    @Test
    fun `createTrial creates and returns new trial`() = runBlocking {
        // Arrange
        val request = IngredientTrialCreate(
            babyId = 1,
            ingredientName = "苹果",
            trialDate = "2026-03-15",
            reaction = "NONE",
            notes = "切成小块喂食"
        )

        val mockResponse = """
            {
                "id": 2,
                "babyId": 1,
                "ingredientName": "苹果",
                "trialDate": "2026-03-15",
                "reaction": "NONE",
                "notes": "切成小块喂食",
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
        val result = apiService.createTrial(trial = request)

        // Assert
        assertEquals(2, result.id)
        assertEquals("苹果", result.ingredientName)
    }

    @Test
    fun `updateTrial updates and returns trial`() = runBlocking {
        // Arrange
        val request = IngredientTrialUpdate(
            reaction = "MILD",
            notes = "发现轻微红疹"
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "ingredientName": "胡萝卜",
                "trialDate": "2026-03-10",
                "reaction": "MILD",
                "notes": "发现轻微红疹",
                "createdAt": "2026-03-10T10:00:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateTrial(trialId = 1, update = request)

        // Assert
        assertEquals("MILD", result.reaction)
    }

    @Test
    fun `deleteTrial returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader("Content-Type", "application/json")
        )

        // Act - should not throw
        apiService.deleteTrial(trialId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/ingredient-trials/1", request.path)
    }

    @Test
    fun `getFlavorDiversity returns diversity info`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "totalTried": 10,
                "diversityScore": 75.5,
                "newIngredientsRecommendations": ["可以尝试增加蛋白质类食材"],
                "triedCategories": ["VEGETABLE", "FRUIT"]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getFlavorDiversity(babyId = 1)

        // Assert
        assertEquals(10, result.totalTried)
        assertEquals(75.5, result.diversityScore, 0.01)
    }
}
