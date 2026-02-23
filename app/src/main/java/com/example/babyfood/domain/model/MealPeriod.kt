package com.example.babyfood.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MealPeriod(val displayName: String, val order: Int) {
    @SerialName("BREAKFAST")
    BREAKFAST("早餐", 0),

    @SerialName("LUNCH")
    LUNCH("午餐", 1),

    @SerialName("SNACK")
    SNACK("点心", 2),

    @SerialName("DINNER")
    DINNER("晚餐", 3)
}