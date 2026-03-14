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
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * NutritionApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
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
    fun `getSafetyRisk returns risk info`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "ingredientName": "蜂蜜",
                "riskLevel": "HIGH",
                "minAgeMonths": 12,
                "maxAgeMonths": 120,
                "warningMessage": "可能含有肉毒杆菌孢子",
                "handlingTips": "1岁以前严禁食用"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getSafetyRisk(ingredientName = "蜂蜜", ageInMonths = 6)

        // Assert
        assertEquals("蜂蜜", result.ingredientName)
        assertEquals("HIGH", result.riskLevel)
        assertEquals(12, result.minAgeMonths)
    }

    @Test
    fun `getNutritionData returns data`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "ingredientName": "菠菜",
                "calories": 23.0,
                "protein": 2.9,
                "iron": 2.7,
                "calcium": 99.0,
                "vitaminA": 469.0,
                "vitaminC": 28.0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getNutritionData(ingredientName = "菠菜")

        // Assert
        assertEquals("菠菜", result.ingredientName)
        assertEquals(23.0, result.calories, 0.01)
        assertEquals(2.7, result.iron, 0.01)
    }

    @Test
    fun `getNutritionGoals returns goals`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "calories": 700.0,
                "protein": 20.0,
                "iron": 10.0,
                "calcium": 600.0,
                "createdAt": "2026-03-15T10:30:00",
                "updatedAt": "2026-03-15T10:30:00"
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
        assertEquals(1, result.babyId)
        assertEquals(700.0, result.calories, 0.01)
        assertEquals(10.0, result.iron, 0.01)
    }

    @Test
    fun `updateNutritionGoals returns updated goals`() = runBlocking {
        // Arrange
        val request = NutritionGoalsUpdate(
            calories = 750.0,
            protein = 20.0,
            calcium = 600.0,
            iron = 11.0
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "calories": 750.0,
                "protein": 20.0,
                "iron": 11.0,
                "calcium": 600.0,
                "createdAt": "2026-03-15T10:30:00",
                "updatedAt": "2026-03-15T11:00:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateNutritionGoals(babyId = 1, goals = request)

        // Assert
        assertEquals(750.0, result.calories, 0.01)
        assertEquals(11.0, result.iron, 0.01)
    }

    @Test
    fun `calculateNutritionIntake returns analysis`() = runBlocking {
        // Arrange
        val request = mapOf(
            "calories" to 600.0,
            "protein" to 15.0,
            "iron" to 8.0,
            "calcium" to 500.0
        )

        val mockResponse = """
            {
                "babyId": 1,
                "actualIntake": {
                    "calories": 600.0,
                    "protein": 15.0,
                    "iron": 8.0,
                    "calcium": 500.0
                },
                "goals": {
                    "id": 1,
                    "babyId": 1,
                    "calories": 700.0,
                    "protein": 20.0,
                    "iron": 10.0,
                    "calcium": 600.0,
                    "createdAt": "2026-03-15T10:30:00",
                    "updatedAt": "2026-03-15T10:30:00"
                },
                "warnings": ["铁摄入不足"]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.calculateNutritionIntake(babyId = 1, intake = request)

        // Assert
        assertEquals(600.0, result.actualIntake.calories, 0.01)
        assertEquals(1, result.warnings.size)
    }

    @Test
    fun `getTextureAdvice returns advice`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "ageInMonths": 7,
                "textureType": "MASH",
                "description": "细滑泥糊，逐渐过渡到带有细小颗粒的稠糊",
                "developmentalStage": "尝试吞咽",
                "chewingAbility": "舌头前后移动"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getTextureAdvice(ageInMonths = 7)

        // Assert
        assertEquals(7, result.ageInMonths)
        assertEquals("MASH", result.textureType)
    }
}
