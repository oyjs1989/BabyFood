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
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * InventoryApiService 集成测试
 * 使用 MockWebServer 模拟后端 API
 */
@RunWith(JUnit4::class)
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
                    "cloudId": "item_1",
                    "userId": 1,
                    "name": "苹果",
                    "quantity": 5.0,
                    "unit": "个",
                    "storageMethod": "REFRIGERATOR",
                    "expiryDate": "2026-04-15",
                    "createdAt": "2026-03-15T10:30:00",
                    "updatedAt": "2026-03-15T10:30:00"
                },
                {
                    "id": 2,
                    "cloudId": "item_2",
                    "userId": 1,
                    "name": "菠菜",
                    "quantity": 200.0,
                    "unit": "克",
                    "storageMethod": "REFRIGERATOR",
                    "expiryDate": "2026-03-20",
                    "createdAt": "2026-03-15T14:20:00",
                    "updatedAt": "2026-03-15T14:20:00"
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
        val result = apiService.getInventoryItems()

        // Assert
        assertEquals(2, result.size)
        assertEquals("item_1", result[0].cloudId)
        assertEquals("苹果", result[0].name)
        assertEquals(5.0, result[0].quantity, 0.01)
    }

    @Test
    fun `getInventoryItem returns single item`() = runBlocking {
        // Arrange
        val mockResponse = """
            {
                "id": 1,
                "cloudId": "item_1",
                "userId": 1,
                "name": "苹果",
                "quantity": 5.0,
                "unit": "个",
                "storageMethod": "REFRIGERATOR",
                "expiryDate": "2026-04-15",
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
        val result = apiService.getInventoryItem(cloudId = "item_1")

        // Assert
        assertEquals("item_1", result.cloudId)
        assertEquals("苹果", result.name)
    }

    @Test
    fun `createInventoryItem creates and returns new item`() = runBlocking {
        // Arrange
        val request = InventoryItemCreate(
            name = "胡萝卜",
            quantity = 3.0,
            unit = "根",
            expiryDate = "2026-04-01",
            storageMethod = "REFRIGERATOR",
            notes = "新鲜购买"
        )

        val mockResponse = """
            {
                "id": 3,
                "cloudId": "item_3",
                "userId": 1,
                "name": "胡萝卜",
                "quantity": 3.0,
                "unit": "根",
                "storageMethod": "REFRIGERATOR",
                "expiryDate": "2026-04-01",
                "createdAt": "2026-03-15T09:00:00",
                "updatedAt": "2026-03-15T09:00:00"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val result = apiService.createInventoryItem(item = request)

        // Assert
        assertEquals("item_3", result.cloudId)
        assertEquals("胡萝卜", result.name)
    }

    @Test
    fun `updateInventoryItem updates and returns item`() = runBlocking {
        // Arrange
        val request = InventoryItemCreate(
            name = "苹果",
            quantity = 4.0,
            unit = "个",
            expiryDate = "2026-04-15",
            storageMethod = "REFRIGERATOR",
            notes = "吃掉了一个"
        )

        val mockResponse = """
            {
                "id": 1,
                "cloudId": "item_1",
                "userId": 1,
                "name": "苹果",
                "quantity": 4.0,
                "unit": "个",
                "storageMethod": "REFRIGERATOR",
                "expiryDate": "2026-04-15",
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
        val result = apiService.updateInventoryItem(cloudId = "item_1", item = request)

        // Assert
        assertEquals("item_1", result.cloudId)
        assertEquals(4.0, result.quantity, 0.01)
    }

    @Test
    fun `deleteInventoryItem returns success`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .addHeader("Content-Type", "application/json")
        )

        // Act - should not throw
        apiService.deleteInventoryItem(cloudId = "item_1")

        // Assert - verify request was made
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/inventory-items/item_1", request.path)
    }

    @Test
    fun `getExpiredAndUrgentItems returns list`() = runBlocking {
        // Arrange
        val mockResponse = """
            [
                {
                    "id": 2,
                    "cloudId": "item_2",
                    "userId": 1,
                    "name": "菠菜",
                    "quantity": 200.0,
                    "unit": "克",
                    "storageMethod": "REFRIGERATOR",
                    "expiryDate": "2026-03-20",
                    "createdAt": "2026-03-15T14:20:00",
                    "updatedAt": "2026-03-15T14:20:00"
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
        val result = apiService.getExpiredAndUrgentItems()

        // Assert
        assertEquals(1, result.size)
        assertEquals("item_2", result[0].cloudId)
    }

    @Test
    fun `getInventoryItem throws exception when not found`() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Inventory item not found")
        )

        // Act & Assert
        try {
            apiService.getInventoryItem(cloudId = "non_existent")
            fail("Should throw HttpException")
        } catch (e: retrofit2.HttpException) {
            assertEquals(404, e.code())
        }
    }
}
