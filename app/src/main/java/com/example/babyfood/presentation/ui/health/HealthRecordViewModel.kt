package com.example.babyfood.presentation.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.HealthRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthRecordViewModel @Inject constructor(
    private val healthRecordRepository: HealthRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthRecordUiState())
    val uiState: StateFlow<HealthRecordUiState> = _uiState.asStateFlow()

    fun loadHealthRecords(babyId: Long) {
        viewModelScope.launch {
            healthRecordRepository.getHealthRecordsByBaby(babyId).collect { records ->
                _uiState.value = _uiState.value.copy(
                    healthRecords = records,
                    isLoading = false
                )
            }
        }
    }

    fun saveHealthRecord(record: HealthRecord) {
        viewModelScope.launch {
            try {
                if (record.id == 0L) {
                    healthRecordRepository.insertHealthRecord(record)
                } else {
                    healthRecordRepository.updateHealthRecord(record)
                }
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteHealthRecord(record: HealthRecord) {
        viewModelScope.launch {
            try {
                healthRecordRepository.deleteHealthRecord(record)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}

data class HealthRecordUiState(
    val healthRecords: List<HealthRecord> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)