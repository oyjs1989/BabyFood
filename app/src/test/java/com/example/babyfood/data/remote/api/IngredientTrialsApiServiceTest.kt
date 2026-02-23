package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.ingredienttrials.*
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
 * IngredientTrialsApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
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
    fun `getTrials returns list of trials`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "babyId": 1,
                    "ingredientName": "南瓜",
                    "triedAt": "2026-01-10",
                    "reaction": "GOOD",
                    "notes": "宝宝很喜欢",
                    "imageUrl": null
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "ingredientName": "菠菜",
                    "triedAt": "2026-01-12",
                    "reaction": "NEUTRAL",
                    "notes": "吃了一点点",
                    "imageUrl": "https://example.com/spinach.jpg"
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
        val result = apiService.getTrials(babyId = 1, limit = 50)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("南瓜", result[0].ingredientName)
        assertEquals("GOOD", result[0].reaction)
        assertEquals(2L, result[1].id)
        assertEquals("菠菜", result[1].ingredientName)
        assertEquals("NEUTRAL", result[1].reaction)
    }

    @Test
    fun `getTrial returns single trial`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "ingredientName": "南瓜",
                "triedAt": "2026-01-10",
                "reaction": "GOOD",
                "notes": "宝宝很喜欢",
                "imageUrl": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getTrial(trialId = 1)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1L, result.babyId)
        assertEquals("南瓜", result.ingredientName)
        assertEquals("GOOD", result.reaction)
    }

    @Test
    fun `createTrial creates and returns new trial`() = runBlocking {
        // Arrange
        val request = CreateTrialRequest(
            babyId = 1,
            ingredientName = "胡萝卜",
            triedAt = "2026-01-15",
            reaction = "GOOD",
            notes = "第一次尝试，吃了很多"
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "ingredientName": "胡萝卜",
                "triedAt": "2026-01-15",
                "reaction": "GOOD",
                "notes": "第一次尝试，吃了很多",
                "imageUrl": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.createTrial(request)

        // Assert
        assertEquals(3L, result.id)
        assertEquals("胡萝卜", result.ingredientName)
        assertEquals("GOOD", result.reaction)
    }

    @Test
    fun `updateTrial updates and returns trial`() = runBlocking {
        // Arrange
        val request = UpdateTrialRequest(
            reaction = "EXCELLENT",
            notes = "宝宝特别喜欢！"
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "ingredientName": "南瓜",
                "triedAt": "2026-01-10",
                "reaction": "EXCELLENT",
                "notes": "宝宝特别喜欢！",
                "imageUrl": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateTrial(trialId = 1, request = request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals("EXCELLENT", result.reaction)
        assertEquals("宝宝特别喜欢！", result.notes)
    }

    @Test
    fun `deleteTrial returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        // Act - should not throw
        apiService.deleteTrial(trialId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
    }

    @Test
    fun `checkTrial checks if ingredient was tried`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "hasTried": true,
                "trialId": 1,
                "triedAt": "2026-01-10",
                "reaction": "GOOD"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.checkTrial(babyId = 1, ingredientName = "南瓜")

        // Assert
        assertTrue(result.hasTried)
        assertEquals(1L, result.trialId)
        assertEquals("GOOD", result.reaction)
    }

    @Test
    fun `checkTrial returns false for untried ingredient`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "hasTried": false,
                "trialId": null,
                "triedAt": null,
                "reaction": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.checkTrial(babyId = 1, ingredientName = "三文鱼")

        // Assert
        assertFalse(result.hasTried)
        assertNull(result.trialId)
        assertNull(result.reaction)
    }

    @Test
    fun `getFlavorDiversity returns diversity info`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "totalIngredientsTried": 25,
                "flavorGroups": {
                    "vegetables": 12,
                    "fruits": 8,
                    "proteins": 3,
                    "grains": 2
                },
                "diversityScore": 75,
                "recommendations": ["可以尝试更多谷物类食物"]
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
        assertEquals(1L, result.babyId)
        assertEquals(25, result.totalIngredientsTried)
        assertEquals(75, result.diversityScore)
        assertEquals(12, result.flavorGroups["vegetables"])
        assertEquals(1, result.recommendations.size)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getTrial throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Trial not found")
        )

        // Act - should throw HttpException
        apiService.getTrial(trialId = 999)
    }
}
