package com.example.babyfood.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.User

/**
 * 用户数据库实体
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
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
    val theme: String? = "light", // 主题设置：light/dark/auto
    val role: String = "USER",
    
    @ColumnInfo(name = "points_balance")
    val pointsBalance: Int = 0,
    
    @ColumnInfo(name = "last_check_in_date")
    val lastCheckInDate: Long? = null
) {
    fun toDomainModel(): User = User(
        id = id,
        username = phone ?: email ?: "",
        phone = phone,
        email = email,
        nickname = nickname,
        avatar = avatar,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L,
        theme = theme ?: "light",
        role = role,
        pointsBalance = pointsBalance,
        lastCheckInDate = lastCheckInDate
    )
}

/**
 * Domain Model 转 Entity
 */
fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    phone = phone,
    email = email,
    nickname = nickname,
    avatar = avatar,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
    isEmailVerified = isEmailVerified,
    isPhoneVerified = isPhoneVerified,
    isLoggedIn = true,
    lastLoginTime = null,
    theme = theme,
    role = role,
    pointsBalance = pointsBalance,
    lastCheckInDate = lastCheckInDate
)
