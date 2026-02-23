package com.example.babyfood.domain.model

/**
 * 食材反应枚举
 * 
 * 用于记录宝宝对食材的反应程度
 */
enum class ReactionType {
    NONE,       // 无反应
    MILD,       // 轻微反应
    MODERATE,   // 中等反应
    SEVERE      // 严重反应/过敏
}
