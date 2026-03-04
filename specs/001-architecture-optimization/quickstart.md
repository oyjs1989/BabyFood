# Quickstart: 架构优化与安全增强

**Feature**: 001-architecture-optimization | **Date**: 2026-03-04

## 概述

本功能解决 BabyFood Android 项目的四个核心架构问题。按优先级实施：

1. **P1**: 移除硬编码 API Key（安全问题）
2. **P2**: 完善 SyncManager 同步功能
3. **P3**: 统一 Entity 映射代码位置
4. **P3**: 简化 BaseUiViewModel 抽象设计

## 前置条件

- JDK 17+
- Android Studio (推荐 Hedgehog 或更新版本)
- 已连接的 Android 设备或模拟器 (API 24+)
- 后端服务已部署（或本地运行）

## 快速开始

### 1. 切换到功能分支

```bash
git checkout 001-architecture-optimization
```

### 2. Phase 1: 安全问题修复 (P1)

**目标**: 移除硬编码 API Key

**步骤**:

```kotlin
// 1. 创建后端代理策略
// 文件: data/ai/BackendAiProxyStrategy.kt
class BackendAiProxyStrategy @Inject constructor(
    private val aiProxyApiService: AiProxyApiService
) : HealthAnalysisStrategy {
    override suspend fun analyze(record: HealthRecord, baby: Baby): String? {
        return try {
            val response = aiProxyApiService.analyzeHealth(
                HealthAnalysisRequest(baby.id, record)
            )
            if (response.success) response.analysis else null
        } catch (e: Exception) {
            Log.e("BackendAiProxyStrategy", "AI 服务不可用", e)
            null
        }
    }
}

// 2. 更新 StrategyManager 使用新策略
// 文件: data/strategy/StrategyManager.kt
@Provides
@Singleton
fun provideHealthAnalysisStrategy(
    backendAiProxyStrategy: BackendAiProxyStrategy
): HealthAnalysisStrategy = backendAiProxyStrategy

// 3. 移除 RemoteHealthAnalysisStrategy 中的硬编码 Key
// 删除: return "sk-aa30af1a40a643cbb8f43881c1ebbb49"
```

**验证**:
```bash
# 搜索硬编码 API Key
rg "sk-[a-f0-9]{32}" app/src/main/java/
# 应返回空结果
```

### 3. Phase 2: 同步功能完善 (P2)

**目标**: 完成 SyncManager TODO

**步骤**:

```kotlin
// 1. 创建 IdMappingEntity
@Entity(tableName = "id_mappings")
data class IdMappingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,
    val localId: Long,
    val cloudId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 2. 添加数据库迁移
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS id_mappings (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                entity_type TEXT NOT NULL,
                local_id INTEGER NOT NULL,
                cloud_id TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)
    }
}

// 3. 实现 SyncManager 中的 TODO
class SyncManager @Inject constructor(
    private val idMappingRepository: IdMappingRepository,
    // ...
) {
    // 实现 ID 映射
    suspend fun getLocalId(entityType: String, cloudId: String): Long? =
        idMappingRepository.getLocalId(entityType, cloudId)
    
    // 实现冲突解决 (Last-Write-Wins)
    private fun resolveConflict(local: SyncableEntity, cloud: SyncableEntity): SyncableEntity {
        return if (local.updatedAt > cloud.updatedAt) local else cloud
    }
}
```

**验证**:
```bash
# 运行同步测试
./gradlew test --tests "*SyncManager*"
```

### 4. Phase 3: 代码优化 (P3)

**目标**: 统一 Entity 映射代码

**步骤**:

```kotlin
// 1. 在 Entity 文件中添加映射函数
// 文件: data/local/database/entity/BabyEntity.kt
@Entity(tableName = "babies")
data class BabyEntity(...) {
    fun toDomainModel(): Baby = Baby(...)
}

fun Baby.toEntity(): BabyEntity = BabyEntity(...)

// 2. 移除 Repository 中的重复映射
// 删除 BabyRepository 中的:
// override fun BabyEntity.toDomainModel(): Baby = ...
// override fun Baby.toEntity(): BabyEntity = ...
```

**验证**:
```bash
# 检查映射函数位置
rg "fun toDomainModel" app/src/main/java/com/example/babyfood/data/local/database/entity/
# 应只在 Entity 文件中找到
```

### 5. Phase 4: 架构改进 (P3)

**目标**: 简化 BaseUiViewModel

**步骤**:

```kotlin
// 1. 修改 BaseUiViewModel
abstract class BaseUiViewModel<T : BaseUiState> : BaseViewModel() {
    // 合并为单一抽象方法
    protected abstract fun T.copyState(isLoading: Boolean, error: String?): T
    
    // 保留旧方法并标记弃用
    @Deprecated("Use copyState instead", ReplaceWith("copyState(isLoading, error)"))
    protected fun T.copyWithLoading(isLoading: Boolean): T = 
        copyState(isLoading, this.error)
    
    @Deprecated("Use copyState instead", ReplaceWith("copyState(this.isLoading, error)"))
    protected fun T.copyWithError(error: String?): T = 
        copyState(this.isLoading, error)
}

// 2. 更新现有 ViewModel（渐进式）
// 旧代码:
override fun BabyUiState.copyWithLoading(isLoading: Boolean) = copy(isLoading = isLoading)
override fun BabyUiState.copyWithError(error: String?) = copy(error = error)
override fun BabyUiState.copyWithErrorAndLoading(error: String?, isLoading: Boolean) = 
    copy(error = error, isLoading = isLoading)

// 新代码:
override fun BabyUiState.copyState(isLoading: Boolean, error: String?) = 
    copy(isLoading = isLoading, error = error)
```

**验证**:
```bash
# 检查弃用警告
./gradlew build 2>&1 | grep "Deprecated"
```

## 构建与测试

### 构建项目

```bash
# Windows
gradlew.bat assembleDebug

# Unix/Linux/macOS
./gradlew assembleDebug
```

### 运行测试

```bash
# 单元测试
gradlew.bat test

# Android 设备测试
gradlew.bat connectedAndroidTest
```

### 安装到设备

```bash
gradlew.bat installDebug
```

## 常见问题

### Q: 后端代理服务不可用怎么办？

A: 应用会显示错误提示并禁用 AI 功能。确保后端服务已部署或本地运行。

### Q: 数据库迁移失败怎么办？

A: 卸载应用重新安装，或使用 `gradlew.bat uninstallDebug` 清除旧数据。

### Q: 同步冲突如何处理？

A: 当前使用 Last-Write-Wins 策略自动解决，并通过 Toast 通知用户。

## 相关文档

- [规格文档](./spec.md)
- [实施计划](./plan.md)
- [研究文档](./research.md)
- [数据模型](./data-model.md)
- [API 合约](./contracts/openapi.yaml)

## 下一步

完成实施后，执行 `/speckit.tasks` 生成详细任务列表。
