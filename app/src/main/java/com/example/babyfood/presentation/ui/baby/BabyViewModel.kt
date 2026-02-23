package com.example.babyfood.presentation.ui.baby

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import com.example.babyfood.presentation.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BabyViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    override val logTag: String = "BabyViewModel"

    private val _uiState = MutableStateFlow(BabyUiState())
    val uiState: StateFlow<BabyUiState> = _uiState.asStateFlow()

    init {
        loadBabies()
    }

    private fun loadBabies() {
        logMethodStart("加载宝宝列表")
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                // 加载当前选中的宝宝 ID
                val selectedBabyId = preferencesManager.getSelectedBabyId()
                logD("当前选中的宝宝 ID: $selectedBabyId")

                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    isLoading = false,
                    selectedBabyId = selectedBabyId
                )
                logSuccess("宝宝列表加载完成，共 ${babies.size} 个宝宝")
                logMethodEnd("加载宝宝列表")
            }
        }
    }

    fun saveBaby(baby: Baby) {
        logMethodStart("保存宝宝")
        safeLaunch("保存宝宝") {
            if (baby.id == 0L) {
                babyRepository.insert(baby)
                logSuccess("宝宝创建成功")
            } else {
                babyRepository.update(baby)
                logSuccess("宝宝更新成功")
            }
            _uiState.value = _uiState.value.copy(
                isSaved = true,
                error = null
            )
            logMethodEnd("保存宝宝")
        }
    }

    fun deleteBaby(baby: Baby) {
        logMethodStart("删除宝宝")
        safeLaunch("删除宝宝") {
            babyRepository.delete(baby)
            logSuccess("宝宝删除成功")
            logMethodEnd("删除宝宝")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun loadBaby(babyId: Long) {
        logMethodStart("加载宝宝详情")
        safeLaunch("加载宝宝详情") {
            val baby = babyRepository.getById(babyId)
            _uiState.value = _uiState.value.copy(selectedBaby = baby)
            loadLatestHealthRecord(babyId)
            logMethodEnd("加载宝宝详情")
        }
    }

    fun loadLatestHealthRecord(babyId: Long) {
        viewModelScope.launch {
            val latestRecord = healthRecordRepository.getLatestHealthRecord(babyId)
            _uiState.value = _uiState.value.copy(latestHealthRecord = latestRecord)
        }
    }

    /**
     * 设置宝宝为当前选中的宝宝
     * @param baby 要设置为当前的宝宝
     */
    fun setAsCurrentBaby(baby: Baby) {
        logMethodStart("设置宝宝为当前")
        logD("宝宝: ${baby.name} (ID: ${baby.id})")

        // 保存到 SharedPreferences
        preferencesManager.saveSelectedBabyId(baby.id)

        // 更新 UI 状态
        _uiState.value = _uiState.value.copy(selectedBabyId = baby.id)

        logSuccess("设置完成")
        logMethodEnd("设置宝宝为当前")
    }

    /**
     * 清除当前选中的宝宝
     */
    fun clearCurrentBaby() {
        logMethodStart("清除当前宝宝")
        preferencesManager.clearSelectedBabyId()
        _uiState.value = _uiState.value.copy(selectedBabyId = -1L)
        logSuccess("清除完成")
        logMethodEnd("清除当前宝宝")
    }
}

data class BabyUiState(
    val babies: List<Baby> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedBaby: Baby? = null,
    val latestHealthRecord: HealthRecord? = null,
    val selectedBabyId: Long = -1L  // 当前选中的宝宝 ID（用于全局状态）
)
