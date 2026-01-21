package com.example.babyfood.domain.model

enum class MealPeriod(val displayName: String, val order: Int) {
    BREAKFAST("早餐", 0),
    LUNCH("午餐", 1),
    DINNER("晚餐", 2),
    SNACK("点心", 3)
}