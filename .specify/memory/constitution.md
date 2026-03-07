# BabyFood 项目宪法

## 核心原则

### I. 安全优先 (Security First)
所有敏感信息（API Key、用户凭证、个人数据）必须严格保护。禁止在代码中硬编码任何敏感凭证，必须通过后端代理调用 AI 服务。用户认证使用 JWT + 验证码机制，Token 过期后必须重新登录。

### II. 云端优先 (Cloud First)
所有数据以云端为准，本地仅作为缓存。应用启动时必须从云端拉取最新数据。数据变更时立即同步到云端。离线状态下仅提供只读访问，编辑功能需要网络连接。

### III. 数据一致性 (Data Consistency)
所有实体必须支持版本控制（version 字段）和软删除（isDeleted 标志），以支持多设备同步。同步冲突必须使用 Last-Write-Wins 策略自动解决。云端 ID 作为主键，本地 ID 映射到云端 ID。

### IV. 代码质量 (Code Quality)
消除重复代码，遵循 DRY 原则。Entity 和 Domain Model 的映射代码必须集中在 Entity 文件中。所有 Repository 必须基于 BaseRepository 或 SyncableRepository 构建。代码必须通过静态分析（detekt）和编译检查。

### V. 用户隐私 (User Privacy)
用户数据所有权归用户所有。云端只存储脱敏后的数据，敏感信息（婴儿姓名、生日、过敏信息）可选择加密存储。用户可以随时导出或删除自己的数据。

### VI. 可维护性 (Maintainability)
使用 MVVM 架构，分层清晰（UI → ViewModel → Repository → Service → API）。使用 Hilt 进行依赖注入，避免手动实例化。所有组件必须打印日志（使用类名作为标签），便于问题排查。遵循 Kotlin 官方代码风格。

### VII. 向后兼容 (Backward Compatibility)
公共 API 和架构接口的变更必须保持向后兼容。废弃的方法必须使用 @Deprecated 注解，并提供默认实现。现有 ViewModel 的迁移不能影响功能。

## 质量门禁

- 所有代码必须通过 `./gradlew build` 编译检查
- 新功能必须包含日志输出（Log.d/i/w/e）
- 敏感信息扫描必须通过（无硬编码 API Key）
- API 接口必须符合 OpenAPI 契约

## 治理规则

本宪法优先于所有其他开发实践。任何变更必须：
1. 不违反宪法原则
2. 通过代码审查
3. 保持向后兼容（除非在主要版本发布中）

**版本**: 1.0 | **批准日期**: 2026-03-06 | **最后修订**: 2026-03-06
