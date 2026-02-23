package com.example.babyfood.data.remote

import com.example.babyfood.data.remote.dto.CloudBaby
import com.example.babyfood.data.remote.dto.CloudPlan
import com.example.babyfood.data.remote.dto.CloudRecipe
import com.example.babyfood.data.remote.dto.SyncPullResponse
import com.example.babyfood.data.remote.dto.SyncPushRequest
import com.example.babyfood.data.remote.dto.SyncPushResponse

/**
 * 远程数据源接口
 * 定义与云端服务器的交互操作
 */
interface RemoteDataSource {

    // ==================== Recipe 操作 ====================

    /**
     * 获取所有食谱
     */
    suspend fun getAllRecipes(): List<CloudRecipe>

    /**
     * 获取单个食谱
     */
    suspend fun getRecipe(cloudId: String): CloudRecipe?

    /**
     * 创建食谱
     */
    suspend fun createRecipe(recipe: CloudRecipe): CloudRecipe?

    /**
     * 更新食谱
     */
    suspend fun updateRecipe(cloudId: String, recipe: CloudRecipe): CloudRecipe?

    /**
     * 删除食谱
     */
    suspend fun deleteRecipe(cloudId: String): Boolean

    // ==================== Plan 操作 ====================

    /**
     * 获取所有计划
     */
    suspend fun getAllPlans(cloudBabyId: String? = null): List<CloudPlan>

    /**
     * 获取单个计划
     */
    suspend fun getPlan(cloudId: String): CloudPlan?

    /**
     * 创建计划
     */
    suspend fun createPlan(plan: CloudPlan): CloudPlan?

    /**
     * 更新计划
     */
    suspend fun updatePlan(cloudId: String, plan: CloudPlan): CloudPlan?

    /**
     * 删除计划
     */
    suspend fun deletePlan(cloudId: String): Boolean

    // ==================== Baby 操作 ====================

    /**
     * 获取所有宝宝
     */
    suspend fun getAllBabies(): List<CloudBaby>

    /**
     * 获取单个宝宝
     */
    suspend fun getBaby(cloudId: String): CloudBaby?

    /**
     * 创建宝宝
     */
    suspend fun createBaby(baby: CloudBaby): CloudBaby?

    /**
     * 更新宝宝
     */
    suspend fun updateBaby(cloudId: String, baby: CloudBaby): CloudBaby?

    /**
     * 删除宝宝
     */
    suspend fun deleteBaby(cloudId: String): Boolean

    // ==================== Sync 操作 ====================

    /**
     * 拉取更新
     */
    suspend fun pull(lastSyncTime: Long?): SyncPullResponse?

    /**
     * 推送更新
     */
    suspend fun push(request: SyncPushRequest): SyncPushResponse?
}