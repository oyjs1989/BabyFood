package com.example.babyfood.presentation.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.HealthRecord
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
                _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            } catch (e: Exception) {
                _uiState.setError(e.message) { error -> copy(error = error) }
            }
        }
    }

    fun deleteHealthRecord(record: HealthRecord) {
        viewModelScope.launch {
            try {
                healthRecordRepository.deleteHealthRecord(record)
            } catch (e: Exception) {
                _uiState.setError(e.message) { error -> copy(error = error) }
            }
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