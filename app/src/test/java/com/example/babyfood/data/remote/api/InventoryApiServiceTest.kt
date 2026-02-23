package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.inventory.*
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
 * InventoryApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
class InventoryApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: InventoryApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(InventoryApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getInventoryItems returns list of items`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 1,
                    "babyId": 1,
                    "name": "胡萝卜",
                    "category": "蔬菜",
                    "quantity": 5,
                    "unit": "根",
                    "productionDate": "2026-01-01",
                    "expiryDate": "2026-02-01",
                    "storageMethod": "REFRIGERATED",
                    "notes": "有机胡萝卜",
                    "expiryStatus": "FRESH",
                    "daysUntilExpiry": 10
                },
                {
                    "id": 2,
                    "babyId": 1,
                    "name": "三文鱼",
                    "category": "肉类",
                    "quantity": 2,
                    "unit": "块",
                    "productionDate": "2026-01-10",
                    "expiryDate": "2026-01-20",
                    "storageMethod": "FROZEN",
                    "notes": "挪威三文鱼",
                    "expiryStatus": "EXPIRING_SOON",
                    "daysUntilExpiry": 3
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
        val result = apiService.getInventoryItems(babyId = 1, category = null, status = null)

        // Assert
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("胡萝卜", result[0].name)
        assertEquals("REFRIGERATED", result[0].storageMethod)
        assertEquals("FRESH", result[0].expiryStatus)
        assertEquals(2L, result[1].id)
        assertEquals("三文鱼", result[1].name)
        assertEquals("FROZEN", result[1].storageMethod)
        assertEquals("EXPIRING_SOON", result[1].expiryStatus)
    }

    @Test
    fun `getInventoryItem returns single item`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "name": "胡萝卜",
                "category": "蔬菜",
                "quantity": 5,
                "unit": "根",
                "productionDate": "2026-01-01",
                "expiryDate": "2026-02-01",
                "storageMethod": "REFRIGERATED",
                "notes": "有机胡萝卜",
                "expiryStatus": "FRESH",
                "daysUntilExpiry": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getInventoryItem(itemId = 1)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1L, result.babyId)
        assertEquals("胡萝卜", result.name)
        assertEquals("蔬菜", result.category)
        assertEquals(5, result.quantity)
        assertEquals("REFRIGERATED", result.storageMethod)
        assertEquals("FRESH", result.expiryStatus)
        assertEquals(10, result.daysUntilExpiry)
    }

    @Test
    fun `createInventoryItem creates and returns new item`() = runBlocking {
        // Arrange
        val request = CreateInventoryItemRequest(
            babyId = 1,
            name = "牛肉",
            category = "肉类",
            quantity = 500,
            unit = "克",
            productionDate = "2026-01-10",
            expiryDate = "2026-01-25",
            storageMethod = "FROZEN",
            notes = "澳洲牛肉"
        )

        val mockResponse = """
            {
                "id": 3,
                "babyId": 1,
                "name": "牛肉",
                "category": "肉类",
                "quantity": 500,
                "unit": "克",
                "productionDate": "2026-01-10",
                "expiryDate": "2026-01-25",
                "storageMethod": "FROZEN",
                "notes": "澳洲牛肉",
                "expiryStatus": "FRESH",
                "daysUntilExpiry": 8
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.createInventoryItem(request)

        // Assert
        assertEquals(3L, result.id)
        assertEquals("牛肉", result.name)
        assertEquals(500, result.quantity)
        assertEquals("FROZEN", result.storageMethod)
        assertEquals("FRESH", result.expiryStatus)
    }

    @Test
    fun `updateInventoryItem updates and returns item`() = runBlocking {
        // Arrange
        val request = UpdateInventoryItemRequest(
            quantity = 3,
            notes = "已用掉2根"
        )

        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "name": "胡萝卜",
                "category": "蔬菜",
                "quantity": 3,
                "unit": "根",
                "productionDate": "2026-01-01",
                "expiryDate": "2026-02-01",
                "storageMethod": "REFRIGERATED",
                "notes": "已用掉2根",
                "expiryStatus": "FRESH",
                "daysUntilExpiry": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.updateInventoryItem(itemId = 1, request = request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(3, result.quantity)
        assertEquals("已用掉2根", result.notes)
    }

    @Test
    fun `deleteInventoryItem returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        // Act - should not throw
        apiService.deleteInventoryItem(itemId = 1)

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
    }

    @Test
    fun `getExpiryAlerts returns expiring items`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 2,
                    "babyId": 1,
                    "name": "三文鱼",
                    "category": "肉类",
                    "quantity": 2,
                    "unit": "块",
                    "productionDate": "2026-01-10",
                    "expiryDate": "2026-01-20",
                    "storageMethod": "FROZEN",
                    "notes": "挪威三文鱼",
                    "expiryStatus": "EXPIRING_SOON",
                    "daysUntilExpiry": 3
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
        val result = apiService.getExpiryAlerts(babyId = 1, daysThreshold = 7)

        // Assert
        assertEquals(1, result.size)
        assertEquals("三文鱼", result[0].name)
        assertEquals("EXPIRING_SOON", result[0].expiryStatus)
        assertEquals(3, result[0].daysUntilExpiry)
    }

    @Test
    fun `getInventoryStats returns statistics`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "babyId": 1,
                "totalItems": 10,
                "categories": {
                    "蔬菜": 4,
                    "肉类": 3,
                    "水果": 2,
                    "谷物": 1
                },
                "expiringSoonCount": 2,
                "expiredCount": 0,
                "freshCount": 8
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.getInventoryStats(babyId = 1)

        // Assert
        assertEquals(1L, result.babyId)
        assertEquals(10, result.totalItems)
        assertEquals(4, result.categories["蔬菜"])
        assertEquals(2, result.expiringSoonCount)
        assertEquals(0, result.expiredCount)
        assertEquals(8, result.freshCount)
    }

    @Test
    fun `consumeItem reduces quantity`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "babyId": 1,
                "name": "胡萝卜",
                "category": "蔬菜",
                "quantity": 2,
                "unit": "根",
                "productionDate": "2026-01-01",
                "expiryDate": "2026-02-01",
                "storageMethod": "REFRIGERATED",
                "notes": "",
                "expiryStatus": "FRESH",
                "daysUntilExpiry": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.consumeItem(itemId = 1, amount = 3)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(2, result.quantity)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getInventoryItem throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Inventory item not found")
        )

        // Act - should throw HttpException
        apiService.getInventoryItem(itemId = 999)
    }
}
