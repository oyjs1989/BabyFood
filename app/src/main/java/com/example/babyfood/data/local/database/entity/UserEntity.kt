package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户数据库实体
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phone: String? = null,
    val email: String? = null,
    val nickname: String = "",
    val avatar: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val isLoggedIn: Boolean = false, // 当前登录状态
    val lastLoginTime: String? = null, // 最后登录时间
    val theme: String? = "light" // 主题设置：light/dark/auto
)