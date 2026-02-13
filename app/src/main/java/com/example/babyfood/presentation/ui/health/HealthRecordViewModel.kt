package com.example.babyfood.presentation.ui.health

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.HealthRecord
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
class HealthRecordViewModel @Inject constructor(
    private val healthRecordRepository: HealthRecordRepository
) : BaseViewModel() {

    override val logTag: String = "HealthRecordViewModel"

    private val _uiState = MutableStateFlow(HealthRecordUiState())
    val uiState: StateFlow<HealthRecordUiState> = _uiState.asStateFlow()

    fun loadHealthRecords(babyId: Long) {
        logMethodStart("加载体检记录")
        logD("宝宝 ID: $babyId")

        viewModelScope.launch {
            healthRecordRepository.getHealthRecordsByBaby(babyId).collect { records ->
                _uiState.value = _uiState.value.copy(
                    healthRecords = records,
                    isLoading = false
                )
                logSuccess("体检记录加载完成，共 ${records.size} 条记录")
                logMethodEnd("加载体检记录")
            }
        }
    }

    fun saveHealthRecord(record: HealthRecord) {
        logMethodStart("保存体检记录")
        logD("记录 ID: ${record.id}, 日期: ${record.recordDate}")

        safeLaunch("保存体检记录") {
            if (record.id == 0L) {
                healthRecordRepository.insertHealthRecord(record)
                logSuccess("体检记录创建成功")
            } else {
                healthRecordRepository.update(record)
                logSuccess("体检记录更新成功")
            }
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logMethodEnd("保存体检记录")
        }
    }

    fun deleteHealthRecord(record: HealthRecord) {
        logMethodStart("删除体检记录")
        logD("记录 ID: ${record.id}")

        safeLaunch("删除体检记录") {
            healthRecordRepository.delete(record)
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logSuccess("体检记录删除成功")
            logMethodEnd("删除体检记录")
        }
    }

    fun clearError() {
        _uiState.clearError { error -> copy(error = error) }
    }

    fun clearSavedFlag() {
        _uiState.setSaved(false) { isSaved -> copy(isSaved = isSaved) }
    }
}

data class HealthRecordUiState(
    val healthRecords: List<HealthRecord> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)
