package com.example.babyfood.data.manager

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.domain.manager.LanguageManager
import com.example.babyfood.domain.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManagerImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : LanguageManager {

    private val _currentLanguage = MutableStateFlow(
        AppLanguage.fromCode(preferencesManager.getLanguageCode())
    )
    override val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        // 保存偏好
        preferencesManager.saveLanguage(language.code)
        _currentLanguage.value = language
        
        // 动态应用 Locale
        val localeList = if (language == AppLanguage.FOLLOW_SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.code)
        }
        
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    override fun getCurrentLocale(): Locale {
        return when (currentLanguage.value) {
            AppLanguage.CHINESE -> Locale.SIMPLIFIED_CHINESE
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.FOLLOW_SYSTEM -> Locale.getDefault()
        }
    }
}
