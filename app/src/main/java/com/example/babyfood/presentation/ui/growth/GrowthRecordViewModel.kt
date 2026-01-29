package com.example.babyfood.presentation.ui.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.GrowthRecordRepository
import com.example.babyfood.domain.model.GrowthRecord
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
                _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            } catch (e: Exception) {
                _uiState.setError(e.message) { error -> copy(error = error) }
            }
        }
    }

    fun deleteGrowthRecord(record: GrowthRecord) {
        viewModelScope.launch {
            try {
                growthRecordRepository.deleteGrowthRecord(record)
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

data class GrowthRecordUiState(
    val growthRecords: List<GrowthRecord> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)