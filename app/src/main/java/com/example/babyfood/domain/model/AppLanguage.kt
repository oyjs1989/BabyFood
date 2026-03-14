package com.example.babyfood.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    CHINESE("zh", "简体中文"),
    ENGLISH("en", "English"),
    FOLLOW_SYSTEM("auto", "跟随系统");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code == code } ?: FOLLOW_SYSTEM
        }
    }
}
