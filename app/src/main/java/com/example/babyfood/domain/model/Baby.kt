package com.example.babyfood.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Baby(
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val allergies: List<String> = emptyList(),
    val weight: Float? = null,      // kg
    val height: Float? = null,      // cm
    val preferences: List<String> = emptyList()
) {
    val ageInMonths: Int
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val months = (today.year - birthDate.year) * 12 + (today.monthNumber - birthDate.monthNumber)
            return if (today.dayOfMonth >= birthDate.dayOfMonth) months else months - 1
        }
}