# Implementation Plan: 架构优化与安全增强

**Branch**: `001-architecture-optimization` | **Date**: 2026-03-04 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-architecture-optimization/spec.md`

## Summary

本功能旨在解决 BabyFood Android 项目的四个核心架构问题：
1. **P1 安全问题**：移除硬编码的 DashScope API Key，改用后端代理方案
2. **P2 同步功能**：完善 SyncManager 的 TODO 实现，包括 ID 映射、冲突解决、实时同步
3. **P3 代码优化**：统一 Entity/Domain Model 映射代码位置，消除重复
4. **P3 架构改进**：简化 BaseUiViewModel 抽象设计，减少样板代码

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose, Room 2.6.1, Hilt 2.50, Retrofit 2.9.0
**Storage**: Room Database (SQLite) + SharedPreferences
**Testing**: JUnit 4.13.2, Espresso 3.5.1
**Target Platform**: Android 7.0+ (minSdk 24, targetSdk 34)
**Project Type**: Mobile (Android)
**Performance Goals**: 同步延迟 < 10秒，UI 响应 < 100ms
**Constraints**: 离线优先，后端代理不可用时禁用 AI 功能
**Scale/Scope**: ~30 个 UI 屏幕，12 个 Repository，14 个 API 服务

## Constitution Check

*GATE: Must pass before Phase 0 research.*

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 安全合规 | ✅ PASS | 移除硬编码 API Key 符合安全最佳实践 |
| 代码质量 | ✅ PASS | 统一映射代码提高可维护性 |
| 向后兼容 | ✅ PASS | 保持现有 ViewModel 兼容性 |
| 测试覆盖 | ⚠️ WARN | 当前测试覆盖率较低，需补充 |

## Project Structure

### Documentation (this feature)

```
specs/001-architecture-optimization/
├── plan.md              # 本文件
├── research.md          # Phase 0 研究文档
├── data-model.md        # Phase 1 数据模型
├── quickstart.md        # Phase 1 快速入门
├── contracts/           # Phase 1 API 合约
│   └── openapi.yaml
├── checklists/          # 质量检查清单
│   └── requirements.md
└── spec.md              # 功能规格
```

### Source Code (repository root)

```
app/src/main/java/com/example/babyfood/
├── data/
│   ├── local/database/
│   │   ├── entity/
│   │   │   ├── IdMappingEntity.kt      # 新增：ID 映射实体
│   │   │   └── *Entity.kt              # 修改：统一映射函数
│   │   ├── dao/
│   │   │   └── IdMappingDao.kt         # 新增：ID 映射 DAO
│   │   └── BabyFoodDatabase.kt         # 修改：添加 id_mappings 表
│   ├── remote/
│   │   ├── api/
│   │   │   └── AiProxyApiService.kt    # 新增：AI 代理 API
│   │   └── dto/
│   │       └── AiProxyRequest.kt       # 新增：AI 代理 DTO
│   ├── repository/
│   │   ├── IdMappingRepository.kt      # 新增：ID 映射仓库
│   │   └── *Repository.kt              # 修改：移除重复映射代码
│   ├── sync/
│   │   └── SyncManager.kt              # 修改：完成 TODO 实现
│   └── ai/
│       ├── BackendAiProxyStrategy.kt   # 新增：后端代理策略
│       └── RemoteHealthAnalysisStrategy.kt  # 修改：移除硬编码 Key
├── presentation/ui/
│   └── BaseViewModel.kt                # 修改：简化抽象方法
└── di/
    └── NetworkModule.kt                # 修改：添加 AI 代理服务
```

**Structure Decision**: 遵循现有 MVVM 架构，在对应模块中添加新文件或修改现有文件。

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| 新增 IdMappingEntity | 实时同步需要云端/本地 ID 映射 | SharedPreferences 无法高效查询映射关系 |
| 新增 AI 代理策略 | 安全性要求，避免 API Key 泄露 | Keystore 方案仍需存储 Key，代理更安全 |

## Implementation Phases

### Phase 1: 安全问题修复 (P1)

**目标**: 移除硬编码 API Key，实现后端代理

**任务**:
1. 创建 `BackendAiProxyStrategy` 调用后端 AI 代理接口
2. 修改 `RemoteHealthAnalysisStrategy` 移除硬编码 Key
3. 更新 `StrategyManager` 使用新策略
4. 添加后端代理不可用时的错误处理

**验收标准**:
- [ ] 代码扫描无硬编码敏感凭证
- [ ] AI 服务调用通过后端代理
- [ ] 代理不可用时显示错误提示

### Phase 2: 同步功能完善 (P2)

**目标**: 完成 SyncManager 的 TODO 实现

**任务**:
1. 创建 `IdMappingEntity` 和 `IdMappingDao`
2. 实现 `IdMappingRepository`
3. 完成 `SyncManager` 中的 ID 映射逻辑
4. 实现 Last-Write-Wins 冲突解决
5. 添加实时同步触发机制
6. 实现 SharedPreferences 存储 lastSyncTime

**验收标准**:
- [ ] 云端 ID 与本地 ID 正确映射
- [ ] 冲突自动解决并通过 Toast 通知
- [ ] 数据变更时自动触发同步

### Phase 3: 代码优化 (P3)

**目标**: 统一 Entity 映射代码位置

**任务**:
1. 在每个 Entity 文件中添加双向映射扩展函数
2. 移除 Repository 中的重复映射代码
3. 添加映射函数单元测试

**验收标准**:
- [ ] 所有映射函数集中在 Entity 文件
- [ ] Repository 无重复映射代码
- [ ] 单元测试覆盖率 ≥ 80%

### Phase 4: 架构改进 (P3)

**目标**: 简化 BaseUiViewModel 抽象设计

**任务**:
1. 重构 `BaseUiViewModel` 为单一抽象方法
2. 迁移现有 ViewModel 保持兼容
3. 添加迁移文档和示例

**验收标准**:
- [ ] BaseUiViewModel 仅需实现 1 个抽象方法
- [ ] 现有 ViewModel 正常工作
- [ ] 新 ViewModel 样板代码减少 50%

## Dependencies

### 外部依赖
- 后端 AI 代理服务已部署（BabyFoodBackend `/api/v1/images/analyze`）
- Android Keystore 可用（API 23+）

### 内部依赖
- Room 数据库迁移至 v17
- Hilt 依赖注入配置
- 现有 SyncManager 架构

## Risk Assessment

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 后端代理服务不可用 | 中 | 高 | 添加降级机制，禁用 AI 功能 |
| 数据库迁移失败 | 低 | 高 | 完善迁移脚本，添加回滚机制 |
| ViewModel 迁移兼容性问题 | 中 | 中 | 保留旧接口，渐进式迁移 |

## Next Steps

1. 执行 `/speckit.tasks` 生成详细任务列表
2. 按 P1 → P2 → P3 优先级实施
3. 每个 Phase 完成后进行集成测试
