package com.example.babyfood.data.sync

import android.util.Log
import android.widget.Toast
import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.remote.RemoteDataSource
import com.example.babyfood.data.remote.dto.CloudBaby
import com.example.babyfood.data.remote.dto.CloudPlan
import com.example.babyfood.data.remote.dto.CloudRecipe
import com.example.babyfood.data.remote.dto.SyncPushRequest
import com.example.babyfood.data.remote.dto.SyncPushResponse
import com.example.babyfood.data.remote.mapper.BabyMapper
import com.example.babyfood.data.remote.mapper.PlanMapper
import com.example.babyfood.data.remote.mapper.RecipeMapper
import com.example.babyfood.data.repository.IdMappingRepository
import com.example.babyfood.domain.model.ConflictInfo
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.EntityType
import com.example.babyfood.domain.model.SyncError
import com.example.babyfood.domain.model.SyncResult
import com.example.babyfood.domain.model.SyncState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步状态（内部使用）
 */
sealed class InternalSyncState {
    object Idle : InternalSyncState()
    data class Syncing(val progress: Int) : InternalSyncState()
    data class Success(val message: String) : InternalSyncState()
    data class Error(val message: String, val error: Throwable?) : InternalSyncState()
}

/**
 * 同步管理器
 * 负责协调本地和云端数据同步
 * 
 * 功能：
 * 1. 云端 ID 与本地 ID 映射
 * 2. Last-Write-Wins 冲突解决
 * 3. 实时同步触发（500ms 防抖）
 * 4. 离线队列管理
 */
@Singleton
class SyncManager @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val babyDao: BabyDao,
    private val planDao: PlanDao,
    private val recipeDao: RecipeDao,
    private val idMappingRepository: IdMappingRepository,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_DEBOUNCE_MS = 500L
    }

    private val _syncState = MutableStateFlow<InternalSyncState>(InternalSyncState.Idle)
    val syncState: StateFlow<InternalSyncState> = _syncState.asStateFlow()

    private val syncScope = CoroutineScope(Dispatchers.IO)
    private var syncJob: Job? = null

    // ==================== ID 映射 ====================

    /**
     * 根据云端 ID 获取本地 ID
     */
    suspend fun getLocalId(entityType: EntityType, cloudId: String): Long? {
        Log.d(TAG, "========== getLocalId 开始 ==========")
        Log.d(TAG, "entityType: $entityType, cloudId: $cloudId")
        val localId = idMappingRepository.getLocalId(entityType, cloudId)
        Log.d(TAG, "本地 ID: $localId")
        Log.d(TAG, "========== getLocalId 结束 ==========")
        return localId
    }

    /**
     * 根据本地 ID 获取云端 ID
     */
    suspend fun getCloudId(entityType: EntityType, localId: Long): String? {
        Log.d(TAG, "========== getCloudId 开始 ==========")
        Log.d(TAG, "entityType: $entityType, localId: $localId")
        val cloudId = idMappingRepository.getCloudId(entityType, localId)
        Log.d(TAG, "云端 ID: $cloudId")
        Log.d(TAG, "========== getCloudId 结束 ==========")
        return cloudId
    }

    /**
     * 保存 ID 映射
     */
    suspend fun saveIdMapping(entityType: EntityType, localId: Long, cloudId: String) {
        Log.d(TAG, "========== saveIdMapping 开始 ==========")
        Log.d(TAG, "entityType: $entityType, localId: $localId, cloudId: $cloudId")
        idMappingRepository.upsert(entityType, localId, cloudId)
        Log.d(TAG, "✓ ID 映射保存成功")
        Log.d(TAG, "========== saveIdMapping 结束 ==========")
    }

    // ==================== 冲突解决 ====================

    /**
     * 解决冲突
     * 使用 Last-Write-Wins 策略：比较 updatedAt 时间戳，选择较新的版本
     * 
     * @param local 本地实体
     * @param cloud 云端实体
     * @return 解决结果
     */
    suspend fun resolveConflict(
        local: SyncableEntityInfo,
        cloud: SyncableEntityInfo
    ): ConflictResolution {
        Log.d(TAG, "========== resolveConflict 开始 ==========")
        Log.d(TAG, "本地版本: ${local.version}, 更新时间: ${local.updatedAt}")
        Log.d(TAG, "云端版本: ${cloud.version}, 更新时间: ${cloud.updatedAt}")

        // Last-Write-Wins: 比较 updatedAt 时间戳
        val resolution = if (local.updatedAt >= cloud.updatedAt) {
            Log.d(TAG, "✓ 选择本地版本（更新时间较新或相同）")
            ConflictResolution.LOCAL_WINS
        } else {
            Log.d(TAG, "✓ 选择云端版本（更新时间较新）")
            ConflictResolution.CLOUD_WINS
        }

        Log.d(TAG, "========== resolveConflict 结束 ==========")
        return resolution
    }

    /**
     * 批量解决冲突
     */
    suspend fun resolveConflicts(conflicts: List<ConflictInfo>): List<ConflictInfo> {
        Log.d(TAG, "========== resolveConflicts 开始 ==========")
        Log.d(TAG, "冲突数量: ${conflicts.size}")

        val resolved = conflicts.map { conflict ->
            // 自动使用 Last-Write-Wins 解决
            val resolution = if (conflict.localVersion > conflict.cloudVersion) {
                ConflictResolution.LOCAL_WINS
            } else {
                ConflictResolution.CLOUD_WINS
            }
            conflict.copy(
                resolution = resolution,
                resolvedAt = Clock.System.now()
            )
        }

        Log.d(TAG, "✓ 冲突解决完成")
        Log.d(TAG, "========== resolveConflicts 结束 ==========")
        return resolved
    }

    // ==================== 同步时间管理 ====================

    /**
     * 获取上次同步时间
     */
    fun getLastSyncTime(): Long? {
        return preferencesManager.getLastSyncTime()
    }

    /**
     * 保存最后同步时间
     */
    fun setLastSyncTime(time: Long) {
        preferencesManager.saveLastSyncTime(time)
    }

    // ==================== 完整同步流程 ====================

    /**
     * 执行完整同步（拉取 + 推送）
     */
    suspend fun sync(): SyncResult {
        Log.d(TAG, "========== sync 开始 ==========")
        val startTime = Clock.System.now()
        
        return try {
            _syncState.value = InternalSyncState.Syncing(0)

            var pulledCount = 0
            var pushedCount = 0
            var conflictCount = 0
            val errors = mutableListOf<SyncError>()

            // 1. 拉取云端更新
            _syncState.value = InternalSyncState.Syncing(10)
            Log.d(TAG, "开始拉取云端更新...")
            val pullResult = pull()
            pulledCount = pullResult.first
            errors.addAll(pullResult.second)

            // 2. 推送本地更新
            _syncState.value = InternalSyncState.Syncing(70)
            Log.d(TAG, "开始推送本地更新...")
            val pushResult = push()
            pushedCount = pushResult.pushedCount
            conflictCount = pushResult.conflicts.size
            errors.addAll(pushResult.errors)

            // 3. 处理冲突
            if (conflictCount > 0) {
                _syncState.value = InternalSyncState.Syncing(90)
                Log.d(TAG, "处理冲突: $conflictCount 个")
                val resolved = resolveConflicts(pushResult.conflicts)
                // 通知用户冲突已自动解决
                // Toast 会在 UI 层显示
            }

            // 更新最后同步时间
            val now = Clock.System.now().toEpochMilliseconds()
            setLastSyncTime(now)

            val duration = Clock.System.now() - startTime
            _syncState.value = InternalSyncState.Success("同步完成")
            
            Log.d(TAG, "✓ 同步完成: 拉取 $pulledCount, 推送 $pushedCount, 冲突 $conflictCount")
            Log.d(TAG, "========== sync 结束 ==========")

            SyncResult(
                success = true,
                pulledCount = pulledCount,
                pushedCount = pushedCount,
                conflictCount = conflictCount,
                errors = errors,
                duration = duration
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ 同步失败: ${e.message}", e)
            _syncState.value = InternalSyncState.Error("同步失败", e)
            Log.d(TAG, "========== sync 结束（失败） ==========")
            
            SyncResult(
                success = false,
                errors = listOf(SyncError(
                    entityType = null,
                    entityId = null,
                    message = e.message ?: "未知错误",
                    throwable = e
                )),
                duration = Clock.System.now() - startTime
            )
        }
    }

    /**
     * 拉取云端更新
     * @return Pair<拉取数量, 错误列表>
     */
    private suspend fun pull(): Pair<Int, List<SyncError>> {
        Log.d(TAG, "========== pull 开始 ==========")
        var count = 0
        val errors = mutableListOf<SyncError>()

        try {
            // 获取上次同步时间
            val lastSyncTime = getLastSyncTime()
            Log.d(TAG, "上次同步时间: $lastSyncTime")

            // 调用远程数据源拉取更新
            val response = remoteDataSource.pull(lastSyncTime)
            if (response == null) {
                Log.w(TAG, "拉取失败：无响应")
                return Pair(0, listOf(SyncError(null, null, "无响应")))
            }

            // 处理食谱更新
            Log.d(TAG, "处理食谱更新: ${response.recipes.size} 个")
            processRecipeUpdates(response.recipes)
            count += response.recipes.size

            // 处理计划更新
            Log.d(TAG, "处理计划更新: ${response.plans.size} 个")
            processPlanUpdates(response.plans)
            count += response.plans.size

            // 处理宝宝更新
            Log.d(TAG, "处理宝宝更新: ${response.babies.size} 个")
            processBabyUpdates(response.babies)
            count += response.babies.size

            Log.d(TAG, "✓ 拉取完成: $count 个更新")

        } catch (e: Exception) {
            Log.e(TAG, "❌ 拉取失败: ${e.message}", e)
            errors.add(SyncError(null, null, "拉取失败: ${e.message}", e))
        }

        Log.d(TAG, "========== pull 结束 ==========")
        return Pair(count, errors)
    }

    /**
     * 推送本地更新
     */
    private suspend fun push(): PushResult {
        Log.d(TAG, "========== push 开始 ==========")
        
        try {
            // 获取待推送的数据
            val recipes = getPendingUploadRecipes()
            val plans = getPendingUploadPlans()
            val babies = getPendingUploadBabies()

            Log.d(TAG, "待推送: 食谱 ${recipes.size}, 计划 ${plans.size}, 宝宝 ${babies.size}")

            if (recipes.isEmpty() && plans.isEmpty() && babies.isEmpty()) {
                Log.d(TAG, "无待推送数据")
                return PushResult(0, emptyList(), emptyList())
            }

            // 构建推送请求
            val request = SyncPushRequest(
                recipes = recipes.map { RecipeMapper.toCloud(it) },
                plans = plans.map { PlanMapper.toCloud(it) },
                babies = babies.map { BabyMapper.toCloud(it) }
            )

            // 调用远程数据源推送更新
            val response = remoteDataSource.push(request)
            if (response == null) {
                Log.w(TAG, "推送失败：无响应")
                return PushResult(0, emptyList(), listOf(SyncError(null, null, "无响应")))
            }

            // 处理冲突
            val conflicts = response.conflicts.map { conflict ->
                ConflictInfo(
                    entityType = EntityType.valueOf(conflict.entityType.uppercase()),
                    localId = 0L, // 需要从映射中查找
                    cloudId = conflict.cloudId,
                    localVersion = conflict.localVersion,
                    cloudVersion = conflict.remoteVersion,
                    conflictReason = conflict.reason
                )
            }

            // 标记已同步
            markAsSynced(recipes, plans, babies)

            val pushedCount = recipes.size + plans.size + babies.size
            Log.d(TAG, "✓ 推送完成: $pushedCount 个更新")

            return PushResult(pushedCount, conflicts, emptyList())

        } catch (e: Exception) {
            Log.e(TAG, "❌ 推送失败: ${e.message}", e)
            return PushResult(0, emptyList(), listOf(SyncError(null, null, "推送失败: ${e.message}", e)))
        }
    }

    // ==================== 实时同步触发 ====================

    /**
     * 触发同步（带防抖）
     * 在数据变更时调用，500ms 内多次调用只执行一次
     */
    @OptIn(FlowPreview::class)
    fun triggerSync() {
        Log.d(TAG, "========== triggerSync 触发 ==========")
        
        // 取消之前的同步任务
        syncJob?.cancel()

        // 延迟执行同步
        syncJob = syncScope.launch {
            delay(SYNC_DEBOUNCE_MS)
            Log.d(TAG, "防抖后开始同步...")
            sync()
        }
    }

    /**
     * 观察数据变更并自动触发同步
     */
    @OptIn(FlowPreview::class)
    fun observeDataChanges() {
        Log.d(TAG, "========== 开始观察数据变更 ==========")
        
        // 观察宝宝数据变更
        babyDao.getAllBabies()
            .debounce(SYNC_DEBOUNCE_MS)
            .onEach {
                Log.d(TAG, "检测到宝宝数据变更")
                triggerSync()
            }
            .launchIn(syncScope)

        // 观察计划数据变更
        planDao.getAllPlansFlow()
            .debounce(SYNC_DEBOUNCE_MS)
            .onEach {
                Log.d(TAG, "检测到计划数据变更")
                triggerSync()
            }
            .launchIn(syncScope)

        // 观察食谱数据变更
        recipeDao.getAllRecipes()
            .debounce(SYNC_DEBOUNCE_MS)
            .onEach {
                Log.d(TAG, "检测到食谱数据变更")
                triggerSync()
            }
            .launchIn(syncScope)
    }

    // ==================== 私有辅助方法 ====================

    private suspend fun processRecipeUpdates(recipes: List<CloudRecipe>) {
        for (recipe in recipes) {
            try {
                val existing = recipeDao.getAllRecipesSync().firstOrNull { it.cloudId == recipe.cloudId }

                if (existing != null) {
                    // 更新现有食谱
                    val updated = RecipeMapper.toEntity(recipe).copy(id = existing.id)
                    recipeDao.update(updated)
                } else {
                    // 插入新食谱
                    val id = recipeDao.insert(RecipeMapper.toEntity(recipe))
                    // 保存 ID 映射
                    recipe.cloudId?.let { cloudId ->
                        saveIdMapping(EntityType.RECIPE, id, cloudId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理食谱更新失败: ${recipe.cloudId}", e)
            }
        }
    }

    private suspend fun processPlanUpdates(plans: List<CloudPlan>) {
        for (plan in plans) {
            try {
                val existing = planDao.getAllPlansSync().firstOrNull { it.cloudId == plan.cloudId }

                // 映射云端 babyId 到本地 ID
                val localBabyId = plan.cloudBabyId?.let { cloudBabyId ->
                    getLocalId(EntityType.BABY, cloudBabyId)
                }

                // 映射云端 recipeId 到本地 ID
                val localRecipeId: Long? = getLocalId(EntityType.RECIPE, plan.cloudRecipeId)

                if (existing != null) {
                    // 更新现有计划
                    val baseEntity = PlanMapper.toEntity(plan)
                    val updated = baseEntity.copy(
                        id = existing.id,
                        babyId = localBabyId ?: existing.babyId,
                        recipeId = localRecipeId ?: existing.recipeId
                    )
                    planDao.update(updated)
                } else {
                    // 插入新计划
                    if (localBabyId != null && localRecipeId != null) {
                        val baseEntity = PlanMapper.toEntity(plan)
                        val id = planDao.insert(baseEntity.copy(
                            babyId = localBabyId,
                            recipeId = localRecipeId
                        ))
                        // 保存 ID 映射
                        saveIdMapping(EntityType.PLAN, id, plan.cloudId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理计划更新失败: ${plan.cloudId}", e)
            }
        }
    }

    private suspend fun processBabyUpdates(babies: List<CloudBaby>) {
        for (baby in babies) {
            try {
                val existing = babyDao.getAllBabiesSync().firstOrNull { it.cloudId == baby.cloudId }

                if (existing != null) {
                    // 更新现有宝宝（合并敏感信息）
                    val updated = BabyMapper.toEntity(baby, existing).copy(id = existing.id)
                    babyDao.update(updated)
                } else {
                    // 云端宝宝数据是脱敏版本，无法创建完整的本地实体
                    // 跳过不存在的宝宝，等待用户在本地创建
                    Log.w(TAG, "跳过云端宝宝（本地不存在）: ${baby.cloudId}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理宝宝更新失败: ${baby.cloudId}", e)
            }
        }
    }

    private suspend fun getPendingUploadRecipes(): List<RecipeEntity> {
        return recipeDao.getAllRecipesSync().filter {
            it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted
        }
    }

    private suspend fun getPendingUploadPlans(): List<PlanEntity> {
        return planDao.getAllPlansSync().filter { 
            it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted 
        }
    }

    private suspend fun getPendingUploadBabies(): List<BabyEntity> {
        return babyDao.getAllBabiesSync().filter { 
            it.syncStatus == "PENDING_UPLOAD" && !it.isDeleted 
        }
    }

    private suspend fun markAsSynced(
        recipes: List<RecipeEntity>,
        plans: List<PlanEntity>,
        babies: List<BabyEntity>
    ) {
        val now = Clock.System.now().toEpochMilliseconds()

        recipes.forEach { recipe ->
            recipeDao.update(recipe.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = recipe.version + 1
            ))
        }

        plans.forEach { plan ->
            planDao.update(plan.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = plan.version + 1
            ))
        }

        babies.forEach { baby ->
            babyDao.update(baby.copy(
                syncStatus = "SYNCED",
                lastSyncTime = now,
                version = baby.version + 1
            ))
        }
    }
}

/**
 * 可同步实体信息
 */
data class SyncableEntityInfo(
    val entityType: EntityType,
    val id: String,
    val version: Int,
    val updatedAt: Long
)

/**
 * 推送结果
 */
data class PushResult(
    val pushedCount: Int,
    val conflicts: List<ConflictInfo>,
    val errors: List<SyncError>
)
