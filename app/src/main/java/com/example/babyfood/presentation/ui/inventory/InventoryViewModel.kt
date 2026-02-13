package com.example.babyfood.presentation.ui.inventory

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.ai.ImageRecognitionException
import com.example.babyfood.data.ai.ImageRecognitionService
import com.example.babyfood.data.repository.InventoryRepository
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.ImageRecognitionRequest
import com.example.babyfood.domain.model.ImageRecognitionResponse
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.presentation.ui.BaseViewModel
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
) : BaseViewModel() {

    override val logTag: String = "InventoryViewModel"

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        logMethodStart("InventoryViewModel 初始化")
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        logMethodStart("加载仓库物品列表")
        viewModelScope.launch {
            inventoryRepository.getAllInventoryItems().collect { items ->
                updateStateWithFilteredItems(items, isSearching = false)
                logSuccess("仓库物品列表加载完成，共 ${items.size} 个物品")
                logMethodEnd("加载仓库物品列表")
            }
        }
    }

    fun searchInventoryItems(query: String) {
        logMethodStart("搜索仓库物品")
        logD("搜索关键词: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query, isSearching = true)
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadInventoryItems()
            } else {
                inventoryRepository.searchInventoryItems(query).collect { items ->
                    updateStateWithFilteredItems(items, isSearching = false)
                    logSuccess("搜索完成，找到 ${items.size} 个物品")
                    logMethodEnd("搜索仓库物品")
                }
            }
        }
    }

    fun filterByExpiryStatus(status: ExpiryStatus?) {
        logD("按保质期状态筛选: $status")
        _uiState.value = _uiState.value.copy(selectedExpiryStatus = status)
        refreshFilters()
    }

    fun filterByStorageMethod(method: StorageMethod?) {
        logD("按保存方式筛选: $method")
        _uiState.value = _uiState.value.copy(selectedStorageMethod = method)
        refreshFilters()
    }

    private fun refreshFilters() {
        viewModelScope.launch {
            val source = if (_uiState.value.searchQuery.isNotEmpty()) {
                inventoryRepository.searchInventoryItems(_uiState.value.searchQuery)
            } else {
                inventoryRepository.getAllInventoryItems()
            }
            source.collect { items ->
                updateStateWithFilteredItems(items)
            }
        }
    }

    private fun updateStateWithFilteredItems(items: List<InventoryItem>, isSearching: Boolean? = null) {
        val filteredItems = applyFilters(items)
        val update = _uiState.value.copy(
            inventoryItems = items,
            filteredItems = filteredItems,
            isLoading = false
        )
        _uiState.value = if (isSearching != null) {
            update.copy(isSearching = isSearching)
        } else {
            update
        }
    }

    private fun applyFilters(items: List<InventoryItem>): List<InventoryItem> {
        return items.filter { item ->
            val matchesExpiryStatus = _uiState.value.selectedExpiryStatus?.let { status ->
                item.getExpiryStatus() == status
            } ?: true
            val matchesStorageMethod = _uiState.value.selectedStorageMethod?.let { method ->
                item.storageMethod == method
            } ?: true
            matchesExpiryStatus && matchesStorageMethod
        }
    }

    fun saveInventoryItem(item: InventoryItem) {
        logMethodStart("保存仓库物品")
        logD("物品名称: ${item.foodName}")

        safeLaunch("保存仓库物品") {
            if (item.id == 0L) {
                inventoryRepository.insert(item)
                logSuccess("新增物品成功")
            } else {
                inventoryRepository.update(item)
                logSuccess("更新物品成功")
            }

            _uiState.value = _uiState.value.copy(
                isSaved = true,
                error = null
            )
            logMethodEnd("保存仓库物品")
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        logMethodStart("删除仓库物品")
        logD("物品名称: ${item.foodName}")

        safeLaunch("删除仓库物品") {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            inventoryRepository.delete(item)
            _uiState.value = _uiState.value.copy(isDeleting = false)
            logSuccess("删除物品成功")
            logMethodEnd("删除仓库物品")
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
        refreshFilters()
    }

    /**
     * 识别食材图片
     * @param imageUri 图片 URI
     */
    fun recognizeFood(imageUri: Uri) {
        logMethodStart("识别食材")
        logD("图片 URI: $imageUri")

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isRecognizing = true,
                    recognizingImageUri = imageUri,
                    recognitionError = null
                )

                // 压缩图片并转换为 Base64
                val base64 = ImageUtils.compressAndEncodeToBase64(context, imageUri)
                logSuccess("图片压缩和编码完成")

                // 调用 AI 识别
                val request = ImageRecognitionRequest(imageBase64 = base64)
                val response = imageRecognitionService.recognizeFood(request)

                logSuccess("食材识别成功: ${response.foodName}")
                logD("置信度: ${response.confidence}, 保存方式: ${response.storageMethod}, 保质期: ${response.estimatedShelfLife}天")

                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionResult = response
                )

                logMethodEnd("识别食材")

                // 清理临时文件
                ImageUtils.deleteTempFile(context, imageUri)

            } catch (e: ImageRecognitionException) {
                logError("食材识别失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionError = e.message
                )
                ImageUtils.deleteTempFile(context, imageUri)
            } catch (e: Exception) {
                logError("食材识别异常: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isRecognizing = false,
                    recognitionError = "识别失败: ${e.message}"
                )
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
    val recognizingImageUri: android.net.Uri? = null,
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
