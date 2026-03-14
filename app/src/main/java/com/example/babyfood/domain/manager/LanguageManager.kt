package com.example.babyfood.domain.manager

import com.example.babyfood.domain.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface LanguageManager {
    /** 当前生效的语言 */
    val currentLanguage: StateFlow<AppLanguage>
    
    /** 切换语言 */
    fun setLanguage(language: AppLanguage)
    
    /** 获取当前 Locale（用于 DateFormatter 等） */
    fun getCurrentLocale(): Locale
}
