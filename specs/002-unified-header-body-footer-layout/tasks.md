# Tasks: 统一页面 Header 布局样式

**Input**: Design documents from `/specs/002-unified-header-body-footer-layout/`
**Prerequisites**: plan.md, spec.md, data-model.md, research.md

**Tests**: Tests are NOT included in this task list (not explicitly requested in feature specification)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions
- Android mobile project paths shown below
- `app/src/main/java/com/example/babyfood/` - Kotlin source code
- `app/src/test/java/com/example/babyfood/` - Unit tests
- `app/src/androidTest/java/com/example/babyfood/` - UI tests

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create directory structure and verify dependencies

- [ ] T001 Create `app/src/main/java/com/example/babyfood/presentation/ui/common/` directory structure
- [ ] T002 Verify Coil image loading library is available in `app/build.gradle.kts` (add if missing)
- [ ] T003 Verify Jetpack Compose BOM and Material Design 3 dependencies are available in `app/build.gradle.kts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data models that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 [P] Create `AppHeaderConfig` data class in `app/src/main/java/com/example/babyfood/presentation/ui/common/AppHeaderConfig.kt`
- [ ] T005 [P] Create `UserMenuItem` data class in `app/src/main/java/com/example/babyfood/presentation/ui/common/UserMenuItem.kt`
- [ ] T006 [P] Create `UserMenuConfig` data class in `app/src/main/java/com/example/babyfood/presentation/ui/common/UserMenuConfig.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 统一 Header 布局显示 (Priority: P1) 🎯 MVP

**Goal**: 创建统一的 Header 组件，在所有页面显示一致的布局（左侧应用名称，右侧登录状态）

**Independent Test**: 通过浏览首页（今日餐单）和至少 2 个其他页面，验证每个页面的 Header 都遵循相同的布局：左侧显示 "BabyFood"，右侧显示登录状态或登录按钮

### Implementation for User Story 1

- [ ] T007 [P] [US1] Create `LoginButton` composable in `app/src/main/java/com/example/babyfood/presentation/ui/common/LoginButton.kt` with basic styling and click handler
- [ ] T008 [P] [US1] Create `RegisterButton` composable in `app/src/main/java/com/example/babyfood/presentation/ui/common/RegisterButton.kt` with basic styling and click handler
- [ ] T009 [US1] Create basic `AppHeader` composable in `app/src/main/java/com/example/babyfood/presentation/ui/common/AppHeader.kt` that uses `Row`, `Surface`, and displays app name on left and login button on right
- [ ] T010 [US1] Integrate `AuthRepository` observation in `AppHeader.kt` using `collectAsState()` to react to authentication state changes
- [ ] T011 [US1] Add Material Design 3 styling to `AppHeader.kt` (56dp height, proper colors, elevation, spacing)
- [ ] T012 [US1] Add `WindowInsets` padding to `AppHeader.kt` for system bar handling
- [ ] T013 [US1] Create `LoginStateIndicator` composable in `app/src/main/java/com/example/babyfood/presentation/ui/common/LoginStateIndicator.kt` to handle different auth states (Authenticated, Unauthenticated, Loading)
- [ ] T014 [US1] Integrate `AppHeader` into `TodayMenuScreen.kt` in `app/src/main/java/com/example/babyfood/presentation/ui/home/components/TodayMenuScreen.kt`
- [ ] T015 [US1] Configure navigation for login button in `TodayMenuScreen.kt` to navigate to login route
- [ ] T016 [US1] Integrate `AppHeader` into `RecipesListScreen.kt` in `app/src/main/java/com/example/babyfood/presentation/ui/recipes/RecipesListScreen.kt`
- [ ] T017 [US1] Integrate `AppHeader` into `BabyListScreen.kt` in `app/src/main/java/com/example/babyfood/presentation/ui/baby/BabyListScreen.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional - Header displays on at least 3 pages with consistent layout

---

## Phase 4: User Story 2 - Header 响应式适配 (Priority: P2)

**Goal**: Header 自动适配不同屏幕尺寸和屏幕方向变化

**Independent Test**: 在不同屏幕尺寸的模拟器或真机上测试应用，验证 Header 在小屏幕、大屏幕、竖屏、横屏下都能正确显示应用名称和登录状态

### Implementation for User Story 2

- [ ] T018 [US2] Enhance `AppHeader.kt` with `BoxWithConstraints` to handle different screen sizes
- [ ] T019 [US2] Add `Modifier.weight()` to app name and login state areas in `AppHeader.kt` for responsive layout
- [ ] T020 [US2] Add screen orientation change handling in `AppHeader.kt` using `Configuration` observation
- [ ] T021 [US2] Test responsive layout on small screens (320dp width) in `AppHeader.kt`
- [ ] T022 [US2] Test responsive layout on large screens (600dp+ width) in `AppHeader.kt`
- [ ] T023 [US2] Integrate responsive `AppHeader` into remaining pages: `PlanListScreen.kt`, `HealthRecordListScreen.kt`, `GrowthCurveScreen.kt`, `InventoryListScreen.kt`
- [ ] T024 [US2] Integrate responsive `AppHeader` into AI pages: `AiSettingsScreen.kt`, `RecommendationScreen.kt`
- [ ] T025 [US2] Integrate responsive `AppHeader` into auth pages: `LoginScreen.kt`, `RegisterScreen.kt` (with special behavior)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - Header displays on all 10+ pages with responsive layout

---

## Phase 5: User Story 3 - Header 交互行为 (Priority: P3)

**Goal**: Header 中的所有交互元素提供一致的反馈和行为（用户菜单、网络失败处理、无障碍访问、登录/注册页面特殊行为）

**Independent Test**: 点击 Header 中的所有交互元素（应用名称、登录按钮、用户头像、重试按钮），验证交互行为正确且符合 Material Design 规范

### Implementation for User Story 3

- [ ] T026 [P] [US3] Create `UserAvatarMenu` composable in `app/src/main/java/com/example/babyfood/presentation/ui/common/UserAvatarMenu.kt` using `DropdownMenu`
- [ ] T027 [P] [US3] Add menu items to `UserAvatarMenu.kt`: 退出登录、个人设置、切换宝宝
- [ ] T028 [US3] Implement "切换宝宝" logic in `UserAvatarMenu.kt` to check baby count and show toast/dialog if only one baby
- [ ] T029 [US3] Add network error handling to `LoginStateIndicator.kt` with `lastKnownState` and retry button
- [ ] T030 [US3] Add `LaunchedEffect` in `LoginStateIndicator.kt` for automatic auth state loading
- [ ] T031 [US3] Add retry functionality to `LoginStateIndicator.kt` when retry button is clicked
- [ ] T032 [US3] Add contentDescription to all interactive elements in `AppHeader.kt` for screen reader support
- [ ] T033 [US3] Add semantics with Role to buttons in `LoginButton.kt`, `RegisterButton.kt`, `UserAvatarMenu.kt`
- [ ] T034 [US3] Add focusable modifiers to buttons for keyboard navigation support
- [ ] T035 [US3] Add `sizeIn(minWidth = 48.dp, minHeight = 48.dp)` to all touch targets in `AppHeader.kt` components
- [ ] T036 [US3] Verify high contrast mode support in `AppHeader.kt` (ensure text/background contrast >= 4.5:1)
- [ ] T037 [US3] Add visual feedback (ripple effect) to buttons in `LoginButton.kt`, `RegisterButton.kt`, `UserAvatarMenu.kt`
- [ ] T038 [US3] Implement login/register page special behavior in `AppHeader.kt` based on `currentRoute`
- [ ] T039 [US3] Add "注册" button display logic for login page in `AppHeader.kt`
- [ ] T040 [US3] Add "登录" button display logic for register page in `AppHeader.kt`
- [ ] T041 [US3] Configure app name click navigation to home route in `AppHeader.kt`
- [ ] T042 [US3] Update all pages to pass correct `currentRoute` to `AppHeader` component

**Checkpoint**: All user stories should now be independently functional - Header has full interactivity, network handling, accessibility support, and special page behaviors

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T043 [P] Create unit tests for `AppHeaderConfig.kt` in `app/src/test/java/com/example/babyfood/presentation/ui/common/AppHeaderConfigTest.kt`
- [ ] T044 [P] Create unit tests for `UserMenuItem.kt` in `app/src/test/java/com/example/babyfood/presentation/ui/common/UserMenuItemTest.kt`
- [ ] T045 [P] Create unit tests for `UserMenuConfig.kt` in `app/src/test/java/com/example/babyfood/presentation/ui/common/UserMenuConfigTest.kt`
- [ ] T046 [P] Create UI tests for `AppHeader.kt` in `app/src/androidTest/java/com/example/babyfood/presentation/ui/common/AppHeaderUiTest.kt`
- [ ] T047 [P] Create UI tests for `UserAvatarMenu.kt` in `app/src/androidTest/java/com/example/babyfood/presentation/ui/common/UserAvatarMenuUiTest.kt`
- [ ] T048 Code cleanup and refactoring - remove unused imports and optimize Composable functions
- [ ] T049 Verify performance - Header render time should be < 0.5s on all devices
- [ ] T050 Verify accessibility - run accessibility scanner and fix any issues
- [ ] T051 Run quickstart.md validation - ensure all steps in quickstart.md work correctly
- [ ] T052 Update `IFLOW.md` with new `AppHeader` component documentation
- [ ] T053 Add inline code comments for complex logic in `LoginStateIndicator.kt` and `UserAvatarMenu.kt`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion (builds on AppHeader)
- **User Story 3 (Phase 5)**: Depends on User Story 2 completion (builds on all pages integration)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Depends on User Story 1 - Extends AppHeader with responsive features
- **User Story 3 (P3)**: Depends on User Story 2 - Adds interactivity to all integrated pages

### Within Each User Story

- Models/Config → Components → Integration
- Basic components before advanced features
- Core implementation before edge cases

### Parallel Opportunities

- All Setup tasks (T001-T003) can run in parallel
- All Foundational data model tasks (T004-T006) can run in parallel
- All button components in US1 (T007-T008) can run in parallel
- All page integrations in US1 (T014-T017) can run in parallel
- All page integrations in US2 (T023-T025) can run in parallel
- All basic menu components in US3 (T026-T027) can run in parallel
- All accessibility tasks in US3 (T032-T035) can run in parallel
- All tests in Phase 6 (T043-T047) can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all button components together:
Task: "Create LoginButton composable in app/src/main/java/com/example/babyfood/presentation/ui/common/LoginButton.kt"
Task: "Create RegisterButton composable in app/src/main/java/com/example/babyfood/presentation/ui/common/RegisterButton.kt"

# Launch all page integrations together:
Task: "Integrate AppHeader into TodayMenuScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/home/components/TodayMenuScreen.kt"
Task: "Integrate AppHeader into RecipesListScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/recipes/RecipesListScreen.kt"
Task: "Integrate AppHeader into BabyListScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/BabyListScreen.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T006) - CRITICAL
3. Complete Phase 3: User Story 1 (T007-T017)
4. **STOP and VALIDATE**: Test Header on 3 pages (home, recipes, baby)
5. Demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test on 3 pages → Demo (MVP!)
3. Add User Story 2 → Test responsive layout on all pages → Demo
4. Add User Story 3 → Test full interactivity → Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (AppHeader + 3 pages)
   - Developer B: User Story 2 (Responsive + remaining pages)
   - Developer C: User Story 3 (Interactivity + polish)
3. Stories complete and integrate sequentially (US1 → US2 → US3)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- User stories have dependencies: US1 → US2 → US3 (cannot run in parallel)
- Each user story phase should be a complete, independently testable increment
- US1 is MVP - delivers value with basic Header on 3 pages
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, skipping foundational phase