# Feature Specification: 架构优化与安全增强

**Feature Branch**: `001-architecture-optimization`  
**Created**: 2026-03-04  
**Status**: Draft  
**Input**: 架构优化与安全增强：移除硬编码API Key、完善SyncManager同步功能、统一Entity映射代码、简化BaseUiViewModel抽象设计

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 安全移除硬编码 API Key (Priority: P1)

作为开发者，我需要移除代码中硬编码的 API Key，以防止敏感信息泄露，确保应用的安全性。当应用需要调用 AI 服务时，应通过后端代理或安全的密钥管理方案获取访问凭证。

**Why this priority**: 安全问题是最高优先级，硬编码 API Key 存在严重的安全风险，可能导致 API 滥用、费用损失和数据泄露。

**Independent Test**: 可通过代码审查确认无硬编码 API Key，并通过运行时测试验证 AI 服务调用仍能正常工作。

**Acceptance Scenarios**:

1. **Given** 代码库中存在硬编码的 DashScope API Key，**When** 执行安全审计扫描，**Then** 不再发现任何硬编码的敏感凭证
2. **Given** 应用需要调用 AI 健康分析服务，**When** 发起 API 请求，**Then** 请求通过后端代理转发，不直接暴露 API Key
3. **Given** 后端代理服务不可用，**When** 尝试调用 AI 服务，**Then** 系统优雅降级并提示用户服务暂时不可用

---

### User Story 2 - 完善云端数据同步功能 (Priority: P2)

作为用户，我需要在多个设备间同步宝宝的辅食计划和记录。当我在手机上添加新的计划后，应该能在其他设备上看到更新。

**Why this priority**: 多设备同步是核心功能，当前 SyncManager 存在多个未实现的 TODO，导致同步功能无法正常工作。

**Independent Test**: 可通过在两个设备上登录同一账号，验证数据同步是否正常工作。

**Acceptance Scenarios**:

1. **Given** 用户在设备 A 上创建了新的餐单计划，**When** 用户在设备 B 上打开应用，**Then** 设备 B 显示最新的计划数据
2. **Given** 云端和本地数据存在冲突，**When** 执行同步操作，**Then** 系统根据 Last-Write-Wins 策略解决冲突并通知用户
3. **Given** 用户首次启动应用，**When** 执行初始同步，**Then** 系统正确映射云端 ID 与本地 ID

---

### User Story 3 - 统一 Entity 映射代码位置 (Priority: P3)

作为开发者，我需要统一 Entity 和 Domain Model 之间的映射代码位置，以减少代码重复和维护成本。

**Why this priority**: 当前映射代码分散在 Entity 文件、Repository 和顶层扩展函数中，容易产生不一致。

**Independent Test**: 可通过代码审查确认所有映射代码集中在 Entity 文件中，并通过单元测试验证映射正确性。

**Acceptance Scenarios**:

1. **Given** 存在 Entity 到 Domain Model 的映射需求，**When** 开发者查看 Entity 文件，**Then** 能找到双向映射扩展函数
2. **Given** Repository 需要进行实体转换，**When** 调用映射函数，**Then** 使用 Entity 文件中定义的扩展函数而非重复实现

---

### User Story 4 - 简化 BaseUiViewModel 抽象设计 (Priority: P3)

作为开发者，我需要简化 BaseUiViewModel 的抽象方法数量，减少 ViewModel 实现的样板代码。

**Why this priority**: 当前需要实现 3 个 copy 方法，增加了开发复杂度，数据类本身已有 copy() 方法。

**Independent Test**: 可通过创建新的 ViewModel 并验证只需实现最少的抽象方法。

**Acceptance Scenarios**:

1. **Given** 新建一个 ViewModel 继承 BaseUiViewModel，**When** 实现必需的抽象方法，**Then** 只需实现 1 个统一的 copyState 方法
2. **Given** UiState 数据类包含 isLoading 和 error 字段，**When** 调用 setLoading() 或 setError()，**Then** 状态正确更新

---

### Edge Cases

- 当后端代理服务完全不可用时，显示错误提示，禁用 AI 功能直到服务恢复
- 当同步过程中网络中断时，如何处理部分同步的状态？
- 当 Entity 和 Domain Model 字段类型不匹配时（如 LocalDate vs String），映射如何处理？

## Requirements *(mandatory)*

### Functional Requirements

#### 安全性要求

- **FR-001**: 系统 MUST NOT 在代码中硬编码任何敏感凭证（API Key、密码、令牌）
- **FR-002**: 系统 MUST 通过后端代理转发所有第三方 AI 服务请求
- **FR-003**: 系统 MUST 在后端代理不可用时显示错误提示并禁用 AI 功能直到服务恢复
- **FR-004**: 系统 MUST 使用 Android Keystore 或 EncryptedSharedPreferences 存储本地敏感配置

#### 同步功能要求

- **FR-005**: 系统 MUST 实现云端 ID 与本地 ID 的双向映射
- **FR-006**: 系统 MUST 实现 Last-Write-Wins 冲突解决策略
- **FR-007**: 系统 MUST 使用 SharedPreferences 存储 lastSyncTime
- **FR-008**: 系统 MUST 在同步失败时记录详细日志并通知用户
- **FR-009**: 系统 MUST 正确处理软删除实体的同步
- **FR-010**: 系统 MUST 在每次数据变更时立即触发实时同步

#### 代码优化要求

- **FR-010**: 系统 MUST 在 Entity 文件中集中定义双向映射扩展函数
- **FR-011**: 系统 MUST 移除 Repository 中重复的映射代码
- **FR-012**: 系统 MUST 为所有映射函数提供单元测试覆盖

#### 架构改进要求

- **FR-013**: 系统 MUST 将 BaseUiViewModel 的抽象方法减少到 1 个
- **FR-014**: 系统 MUST 保持现有 ViewModel 的向后兼容性
- **FR-015**: 系统 MUST 确保 UiState 接口包含 isLoading 和 error 字段

### Key Entities

- **SyncMetadata**: 同步元数据，包含 cloudId、syncStatus、lastSyncTime、version、isDeleted 字段
- **IdMapping**: ID 映射关系，记录云端 ID 与本地 ID 的对应关系
- **ConflictInfo**: 冲突信息，记录冲突的实体类型、ID、版本和冲突原因
- **UiState**: UI 状态接口，包含 isLoading 和 error 字段

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 代码安全审计扫描通过率 100%，无硬编码敏感凭证
- **SC-002**: 用户可在 10 秒内完成跨设备数据同步
- **SC-003**: 同步冲突自动解决率达到 95% 以上
- **SC-004**: Entity 映射代码减少 30% 以上（通过消除重复）
- **SC-005**: 新 ViewModel 实现所需样板代码减少 50%
- **SC-006**: 所有映射函数单元测试覆盖率达到 80% 以上

## Clarifications

### Session 2026-03-04

- Q: 云端 ID 与本地 ID 的映射关系应该存储在哪里？ → A: 使用本地 Room 数据库新建 id_mappings 表存储映射关系
- Q: 当冲突自动解决后，如何通知用户？ → A: 使用 Toast/Snackbar 显示简短通知
- Q: 当后端代理不可用时，AI 健康分析功能应如何降级？ → A: 显示错误提示，禁用 AI 功能直到服务恢复
- Q: 数据同步应该在什么时候自动触发？ → A: 实时同步（每次数据变更立即同步）

## Assumptions

- 后端代理服务已部署并可用（或将在本次优化中同步实现）
- 用户设备支持 Android Keystore（API 23+，当前 minSdk 为 24）
- 现有 ViewModel 迁移期间保持功能稳定性
- 同步功能暂不支持用户手动选择冲突解决策略（后续版本考虑）
- ID 映射关系持久化在本地 Room 数据库中（id_mappings 表）
