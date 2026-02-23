package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    /**
     * 插入用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * 更新用户
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * 根据ID获取用户
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    /**
     * 根据手机号获取用户
     */
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    /**
     * 根据邮箱获取用户
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * 获取当前登录用户
     */
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    /**
     * 设置登录状态
     */
    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE users SET isLoggedIn = 1, lastLoginTime = :loginTime WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long, loginTime: String)

    /**
     * 删除用户
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    /**
     * 获取所有用户
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * 更新用户昵称
     */
    @Query("UPDATE users SET nickname = :nickname WHERE id = :userId")
    suspend fun updateNickname(userId: Long, nickname: String)

    /**
     * 更新用户头像
     */
    @Query("UPDATE users SET avatar = :avatar WHERE id = :userId")
    suspend fun updateAvatar(userId: Long, avatar: String)

    /**
     * 更新用户主题设置
     */
    @Query("UPDATE users SET theme = :theme WHERE id = :userId")
    suspend fun updateTheme(userId: Long, theme: String)
}