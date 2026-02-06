package com.example.babyfood.presentation.ui.inventory

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.ai.ImageRecognitionService
import com.example.babyfood.data.ai.ImageRecognitionException
import com.example.babyfood.data.repository.InventoryRepository
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.ImageRecognitionRequest
import com.example.babyfood.domain.model.ImageRecognitionResponse
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val imageRecognitionService: ImageRecognitionService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "InventoryViewModel"
    }

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        Log.d(TAG, "========== 开始加载仓库物品列表 ==========")
        viewModelScope.launch {
            inventoryRepository.getAllInventoryItems().collect { items ->
                val filteredItems = applyFilters(items)
                _uiState.value = _uiState.value.copy(
                    inventoryItems = items,
                    filteredItems = filteredItems,
                    isLoading = false
                )
                Log.d(TAG, "✓ 仓库物品列表加载完成，共 ${items.size} 个物品 ==========")
            }
        }
    }

    fun searchInventoryItems(query: String) {
        Log.d(TAG, "========== 搜索仓库物品 ==========")
        Log.d(TAG, "搜索关键词: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query, isSearching = true)
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadInventoryItems()
                _uiState.value = _uiState.value.copy(isSearching = false)
            } else {
                inventoryRepository.searchInventoryItems(query).collect { items ->
                    val filteredItems = applyFilters(items)
                    _uiState.value = _uiState.value.copy(
                        inventoryItems = items,
                        filteredItems = filteredItems,
                        isSearching = false
                    )
                    Log.d(TAG, "✓ 搜索完成，找到 ${items.size} 个物品 ==========")
                }
            }
        }
    }

    fun filterByExpiryStatus(status: ExpiryStatus?) {
        Log.d(TAG, "========== 按保质期状态筛选 ==========")
        Log.d(TAG, "筛选状态: $status")
        _uiState.value = _uiState.value.copy(selectedExpiryStatus = status)
        applyCurrentFilters()
    }

    fun filterByStorageMethod(method: StorageMethod?) {
        Log.d(TAG, "========== 按保存方式筛选 ==========")
        Log.d(TAG, "筛选方式: $method")
        _uiState.value = _uiState.value.copy(selectedStorageMethod = method)
        applyCurrentFilters()
    }

    private fun applyCurrentFilters() {
        viewModelScope.launch {
            val items = if (_uiState.value.searchQuery.isNotEmpty()) {
                inventoryRepository.searchInventoryItems(_uiState.value.searchQuery)
            } else {
                inventoryRepository.getAllInventoryItems()
            }

            items.collect { allItems ->
                val filteredItems = applyFilters(allItems)
                _uiState.value = _uiState.value.copy(
                    inventoryItems = allItems,
                    filteredItems = filteredItems
                )
            }
        }
    }

    private fun applyFilters(items: List<InventoryItem>): List<InventoryItem> {
        var filtered = items

        // 按保质期状态筛选
        _uiState.value.selectedExpiryStatus?.let { status ->
            filtered = filtered.filter { it.getExpiryStatus() == status }
        }

        // 按保存方式筛选
        _uiState.value.selectedStorageMethod?.let { method ->
            filtered = filtered.filter { it.storageMethod == method }
        }

        return filtered
    }

    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "========== 保存仓库物品 ==========")
                Log.d(TAG, "物品名称: ${item.foodName}")

                if (item.id == 0L) {
                    inventoryRepository.insert(item)
                    Log.d(TAG, "✓ 新增物品成功")
                } else {
                    inventoryRepository.update(item)
                    Log.d(TAG, "✓ 更新物品成功")
                }

                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
                Log.d(TAG, "========== 保存完成 ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 保存物品失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "========== 删除仓库物品 ==========")
                Log.d(TAG, "物品名称: ${item.foodName}")
                _uiState.value = _uiState.value.copy(isDeleting = true)
                inventoryRepository.delete(item)
                _uiState.value = _uiState.value.copy(isDeleting = false)
                Log.d(TAG, "✓ 删除物品成功")
                Log.d(TAG, "========== 删除完成 ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 删除物品失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    error = e.message
                )
            }
        }
    }

    fun loadInventoryItem(itemId: Long) {
        viewModelScope.launch {
            val item = inventoryRepository.getById(itemId)
            _uiState.value = _uiState.value.copy(selectedItem = item)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedExpiryStatus = null,
            selectedStorageMethod = null
        )
        applyCurrentFilters()
    }

    /**
     * 识别食材图片
     *
     * @param imageUri 图片 URI
     */
    fun recognizeFood(imageUri: Uri) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "========== 开始识别食材 ==========")
                Log.d(TAG, "图片 URI: $imageUri")

                _uiState.value = _uiState.value.copy(
                    isRecognizing = true,
                    recognizingImageUri = imageUri,
                    recognitionError = null
                )

                // 压缩图片并转换为 Base64
                val base64 = ImageUtils.compressAndEncodeToBase64(context, imageUri)
                Log.d(TAG, "✓ 图片压缩和编码完成")

                // 调用 AI 识别
                val request = ImageRecognitionRequest(imageBase64 = base64)
                val response = imageRecognitionService.recognizeFood(request)

                Log.d(TAG, "✓ 食材识别成功: ${response.foodName}")
                Log.d(TAG, "置信度: ${response.confidence}")
                Log.d(TAG, "保存方式: ${response.storageMethod}")
                Log.d(TAG, "保质期: ${response.estimatedShelfLife}天")

                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionResult = response
                )

                Log.d(TAG, "========== 识别完成 ==========")

                // 清理临时文件
                ImageUtils.deleteTempFile(context, imageUri)

            } catch (e: ImageRecognitionException) {
                Log.e(TAG, "❌ 食材识别失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionError = e.message
                )
                // 清理临时文件
                ImageUtils.deleteTempFile(context, imageUri)
            } catch (e: Exception) {
                Log.e(TAG, "❌ 食材识别异常: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionError = "识别失败: ${e.message}"
                )
                // 清理临时文件
                ImageUtils.deleteTempFile(context, imageUri)
            }
        }
    }

    /**
     * 清除识别结果
     */
    fun clearRecognitionResult() {
        _uiState.value = _uiState.value.copy(
            recognizingImageUri = null,
            recognitionResult = null,
            recognitionError = null
        )
    }

    fun getExpiryStatistics(): ExpiryStatistics {
        val items = _uiState.value.inventoryItems
        return ExpiryStatistics(
            totalCount = items.size,
            expiredCount = items.count { it.isExpired() },
            expiringCount = items.count { it.getExpiryStatus() == ExpiryStatus.URGENT },
            warningCount = items.count { it.getExpiryStatus() == ExpiryStatus.WARNING },
            normalCount = items.count { it.getExpiryStatus() == ExpiryStatus.NORMAL }
        )
    }
}

data class InventoryUiState(
    val inventoryItems: List<InventoryItem> = emptyList(),
    val filteredItems: List<InventoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedItem: InventoryItem? = null,
    val searchQuery: String = "",
    val selectedExpiryStatus: ExpiryStatus? = null,
    val selectedStorageMethod: StorageMethod? = null,
    // 图像识别相关字段
    val isRecognizing: Boolean = false,
    val recognizingImageUri: android.net.Uri? = null,  // 正在识别的图片URI
    val recognitionResult: ImageRecognitionResponse? = null,
    val recognitionError: String? = null,
    // 搜索加载状态
    val isSearching: Boolean = false,
    // 删除加载状态
    val isDeleting: Boolean = false
)

data class ExpiryStatistics(
    val totalCount: Int = 0,
    val expiredCount: Int = 0,
    val expiringCount: Int = 0,
    val warningCount: Int = 0,
    val normalCount: Int = 0
) {
    val urgentCount: Int
        get() = expiredCount + expiringCount
}