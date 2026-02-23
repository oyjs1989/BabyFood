package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.nutrition.*
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
 * NutritionApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
class NutritionApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: NutritionApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(NutritionApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getSafetyRisks returns list of SafetyRiskDto`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "ingredientName": "蜂蜜",
                    "riskLevel": "FORBIDDEN",
                    "reason": "可能含有肉毒杆菌",
                    "minAge": 12,
                    "handling": "12个月以下禁用"
                },
                {
                    "id": 2,
                    "ingredientName": "花生",
                    "riskLevel": "CAUTIOUS_INTRODUCTION",
                    "reason": "常见过敏原",
                    "minAge": 6,
                    "handling": "逐步引入，观察反应"
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
        val result = apiService.getSafetyRisks(ageInMonths = 8)

        // Assert
        assertEquals(2, result.size)
        assertEquals("蜂蜜", result[0].ingredientName)
        assertEquals("FORBIDDEN", result[0].riskLevel)
        assertEquals("花生", result[1].ingredientName)
        assertEquals("CAUTIOUS_INTRODUCTION", result[1].riskLevel)
    }

    @Test
    fun `getSafetyRisksByIngredients returns filtered risks`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "ingredientName": "蜂蜜",
                    "riskLevel": "FORBIDDEN",
                    "reason": "可能含有肉毒杆菌",
                    "minAge": 12,
                    "handling": "12个月以下禁用"
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
        val result = apiService.getSafetyRisksByIngredients(
            ingredients = listOf("蜂蜜", "牛奶"),
            ageInMonths = 8
        )

        // Assert
        assertEquals(1, result.size)
        assertEquals("蜂蜜", result[0].ingredientName)
    }

    @Test
    fun `getNutritionGoals returns goals for baby`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "ageInMonths": 8,
                "calories": 800.0,
                "protein": 15.0,
                "calcium": 270.0,
                "iron": 11.0,
                "zinc": 3.0,
                "vitaminA": 500.0,
                "vitaminC": 50.0,
                "fiber": 0.0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getNutritionGoals(babyId = 1)

        // Assert
        assertEquals(1L, result.babyId)
        assertEquals(8, result.ageInMonths)
        assertEquals(800.0, result.calories, 0.01)
        assertEquals(15.0, result.protein, 0.01)
        assertEquals(270.0, result.calcium, 0.01)
        assertEquals(11.0, result.iron, 0.01)
    }

    @Test
    fun `updateNutritionGoals updates and returns goals`() = runBlocking {
        // Arrange
        val request = UpdateNutritionGoalsRequest(
            babyId = 1,
            calories = 850.0,
            protein = 16.0,
            calcium = 280.0,
            iron = 11.0,
            zinc = 3.0,
            vitaminA = 500.0,
            vitaminC = 50.0
        )

        val mockResponse = """
            {
                "babyId": 1,
                "ageInMonths": 8,
                "calories": 850.0,
                "protein": 16.0,
                "calcium": 280.0,
                "iron": 11.0,
                "zinc": 3.0,
                "vitaminA": 500.0,
                "vitaminC": 50.0,
                "fiber": 0.0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateNutritionGoals(request)

        // Assert
        assertEquals(1L, result.babyId)
        assertEquals(850.0, result.calories, 0.01)
        assertEquals(16.0, result.protein, 0.01)
    }

    @Test
    fun `calculateNutritionIntake returns calculated values`() = runBlocking {
        // Arrange
        val request = CalculateIntakeRequest(
            babyId = 1,
            recipeIds = listOf(1, 2, 3),
            portions = listOf(1.0, 0.5, 1.0)
        )

        val mockResponse = """
            {
                "calories": 450.0,
                "protein": 12.5,
                "calcium": 150.0,
                "iron": 5.0,
                "zinc": 2.0,
                "vitaminA": 200.0,
                "vitaminC": 25.0,
                "fiber": 3.0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.calculateNutritionIntake(request)

        // Assert
        assertEquals(450.0, result.calories, 0.01)
        assertEquals(12.5, result.protein, 0.01)
        assertEquals(150.0, result.calcium, 0.01)
        assertEquals(5.0, result.iron, 0.01)
    }

    @Test
    fun `getAllSafetyRisks returns all risks`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "ingredientName": "蜂蜜",
                    "riskLevel": "FORBIDDEN",
                    "reason": "可能含有肉毒杆菌",
                    "minAge": 12,
                    "handling": "12个月以下禁用"
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
        val result = apiService.getAllSafetyRisks(ageInMonths = 6)

        // Assert
        assertEquals(1, result.size)
        assertEquals("蜂蜜", result[0].ingredientName)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getNutritionGoals throws exception on server error`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // Act - should throw HttpException
        apiService.getNutritionGoals(babyId = 1)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getNutritionGoals throws exception on not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Baby not found")
        )

        // Act - should throw HttpException
        apiService.getNutritionGoals(babyId = 999)
    }
}
