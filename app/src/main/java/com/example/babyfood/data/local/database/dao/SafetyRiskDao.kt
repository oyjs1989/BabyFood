package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.babyfood.data.local.database.entity.SafetyRiskEntity

@Dao
interface SafetyRiskDao {
    @Query("SELECT * FROM safety_risks WHERE ingredientName = :ingredientName LIMIT 1")
    suspend fun getByIngredientName(ingredientName: String): SafetyRiskEntity?

    @Query("SELECT * FROM safety_risks WHERE riskLevel = 'FORBIDDEN'")
    suspend fun getAllForbiddenIngredients(): List<SafetyRiskEntity>

    @Query("SELECT * FROM safety_risks WHERE (:age >= applicableAgeRangeStart OR applicableAgeRangeStart IS NULL) AND (:age <= applicableAgeRangeEnd OR applicableAgeRangeEnd IS NULL)")
    suspend fun getRisksByAge(age: Int): List<SafetyRiskEntity>

    @Query("SELECT * FROM safety_risks")
    suspend fun getAllRisks(): List<SafetyRiskEntity>

    @Query("INSERT INTO safety_risks (ingredientName, riskLevel, riskReason, handlingAdvice, applicableAgeRangeStart, applicableAgeRangeEnd, severity, dataSource, createdAt) VALUES (:ingredientName, :riskLevel, :riskReason, :handlingAdvice, :applicableAgeRangeStart, :applicableAgeRangeEnd, :severity, :dataSource, :createdAt)")
    suspend fun insert(
        ingredientName: String,
        riskLevel: String,
        riskReason: String,
        handlingAdvice: String?,
        applicableAgeRangeStart: Int?,
        applicableAgeRangeEnd: Int?,
        severity: Int,
        dataSource: String,
        createdAt: Long
    ): Long
}