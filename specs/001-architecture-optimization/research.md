# Research: 架构优化与安全增强

**Feature**: 001-architecture-optimization | **Date**: 2026-03-04

## 1. API Key 安全存储方案

### Decision: 后端代理方案

**Rationale**: 
- 后端代理是最安全的方案，API Key 完全不暴露在客户端
- 符合 OWASP 移动应用安全最佳实践
- 后端已有 `ImageAnalysisApiService` 代理接口可复用

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| Android Keystore | 系统级加密存储 | Key 仍存在客户端，可被逆向 | ❌ 不推荐 |
| NDK 硬编码 | 增加逆向难度 | 仍可被提取，非真正安全 | ❌ 不推荐 |
| EncryptedSharedPreferences | 加密存储 | Key 仍存在客户端 | ❌ 不推荐 |
| **后端代理** | Key 完全不在客户端 | 需要网络连接 | ✅ 推荐 |

**Implementation Notes**:
- 后端已有 `/api/v1/images/analyze` 代理接口
- 需扩展支持健康分析请求转发
- 添加错误处理和降级机制

---

## 2. 云端/本地 ID 映射方案

### Decision: Room 数据库表存储

**Rationale**:
- 项目已使用 Room 数据库，保持一致性
- 支持高效查询和索引
- 数据持久化，应用重启后映射关系保留

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| SharedPreferences JSON | 简单实现 | 查询效率低，无索引 | ❌ 不推荐 |
| 内存缓存 | 查询最快 | 应用重启丢失 | ❌ 不推荐 |
| **Room 数据库表** | 高效查询，持久化 | 需要数据库迁移 | ✅ 推荐 |

**Implementation Notes**:
- 新建 `id_mappings` 表
- 字段：`id` (主键), `entity_type` (实体类型), `local_id` (本地ID), `cloud_id` (云端ID)
- 添加复合索引：`(entity_type, local_id)` 和 `(entity_type, cloud_id)`

---

## 3. 冲突解决策略

### Decision: Last-Write-Wins (LWW)

**Rationale**:
- 实现简单，性能开销低
- 适合辅食计划场景（用户通常在一个设备上操作）
- 符合业界常见做法

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| First-Write-Wins | 保留历史数据 | 可能丢失最新修改 | ❌ 不推荐 |
| **Last-Write-Wins** | 保留最新数据 | 可能覆盖早期修改 | ✅ 推荐 |
| 用户手动选择 | 用户控制 | 体验差，复杂 | ⚠️ 后续版本 |
| 向量时钟 | 精确冲突检测 | 实现复杂 | ❌ 过度设计 |

**Implementation Notes**:
- 使用 `updatedAt` 时间戳比较
- 云端时间优先（避免客户端时间不准确）
- 冲突解决后通过 Toast 通知用户

---

## 4. 实时同步触发机制

### Decision: 数据变更时立即同步

**Rationale**:
- 用户期望即时看到变更
- 辅食数据量小，实时同步开销可控
- 简化用户操作，无需手动触发

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| 手动触发 | 用户可控 | 体验差 | ❌ 不推荐 |
| 定时同步 | 省电 | 数据不及时 | ❌ 不推荐 |
| 应用启动时同步 | 简单 | 可能遗漏变更 | ❌ 不推荐 |
| **实时同步** | 数据最新 | 频繁网络请求 | ✅ 推荐 |

**Implementation Notes**:
- 使用 Flow 观察数据变更
- 变更时触发同步任务
- 添加防抖机制（500ms）避免频繁同步
- 网络不可用时缓存变更，恢复后自动同步

---

## 5. Entity 映射代码统一方案

### Decision: 在 Entity 文件中集中定义扩展函数

**Rationale**:
- 映射逻辑与 Entity 定义放在一起，便于维护
- 扩展函数是 Kotlin 惯用方式
- 避免在 Repository 中重复定义

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| Mapper 类 | 集中管理 | 增加类数量 | ❌ 不推荐 |
| Repository 内定义 | 就近使用 | 重复代码 | ❌ 当前问题 |
| **Entity 扩展函数** | 集中且简洁 | 需要迁移 | ✅ 推荐 |

**Implementation Notes**:
- 每个 Entity 文件添加 `toDomainModel()` 和 `toEntity()` 扩展函数
- 移除 Repository 中的重复映射代码
- 移除顶层扩展函数文件（如有）

---

## 6. BaseUiViewModel 简化方案

### Decision: 合并为单一 `copyState` 抽象方法

**Rationale**:
- 减少样板代码
- 数据类本身已有 `copy()` 方法
- 保持向后兼容性

**Alternatives Considered**:

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| 反射实现 | 无需抽象方法 | 性能开销 | ❌ 不推荐 |
| 保留 3 个方法 | 无需修改 | 样板代码多 | ❌ 当前问题 |
| **单一 copyState** | 简洁 | 需要迁移 | ✅ 推荐 |

**Implementation Notes**:
- 新抽象方法：`protected abstract fun T.copyState(isLoading: Boolean, error: String?): T`
- 保留旧方法并标记 `@Deprecated`
- 渐进式迁移现有 ViewModel

---

## Summary

| 问题 | 决策 | 风险等级 |
|------|------|----------|
| API Key 安全 | 后端代理 | 低 |
| ID 映射 | Room 数据库表 | 低 |
| 冲突解决 | Last-Write-Wins | 低 |
| 同步触发 | 实时同步 | 中 |
| 映射代码 | Entity 扩展函数 | 低 |
| ViewModel 简化 | 单一 copyState | 中 |

**关键依赖**:
- 后端 AI 代理服务需部署
- 数据库迁移脚本需完善
- 网络状态检测机制
