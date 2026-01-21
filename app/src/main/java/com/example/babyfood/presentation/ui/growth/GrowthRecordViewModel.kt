package com.example.babyfood.presentation.ui.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.GrowthRecordRepository
import com.example.babyfood.domain.model.GrowthRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrowthRecordViewModel @Inject constructor(
    private val growthRecordRepository: GrowthRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthRecordUiState())
    val uiState: StateFlow<GrowthRecordUiState> = _uiState.asStateFlow()

    fun loadGrowthRecords(babyId: Long) {
        viewModelScope.launch {
            growthRecordRepository.getGrowthRecordsByBaby(babyId).collect { records ->
                _uiState.value = _uiState.value.copy(
                    growthRecords = records,
                    isLoading = false
                )
            }
        }
    }

    fun saveGrowthRecord(record: GrowthRecord) {
        viewModelScope.launch {
            try {
                if (record.id == 0L) {
                    growthRecordRepository.insertGrowthRecord(record)
                } else {
                    growthRecordRepository.updateGrowthRecord(record)
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

    fun deleteGrowthRecord(record: GrowthRecord) {
        viewModelScope.launch {
            try {
                growthRecordRepository.deleteGrowthRecord(record)
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

data class GrowthRecordUiState(
    val growthRecords: List<GrowthRecord> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)