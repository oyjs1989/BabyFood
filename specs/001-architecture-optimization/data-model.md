# Data Model: 架构优化与安全增强

**Feature**: 001-architecture-optimization | **Date**: 2026-03-04

## 新增实体

### 1. IdMappingEntity

**用途**: 存储云端 ID 与本地 ID 的映射关系

**表名**: `id_mappings`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | Long | PRIMARY KEY AUTOINCREMENT | 主键 |
| `entity_type` | String | NOT NULL, INDEX | 实体类型（Baby, Plan, Recipe 等） |
| `local_id` | Long | NOT NULL, INDEX | 本地数据库 ID |
| `cloud_id` | String | NOT NULL, UNIQUE | 云端 UUID |
| `created_at` | Long | NOT NULL | 创建时间戳 |
| `updated_at` | Long | NOT NULL | 更新时间戳 |

**索引**:
- `idx_id_mappings_entity_type_local_id` ON `(entity_type, local_id)`
- `idx_id_mappings_entity_type_cloud_id` ON `(entity_type, cloud_id)`

**Room Entity 定义**:
```kotlin
@Entity(
    tableName = "id_mappings",
    indices = [
        Index(value = ["entity_type", "local_id"], unique = true),
        Index(value = ["entity_type", "cloud_id"], unique = true)
    ]
)
data class IdMappingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityType: String,
    val localId: Long,
    val cloudId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

**映射扩展函数**:
```kotlin
// IdMappingEntity.kt
fun IdMappingEntity.toDomainModel(): IdMapping = IdMapping(
    id = id,
    entityType = EntityType.valueOf(entityType),
    localId = localId,
    cloudId = cloudId,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt)
)

fun IdMapping.toEntity(): IdMappingEntity = IdMappingEntity(
    id = id,
    entityType = entityType.name,
    localId = localId,
    cloudId = cloudId,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds()
)
```

---

### 2. SyncState (枚举)

**用途**: 同步状态枚举

```kotlin
enum class SyncState {
    SYNCED,           // 已同步
    PENDING_UPLOAD,   // 待上传
    PENDING_DOWNLOAD, // 待下载
    SYNCING,          // 同步中
    ERROR,            // 同步错误
    OFFLINE           // 离线
}
```

---

## 修改实体

### 1. SyncMetadata 更新

**现有字段** (SyncableEntity 接口):
- `cloudId: String?`
- `syncStatus: String`
- `lastSyncTime: Long?`
- `version: Int`
- `isDeleted: Boolean`

**新增方法**:
```kotlin
interface SyncableEntity {
    // ... 现有字段 ...
    
    /**
     * 准备同步更新
     */
    fun prepareForSyncUpdate(cloudId: String, lastSyncTime: Long): SyncableEntity
    
    /**
     * 标记为待上传
     */
    fun markForUpload(): SyncableEntity
}
```

---

## Domain Model

### IdMapping

```kotlin
data class IdMapping(
    val id: Long = 0,
    val entityType: EntityType,
    val localId: Long,
    val cloudId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class EntityType {
    BABY,
    PLAN,
    RECIPE,
    INVENTORY_ITEM
}
```

### ConflictInfo

```kotlin
data class ConflictInfo(
    val entityType: EntityType,
    val localId: Long,
    val cloudId: String,
    val localVersion: Int,
    val cloudVersion: Int,
    val conflictReason: String,
    val resolvedAt: Instant? = null,
    val resolution: ConflictResolution? = null
)

enum class ConflictResolution {
    LOCAL_WINS,
    CLOUD_WINS,
    MANUAL
}
```

### SyncResult

```kotlin
data class SyncResult(
    val success: Boolean,
    val pulledCount: Int = 0,
    val pushedCount: Int = 0,
    val conflictCount: Int = 0,
    val errors: List<SyncError> = emptyList(),
    val duration: Duration
)

data class SyncError(
    val entityType: EntityType?,
    val entityId: String?,
    val message: String,
    val throwable: Throwable? = null
)
```

---

## DAO 接口

### IdMappingDao

```kotlin
@Dao
interface IdMappingDao {
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType AND local_id = :localId")
    suspend fun getByLocalId(entityType: String, localId: Long): IdMappingEntity?
    
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType AND cloud_id = :cloudId")
    suspend fun getByCloudId(entityType: String, cloudId: String): IdMappingEntity?
    
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType")
    fun getByEntityType(entityType: String): Flow<List<IdMappingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapping: IdMappingEntity): Long
    
    @Update
    suspend fun update(mapping: IdMappingEntity)
    
    @Delete
    suspend fun delete(mapping: IdMappingEntity)
    
    @Query("DELETE FROM id_mappings WHERE entity_type = :entityType AND local_id = :localId")
    suspend fun deleteByLocalId(entityType: String, localId: Long)
    
    @Transaction
    suspend fun upsert(entityType: String, localId: Long, cloudId: String) {
        val existing = getByLocalId(entityType, localId)
        if (existing != null) {
            update(existing.copy(cloudId = cloudId, updatedAt = System.currentTimeMillis()))
        } else {
            insert(IdMappingEntity(
                entityType = entityType,
                localId = localId,
                cloudId = cloudId
            ))
        }
    }
}
```

---

## 数据库迁移

### MIGRATION_16_17

```kotlin
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
        """.trimIndent())
        
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS idx_id_mappings_entity_type_local_id
            ON id_mappings (entity_type, local_id)
        """.trimIndent())
        
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS idx_id_mappings_entity_type_cloud_id
            ON id_mappings (entity_type, cloud_id)
        """.trimIndent())
    }
}
```

---

## 实体关系图

```
┌─────────────────┐     ┌─────────────────┐
│   BabyEntity    │     │ IdMappingEntity │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ local_id (FK)   │
│ cloudId         │     │ cloud_id        │
│ name            │     │ entity_type     │
│ ...             │     │ = "BABY"        │
└─────────────────┘     └─────────────────┘

┌─────────────────┐     ┌─────────────────┐
│   PlanEntity    │     │ IdMappingEntity │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ local_id (FK)   │
│ cloudId         │     │ cloud_id        │
│ babyId          │     │ entity_type     │
│ ...             │     │ = "PLAN"        │
└─────────────────┘     └─────────────────┘

┌─────────────────┐     ┌─────────────────┐
│  RecipeEntity   │     │ IdMappingEntity │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ local_id (FK)   │
│ cloudId         │     │ cloud_id        │
│ name            │     │ entity_type     │
│ ...             │     │ = "RECIPE"      │
└─────────────────┘     └─────────────────┘
```

---

## 验证规则

| 实体 | 字段 | 规则 |
|------|------|------|
| IdMapping | entityType | 非空，必须是 EntityType 枚举值 |
| IdMapping | localId | 非空，必须 > 0 |
| IdMapping | cloudId | 非空，必须是有效 UUID |
| IdMapping | (entityType, localId) | 组合唯一 |
| IdMapping | (entityType, cloudId) | 组合唯一 |
