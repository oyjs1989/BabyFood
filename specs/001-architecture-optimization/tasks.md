# Tasks: 架构优化与安全增强

**Input**: Design documents from `/specs/001-architecture-optimization/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml

**Tests**: Not explicitly requested in feature spec - tests are optional.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and configuration updates

- [ ] T001 [P] Create new feature branch `001-architecture-optimization` from main
- [ ] T002 [P] Update `gradle.properties` with any new configuration needed
- [ ] T003 [P] Verify backend AI proxy service is deployed and accessible at `http://39.108.143.232:8080/api/v1/ai/health-analysis`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 Create `IdMappingEntity` in `app/src/main/java/com/example/babyfood/data/local/database/entity/IdMappingEntity.kt`
- [ ] T005 [P] Create `IdMappingDao` in `app/src/main/java/com/example/babyfood/data/local/database/dao/IdMappingDao.kt`
- [ ] T006 [P] Create `EntityType` enum in `app/src/main/java/com/example/babyfood/domain/model/EntityType.kt`
- [ ] T007 [P] Create `IdMapping` domain model in `app/src/main/java/com/example/babyfood/domain/model/IdMapping.kt`
- [ ] T008 Create `IdMappingRepository` in `app/src/main/java/com/example/babyfood/data/repository/IdMappingRepository.kt`
- [ ] T009 Add `MIGRATION_16_17` to `BabyFoodDatabase.kt` for `id_mappings` table
- [ ] T010 Update database version to 17 in `BabyFoodDatabase.kt`
- [ ] T011 Register `IdMappingDao` in `DatabaseModule.kt` Hilt module
- [ ] T012 Create `AiProxyApiService` interface in `app/src/main/java/com/example/babyfood/data/remote/api/AiProxyApiService.kt`
- [ ] T013 Create `HealthAnalysisProxyRequest` DTO in `app/src/main/java/com/example/babyfood/data/remote/dto/HealthAnalysisProxyRequest.kt`
- [ ] T014 [P] Create `HealthAnalysisProxyResponse` DTO in `app/src/main/java/com/example/babyfood/data/remote/dto/HealthAnalysisProxyResponse.kt`
- [ ] T015 Register `AiProxyApiService` in `NetworkModule.kt` Hilt module

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 安全移除硬编码 API Key (Priority: P1) 🎯 MVP

**Goal**: 移除代码中硬编码的 DashScope API Key，通过后端代理调用 AI 服务，确保应用安全性。

**Independent Test**: 
1. 运行代码扫描 `rg "sk-[a-f0-9]{32}" app/src/main/java/` 应返回空结果
2. AI 健康分析服务仍能正常工作（通过后端代理）
3. 后端代理不可用时显示错误提示

### Implementation for User Story 1

- [ ] T016 [US1] Create `BackendAiProxyStrategy` in `app/src/main/java/com/example/babyfood/data/ai/BackendAiProxyStrategy.kt` - implements `HealthAnalysisStrategy` using `AiProxyApiService`
- [ ] T017 [US1] Update `StrategyManager.kt` to provide `BackendAiProxyStrategy` as default health analysis strategy
- [ ] T018 [US1] Remove hardcoded API Key from `RemoteHealthAnalysisStrategy.kt` (delete or comment out `sk-aa30af1a40a643cbb8f43881c1ebbb49`)
- [ ] T019 [US1] Add error handling in `BackendAiProxyStrategy` for service unavailable (503) - return null and log error
- [ ] T020 [US1] Add fallback logic in `StrategyManager` - when proxy fails, show Toast "AI 服务暂时不可用"
- [ ] T021 [US1] Add logging to `BackendAiProxyStrategy` for debugging (request/response)
- [ ] T022 [US1] Verify no hardcoded API keys exist: run `rg "sk-[a-f0-9]{32}" app/src/main/java/` and confirm empty result

**Checkpoint**: User Story 1 complete - API Key 已移除，AI 服务通过后端代理调用

---

## Phase 4: User Story 2 - 完善云端数据同步功能 (Priority: P2)

**Goal**: 完成 SyncManager 的 TODO 实现，实现云端 ID 与本地 ID 映射、冲突解决、实时同步。

**Independent Test**: 
1. 在设备 A 创建计划后，设备 B 能看到更新
2. 冲突自动解决并通过 Toast 通知用户
3. 数据变更时自动触发同步

### Implementation for User Story 2

- [ ] T023 [US2] Implement `toDomainModel()` extension in `IdMappingEntity.kt` - converts Entity to Domain model
- [ ] T024 [US2] Implement `toEntity()` extension in `IdMappingEntity.kt` - converts Domain model to Entity
- [ ] T025 [US2] Implement `getByLocalId()` in `IdMappingRepository` - queries mapping by local ID
- [ ] T026 [US2] Implement `getByCloudId()` in `IdMappingRepository` - queries mapping by cloud ID
- [ ] T027 [US2] Implement `upsert()` in `IdMappingRepository` - creates or updates ID mapping
- [ ] T028 [US2] Create `ConflictInfo` domain model in `app/src/main/java/com/example/babyfood/domain/model/ConflictInfo.kt`
- [ ] T029 [US2] Create `SyncResult` domain model in `app/src/main/java/com/example/babyfood/domain/model/SyncResult.kt`
- [ ] T030 [US2] Create `SyncState` enum in `app/src/main/java/com/example/babyfood/domain/model/SyncState.kt`
- [ ] T031 [US2] Implement `getLocalId()` in `SyncManager.kt` using `IdMappingRepository`
- [ ] T032 [US2] Implement `getCloudId()` in `SyncManager.kt` using `IdMappingRepository`
- [ ] T033 [US2] Implement `saveIdMapping()` in `SyncManager.kt` - persists ID mapping after successful sync
- [ ] T034 [US2] Implement `resolveConflict()` in `SyncManager.kt` - Last-Write-Wins strategy using `updatedAt` timestamp
- [ ] T035 [US2] Add `lastSyncTime` storage using SharedPreferences in `SyncManager.kt`
- [ ] T036 [US2] Implement `getLastSyncTime()` in `SyncManager.kt` - reads from SharedPreferences
- [ ] T037 [US2] Implement `setLastSyncTime()` in `SyncManager.kt` - writes to SharedPreferences
- [ ] T038 [US2] Add conflict notification - show Toast when conflict is auto-resolved
- [ ] T039 [US2] Implement real-time sync trigger in `SyncManager.kt` - observe data changes and trigger sync with 500ms debounce
- [ ] T040 [US2] Add sync status logging in `SyncManager.kt` - log pull/push/conflict events
- [ ] T041 [US2] Handle offline sync - queue changes when network unavailable, sync when restored
- [ ] T042 [US2] Update `SyncApiService.kt` to match OpenAPI contract paths (`/api/v1/sync/pull`, `/api/v1/sync/push`)

**Checkpoint**: User Story 2 complete - 同步功能完整实现，支持 ID 映射、冲突解决、实时同步

---

## Phase 5: User Story 3 - 统一 Entity 映射代码位置 (Priority: P3)

**Goal**: 将 Entity 和 Domain Model 之间的映射代码统一到 Entity 文件中，消除 Repository 中的重复代码。

**Independent Test**: 
1. 所有 `toDomainModel()` 和 `toEntity()` 函数都在 Entity 文件中定义
2. Repository 文件中无重复映射代码
3. 映射函数工作正常

### Implementation for User Story 3

- [ ] T043 [P] [US3] Add `toDomainModel()` and `toEntity()` to `BabyEntity.kt` if not present
- [ ] T044 [P] [US3] Add `toDomainModel()` and `toEntity()` to `PlanEntity.kt` if not present
- [ ] T045 [P] [US3] Add `toDomainModel()` and `toEntity()` to `RecipeEntity.kt` if not present
- [ ] T046 [P] [US3] Add `toDomainModel()` and `toEntity()` to `HealthRecordEntity.kt` if not present
- [ ] T047 [P] [US3] Add `toDomainModel()` and `toEntity()` to `GrowthRecordEntity.kt` if not present
- [ ] T048 [P] [US3] Add `toDomainModel()` and `toEntity()` to `InventoryItemEntity.kt` if not present
- [ ] T049 [P] [US3] Add `toDomainModel()` and `toEntity()` to `UserEntity.kt` if not present
- [ ] T050 [US3] Remove duplicate mapping code from `BabyRepository.kt` - use Entity extensions instead
- [ ] T051 [US3] Remove duplicate mapping code from `PlanRepository.kt` - use Entity extensions instead
- [ ] T052 [US3] Remove duplicate mapping code from `RecipeRepository.kt` - use Entity extensions instead
- [ ] T053 [US3] Remove duplicate mapping code from `HealthRecordRepository.kt` - use Entity extensions instead
- [ ] T054 [US3] Remove duplicate mapping code from `GrowthRecordRepository.kt` - use Entity extensions instead
- [ ] T055 [US3] Remove duplicate mapping code from `InventoryRepository.kt` - use Entity extensions instead
- [ ] T056 [US3] Verify all mapping code is in Entity files: run `rg "fun toDomainModel" app/src/main/java/` and confirm only Entity files

**Checkpoint**: User Story 3 complete - 映射代码已统一到 Entity 文件

---

## Phase 6: User Story 4 - 简化 BaseUiViewModel 抽象设计 (Priority: P3)

**Goal**: 将 BaseUiViewModel 的抽象方法从 3 个减少到 1 个，减少 ViewModel 实现的样板代码。

**Independent Test**: 
1. 新 ViewModel 只需实现 1 个抽象方法
2. 现有 ViewModel 仍能正常工作
3. `setLoading()` 和 `setError()` 方法正常工作

### Implementation for User Story 4

- [ ] T057 [US4] Create new `BaseUiState` interface in `app/src/main/java/com/example/babyfood/presentation/ui/BaseUiState.kt` with `isLoading: Boolean` and `error: String?`
- [ ] T058 [US4] Update `BaseUiViewModel.kt` - add single abstract method `protected abstract fun T.copyState(isLoading: Boolean, error: String?): T`
- [ ] T059 [US4] Update `BaseUiViewModel.kt` - deprecate old methods `copyWithLoading()`, `copyWithError()`, `copyWithErrorAndLoading()` with `@Deprecated` annotation
- [ ] T060 [US4] Update `BaseUiViewModel.kt` - implement deprecated methods using `copyState()` as default implementation
- [ ] T061 [US4] Update `setLoading()` in `BaseUiViewModel.kt` to use `copyState()`
- [ ] T062 [US4] Update `setError()` in `BaseUiViewModel.kt` to use `copyState()`
- [ ] T063 [US4] Update `BabyViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T064 [US4] Update `HomeViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T065 [US4] Update `PlansViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T066 [US4] Update `RecipesViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T067 [US4] Update `HealthRecordViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T068 [US4] Update `GrowthRecordViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T069 [US4] Update `InventoryViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T070 [US4] Update `LoginViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T071 [US4] Update `RegisterViewModel.kt` to implement `copyState()` and remove deprecated method overrides
- [ ] T072 [US4] Verify build succeeds: run `gradlew.bat assembleDebug`

**Checkpoint**: User Story 4 complete - BaseUiViewModel 已简化，现有 ViewModel 正常工作

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T073 [P] Update `IFLOW.md` with new database version (17) and new entities
- [ ] T074 [P] Update `AGENTS.md` with architecture optimization changes
- [ ] T075 Run quickstart.md validation - verify all phases work as documented
- [ ] T076 Run full build and test: `gradlew.bat assembleDebug && gradlew.bat test`
- [ ] T077 Code cleanup - remove any unused imports or commented code
- [ ] T078 Security audit - verify no hardcoded credentials remain in codebase

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - US1 (P1): Can start immediately after Foundational
  - US2 (P2): Can start after Foundational (independent of US1)
  - US3 (P3): Can start after Foundational (independent of US1, US2)
  - US4 (P3): Can start after Foundational (independent of US1, US2, US3)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories - can implement independently
- **User Story 2 (P2)**: No dependencies on other stories - can implement independently
- **User Story 3 (P3)**: No dependencies on other stories - can implement independently
- **User Story 4 (P3)**: No dependencies on other stories - can implement independently

### Within Each User Story

- Models/Entities before services
- Services before integration
- Core implementation before polish

### Parallel Opportunities

**Within Phase 2 (Foundational)**:
- T004, T005, T006, T007 can run in parallel (different files)
- T013, T014 can run in parallel (different files)

**Within Phase 5 (User Story 3)**:
- T043-T049 can ALL run in parallel (different Entity files)

**Within Phase 7 (Polish)**:
- T073, T074 can run in parallel (different files)

**Across User Stories**:
- Once Foundational phase is complete, all 4 user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 3 (Entity Mappings)

```bash
# Launch all Entity mapping tasks in parallel:
Task: "Add toDomainModel() and toEntity() to BabyEntity.kt"
Task: "Add toDomainModel() and toEntity() to PlanEntity.kt"
Task: "Add toDomainModel() and toEntity() to RecipeEntity.kt"
Task: "Add toDomainModel() and toEntity() to HealthRecordEntity.kt"
Task: "Add toDomainModel() and toEntity() to GrowthRecordEntity.kt"
Task: "Add toDomainModel() and toEntity() to InventoryItemEntity.kt"
Task: "Add toDomainModel() and toEntity() to UserEntity.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (API Key Security)
4. **STOP and VALIDATE**: Test API Key removal independently
5. Deploy if ready - security issue resolved

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy (Security Fix!)
3. Add User Story 2 → Test independently → Deploy (Sync Feature!)
4. Add User Story 3 → Test independently → Deploy (Code Quality!)
5. Add User Story 4 → Test independently → Deploy (Architecture!)

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (P1 - Security)
   - Developer B: User Story 2 (P2 - Sync)
   - Developer C: User Story 3 (P3 - Code Quality)
   - Developer D: User Story 4 (P3 - Architecture)
3. Stories complete and integrate independently

---

## Task Summary

| Phase | Tasks | Parallel | Story |
|-------|-------|----------|-------|
| Phase 1: Setup | 3 | 3 | - |
| Phase 2: Foundational | 12 | 4 | - |
| Phase 3: US1 (P1) | 7 | 0 | US1 |
| Phase 4: US2 (P2) | 20 | 0 | US2 |
| Phase 5: US3 (P3) | 14 | 7 | US3 |
| Phase 6: US4 (P3) | 16 | 0 | US4 |
| Phase 7: Polish | 6 | 2 | - |
| **Total** | **78** | **16** | - |

### Tasks Per User Story

- **User Story 1 (P1)**: 7 tasks - Security fix, highest priority
- **User Story 2 (P2)**: 20 tasks - Sync feature, most complex
- **User Story 3 (P3)**: 14 tasks - Code quality, 7 parallelizable
- **User Story 4 (P3)**: 16 tasks - Architecture, affects many ViewModels

### Parallel Opportunities

- **16 tasks can run in parallel** (different files, no dependencies)
- **User Stories 1-4 can run in parallel** after Foundational phase

### Suggested MVP Scope

**Minimum Viable Product**: User Story 1 (P1) only
- Fixes critical security vulnerability
- Can be deployed independently
- 22 total tasks (Setup + Foundational + US1)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Tests not included as they were not explicitly requested in the spec
