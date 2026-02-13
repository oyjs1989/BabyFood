package com.example.babyfood.presentation.ui.growth

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.GrowthRecordRepository
import com.example.babyfood.domain.model.GrowthRecord
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.presentation.ui.clearError
import com.example.babyfood.presentation.ui.clearErrorAndSaved
import com.example.babyfood.presentation.ui.setError
import com.example.babyfood.presentation.ui.setSaved
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrowthRecordViewModel @Inject constructor(
    private val growthRecordRepository: GrowthRecordRepository
) : BaseViewModel() {

    override val logTag: String = "GrowthRecordViewModel"

    private val _uiState = MutableStateFlow(GrowthRecordUiState())
    val uiState: StateFlow<GrowthRecordUiState> = _uiState.asStateFlow()

    fun loadGrowthRecords(babyId: Long) {
        logMethodStart("加载生长记录")
        logD("宝宝 ID: $babyId")

        viewModelScope.launch {
            growthRecordRepository.getGrowthRecordsByBaby(babyId).collect { records ->
                _uiState.value = _uiState.value.copy(
                    growthRecords = records,
                    isLoading = false
                )
                logSuccess("生长记录加载完成，共 ${records.size} 条记录")
                logMethodEnd("加载生长记录")
            }
        }
    }

    fun saveGrowthRecord(record: GrowthRecord) {
        logMethodStart("保存生长记录")
        logD("记录 ID: ${record.id}, 日期: ${record.recordDate}")

        safeLaunch("保存生长记录") {
            if (record.id == 0L) {
                growthRecordRepository.insertGrowthRecord(record)
                logSuccess("生长记录创建成功")
            } else {
                growthRecordRepository.updateGrowthRecord(record)
                logSuccess("生长记录更新成功")
            }
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logMethodEnd("保存生长记录")
        }
    }

    fun deleteGrowthRecord(record: GrowthRecord) {
        logMethodStart("删除生长记录")
        logD("记录 ID: ${record.id}")

        safeLaunch("删除生长记录") {
            growthRecordRepository.deleteGrowthRecord(record)
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logSuccess("生长记录删除成功")
            logMethodEnd("删除生长记录")
        }
    }

    fun clearError() {
        _uiState.clearError { error -> copy(error = error) }
    }

    fun clearSavedFlag() {
        _uiState.setSaved(false) { isSaved -> copy(isSaved = isSaved) }
    }
}

data class GrowthRecordUiState(
    val growthRecords: List<GrowthRecord> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)
