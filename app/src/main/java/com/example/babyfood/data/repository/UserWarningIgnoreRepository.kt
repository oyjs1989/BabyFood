package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.UserWarningIgnoreDao
import com.example.babyfood.data.local.database.entity.UserWarningIgnoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户警告忽略记录仓库
 *
 * 负责记录和管理用户忽略安全警告的行为
 */
@Singleton
class UserWarningIgnoreRepository @Inject constructor(
    private val userWarningIgnoreDao: UserWarningIgnoreDao
) {
    companion object {
        private const val TAG = "UserWarningIgnoreRepository"
    }

    /**
     * 获取用户忽略特定警告的次数
     */
    suspend fun getIgnoreCount(
        userId: Long,
        warningType: String,
        ingredientName: String
    ): Int {
        android.util.Log.d(TAG, "========== 开始查询忽略次数 ==========")
        android.util.Log.d(TAG, "用户ID: $userId, 警告类型: $warningType, 食材: $ingredientName")

        val count = userWarningIgnoreDao.getIgnoreCount(userId, warningType, ingredientName)

        android.util.Log.d(TAG, "✓ 查询成功，忽略次数: $count")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return count
    }

    /**
     * 记录用户忽略警告的行为
     */
    suspend fun recordIgnore(
        userId: Long,
        warningType: String,
        ingredientName: String
    ) {
        android.util.Log.d(TAG, "========== 开始记录忽略警告 ==========")
        android.util.Log.d(TAG, "用户ID: $userId, 警告类型: $warningType, 食材: $ingredientName")

        // 查询是否已有记录
        val existing = userWarningIgnoreDao.getByUserAndWarningTypeAndIngredient(
            userId = userId,
            warningType = warningType,
            ingredientName = ingredientName
        )

        if (existing != null) {
            // 更新忽略次数
            val updated = existing.copy(
                ignoreCount = existing.ignoreCount + 1,
                ignoreDate = System.currentTimeMillis()
            )
            userWarningIgnoreDao.insert(updated)
            android.util.Log.d(TAG, "✓ 更新成功，新的忽略次数: ${updated.ignoreCount}")
        } else {
            // 创建新记录
            val new = UserWarningIgnoreEntity(
                userId = userId,
                ingredientName = ingredientName,
                warningType = warningType,
                ignoreDate = System.currentTimeMillis(),
                ignoreCount = 1
            )
            userWarningIgnoreDao.insert(new)
            android.util.Log.d(TAG, "✓ 创建成功，忽略次数: 1")
        }

        android.util.Log.d(TAG, "========== 记录完成 ==========")
    }

    /**
     * 清除用户的所有忽略记录
     */
    suspend fun clearAllIgnores(userId: Long) {
        android.util.Log.d(TAG, "========== 开始清除忽略记录 ==========")
        android.util.Log.d(TAG, "用户ID: $userId")

        userWarningIgnoreDao.deleteByUserId(userId)

        android.util.Log.d(TAG, "✓ 清除成功")
        android.util.Log.d(TAG, "========== 清除完成 ==========")
    }

    /**
     * 获取用户在指定时间范围内的忽略记录
     */
    suspend fun getIgnoresInDateRange(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<UserWarningIgnoreEntity> {
        android.util.Log.d(TAG, "========== 开始查询指定日期范围的忽略记录 ==========")
        android.util.Log.d(TAG, "用户ID: $userId, 日期范围: $startDate - $endDate")

        // 注意：DAO 中需要添加此查询方法，这里暂时返回空列表
        val results = emptyList<UserWarningIgnoreEntity>()

        android.util.Log.d(TAG, "✓ 查询成功，找到 ${results.size} 条记录")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return results
    }
}