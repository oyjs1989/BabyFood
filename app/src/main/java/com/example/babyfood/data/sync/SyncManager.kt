package com.example.babyfood.data.sync

import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.data.remote.RemoteDataSource
import com.example.babyfood.data.remote.dto.CloudBaby
import com.example.babyfood.data.remote.dto.CloudPlan
import com.example.babyfood.data.remote.dto.CloudRecipe
import com.example.babyfood.data.remote.dto.SyncPullResponse
import com.example.babyfood.data.remote.dto.SyncPushRequest
import com.example.babyfood.data.remote.dto.SyncPushResponse
import com.example.babyfood.data.remote.mapper.BabyMapper
import com.example.babyfood.data.remote.mapper.PlanMapper
import com.example.babyfood.data.remote.mapper.RecipeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步状态
 */
sealed class SyncState {
    object Idle : SyncState()
    data class Syncing(val progress: Int) : SyncState()
    data class Success(val message: String) : SyncState()
    data class Error(val message: String, val error: Throwable?) : SyncState()
}

/**
 * 同步管理器
 * 负责协调本地和云端数据同步
 */
@Singleton
class SyncManager @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val babyDao: BabyDao,
    private val planDao: PlanDao,
    private val recipeDao: RecipeDao
) {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    /**
     * 执行完整同步（拉取 + 推送）
     */
    suspend fun sync(): SyncResult {
        return try {
            _syncState.value = SyncState.Syncing(0)

            // 1. 拉取云端更新
            _syncState.value = SyncState.Syncing(10)
            val pullResult = pull()

            // 2. 推送本地更新
            _syncState.value = SyncState.Syncing(70)
            val pushResult = push()

            // 3. 处理冲突
            _syncState.value = SyncState.Syncing(90)
            handleConflicts(pushResult.conflicts)

            _syncState.value = SyncState.Success("同步完成")
            SyncResult.Success("同步完成")

        } catch (e: Exception) {
            _syncState.value = SyncState.Error("同步失败", e)
            SyncResult.Error(e.message ?: "未知错误", e)
        }
    }

    /**
     * 拉取云端更新
     */
    suspend fun pull(): SyncResult {
        return try {
            // 获取上次同步时间
            val lastSyncTime = getLastSyncTime()

            // 调用远程数据源拉取更新
            val response = remoteDataSource.pull(lastSyncTime)
                ?: return SyncResult.Error("拉取失败：无响应", null)

            // 处理食谱更新
            processRecipeUpdates(response.recipes)

            // 处理计划更新
            processPlanUpdates(response.plans)

            // 处理宝宝更新
            processBabyUpdates(response.babies)

            // 更新最后同步时间
            saveLastSyncTime(response.serverTime)

            SyncResult.Success("拉取完成")

        } catch (e: Exception) {
            SyncResult.Error("拉取失败：${e.message}", e)
        }
    }

    /**
     * 推送本地更新
     */
    suspend fun push(): SyncPushResponse {
        return try {
            // 获取待推送的数据
            val recipes = getPendingUploadRecipes()
            val plans = getPendingUploadPlans()
            val babies = getPendingUploadBabies()

            // 构建推送请求
            val request = SyncPushRequest(
                recipes = recipes.map { RecipeMapper.toCloud(it) },
                plans = plans.map { PlanMapper.toCloud(it) },
                babies = babies.map { BabyMapper.toCloud(it) }
            )

            // 调用远程数据源推送更新
            val response = remoteDataSource.push(request)
                ?: return SyncPushResponse(
                    success = false,
                    conflicts = emptyList(),
                    serverTime = Clock.System.now().toEpochMilliseconds()
                )

            // 标记已同步
            markAsSynced(recipes, plans, babies)

            response

        } catch (e: Exception) {
            SyncPushResponse(
                success = false,
                conflicts = emptyList(),
                serverTime = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    /**
     * 处理食谱更新
     */
    private suspend fun processRecipeUpdates(recipes: List<CloudRecipe>) {
        for (recipe in recipes) {
            val existing = recipeDao.getAllRecipesSync().firstOrNull { it.cloudId == recipe.cloudId }

            if (existing != null) {
                // 更新现有食谱
                val updated = RecipeMapper.toEntity(recipe).copy(id = existing.id)
                recipeDao.updateRecipe(updated)
            } else {
                // 插入新食谱
                recipeDao.insertRecipe(RecipeMapper.toEntity(recipe))
            }
        }
    }

    /**
     * 处理计划更新
     */
    private suspend fun processPlanUpdates(plans: List<CloudPlan>) {
        for (plan in plans) {
            val existing = planDao.getAllPlansSync().firstOrNull { it.cloudId == plan.cloudId }

            if (existing != null) {
                // 更新现有计划
                val updated = PlanMapper.toEntity(plan).copy(id = existing.id)
                planDao.updatePlan(updated)
            } else {
                // 插入新计划（需要映射 babyId 和 recipeId）
                // TODO: 实现本地 ID 映射
            }
        }
    }

    /**
     * 处理宝宝更新
     */
    private suspend fun processBabyUpdates(babies: List<CloudBaby>) {
        for (baby in babies) {
            val existing = babyDao.getAllBabiesSync().firstOrNull { it.cloudId == baby.cloudId }

            if (existing != null) {
                // 更新现有宝宝（合并敏感信息）
                val updated = BabyMapper.toEntity(baby, existing).copy(id = existing.id)
                babyDao.updateBaby(updated)
            } else {
                // 插入新宝宝（需要从本地获取敏感信息）
                // TODO: 实现本地 ID 映射
            }
        }
    }

    /**
     * 处理冲突
     */
    private suspend fun handleConflicts(conflicts: List<com.example.babyfood.data.remote.dto.ConflictInfo>) {
        // TODO: 实现冲突解决策略
        // 1. 版本号比较
        // 2. 时间戳比较
        // 3. 用户选择
    }

    /**
     * 获取待推送的食谱
     */
    private suspend fun getPendingUploadRecipes(): List<RecipeEntity> {
        return recipeDao.getAllRecipesSync().filter { it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted }
    }

    /**
     * 获取待推送的计划
     */
    private suspend fun getPendingUploadPlans(): List<PlanEntity> {
        return planDao.getAllPlansSync().filter { it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted }
    }

    /**
     * 获取待推送的宝宝
     */
    private suspend fun getPendingUploadBabies(): List<BabyEntity> {
        return babyDao.getAllBabiesSync().filter { it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted }
    }

    /**
     * 标记为已同步
     */
    private suspend fun markAsSynced(
        recipes: List<RecipeEntity>,
        plans: List<PlanEntity>,
        babies: List<BabyEntity>
    ) {
        val now = Clock.System.now().toEpochMilliseconds()

        recipes.forEach { recipe ->
            recipeDao.updateRecipe(recipe.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = recipe.version + 1
            ))
        }

        plans.forEach { plan ->
            planDao.updatePlan(plan.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = plan.version + 1
            ))
        }

        babies.forEach { baby ->
            babyDao.updateBaby(baby.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = baby.version + 1
            ))
        }
    }

    /**
     * 获取上次同步时间
     */
    private fun getLastSyncTime(): Long? {
        // TODO: 从 SharedPreferences 获取
        return null
    }

    /**
     * 保存最后同步时间
     */
    private fun saveLastSyncTime(time: Long) {
        // TODO: 保存到 SharedPreferences
    }
}

/**
 * 同步结果
 */
sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Error(val message: String, val error: Throwable?) : SyncResult()
}