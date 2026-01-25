package com.example.babyfood.presentation.ui.baby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BabyViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val healthRecordRepository: HealthRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BabyUiState())
    val uiState: StateFlow<BabyUiState> = _uiState.asStateFlow()

    init {
        loadBabies()
    }

    private fun loadBabies() {
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    isLoading = false
                )
            }
        }
    }

    fun saveBaby(baby: Baby) {
        viewModelScope.launch {
            try {
                if (baby.id == 0L) {
                    babyRepository.insertBaby(baby)
                } else {
                    babyRepository.updateBaby(baby)
                }
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deleteBaby(baby: Baby) {
        viewModelScope.launch {
            try {
                babyRepository.deleteBaby(baby)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun loadBaby(babyId: Long) {
        viewModelScope.launch {
            val baby = babyRepository.getBabyById(babyId)
            _uiState.value = _uiState.value.copy(selectedBaby = baby)
            loadLatestHealthRecord(babyId)
        }
    }

    fun loadLatestHealthRecord(babyId: Long) {
        viewModelScope.launch {
            val latestRecord = healthRecordRepository.getLatestHealthRecord(babyId)
            _uiState.value = _uiState.value.copy(latestHealthRecord = latestRecord)
        }
    }
}

data class BabyUiState(
    val babies: List<Baby> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedBaby: Baby? = null,
    val latestHealthRecord: HealthRecord? = null
)