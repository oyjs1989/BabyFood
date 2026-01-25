# Tasks: 修复登录按钮加载动画

**Input**: Design documents from `/specs/001-fix-login-loading-animation/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Tests are OPTIONAL - not explicitly requested in feature specification

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions
- **Mobile (Android)**: `app/src/main/java/com/example/babyfood/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify existing project structure and dependencies

- [ ] T001 Verify project structure exists at `app/src/main/java/com/example/babyfood/`
- [ ] T002 Verify Kotlin 2.0.21 and Jetpack Compose dependencies are configured in `app/build.gradle.kts`
- [ ] T003 Verify Material Design 3 dependencies are available

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 Verify NetworkModule.kt has OkHttp timeout configuration (connect: 10s, read: 30s) in `app/src/main/java/com/example/babyfood/di/NetworkModule.kt`
- [ ] T005 Verify LoginViewModel.kt has LoginUiState with isLoading field in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`
- [ ] T006 Verify LoginScreen.kt uses Button component with enabled state in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 登录加载状态显示 (Priority: P1) 🎯 MVP

**Goal**: 修复登录按钮在加载状态下显示不清晰的问题，改为显示清晰的 24dp 白色旋转圆形进度指示器

**Independent Test**: 在登录页面输入有效账号密码后点击登录按钮，观察按钮上显示的加载动画是否清晰易懂（白色旋转圆形进度指示器 24dp）

### Implementation for User Story 1

- [ ] T007 [US1] Add BackHandler import to LoginScreen.kt in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T008 [US1] Add BackHandler component in LoginScreen to intercept back button when isLoading is true in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T009 [US1] Update CircularProgressIndicator in login button to use 24dp size, white color, 2dp strokeWidth in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T010 [US1] Add semantics modifier with contentDescription "登录中" to CircularProgressIndicator for accessibility in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T011 [US1] Add cancelLogin() method to LoginViewModel to reset isLoading state in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`
- [ ] T012 [US1] Add SocketTimeoutException and TimeoutException catch blocks in login() method with user-friendly error message "请求超时，请检查网络后重试" in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`
- [ ] T013 [US1] Add IOException catch block in login() method with error message "网络错误，请检查网络连接" in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`
- [ ] T014 [US1] Add necessary imports (SocketTimeoutException, TimeoutException, IOException) to LoginViewModel.kt in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`
- [ ] T015 [US1] Add logging for cancelLogin operation in LoginViewModel.kt in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginViewModel.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 加载动画视觉一致性 (Priority: P2)

**Goal**: 确保登录按钮的加载动画与应用整体设计风格保持一致，使用标准的 Material Design 组件

**Independent Test**: 对比登录按钮的加载动画与其他页面（如注册页面）的加载动画，确认它们使用相同的设计风格

### Implementation for User Story 2

- [ ] T016 [US2] Verify CircularProgressIndicator uses Material Design 3 standard component in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T017 [US2] Verify loading animation color (white) matches app theme Primary color contrast in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T018 [US2] Verify loading animation size (24dp) is consistent with other loading indicators in the app in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - 加载状态可访问性 (Priority: P3)

**Goal**: 确保加载状态对视觉障碍用户友好，提供额外的辅助信息

**Independent Test**: 启用屏幕阅读器（如 TalkBack），测试加载状态是否有适当的辅助文本描述

### Implementation for User Story 3

- [ ] T019 [US3] Verify contentDescription "登录中" is properly set on CircularProgressIndicator in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T020 [US3] Verify button color contrast meets WCAG AA standards (4.5:1) in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`
- [ ] T021 [US3] Verify button disabled state is visually distinct from loading state in `app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T022 Manual test: Verify loading animation displays correctly when clicking login button
- [ ] T023 Manual test: Verify button is disabled during loading state
- [ ] T024 Manual test: Verify loading animation disappears after login success/failure
- [ ] T025 Manual test: Verify timeout error displays correctly when network is disconnected
- [ ] T026 Manual test: Verify back button cancels login request during loading state
- [ ] T027 Manual test: Verify screen reader announces "登录中" when loading state is active
- [ ] T028 Run app build to verify no compilation errors

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can proceed sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after User Story 1 completion - Verifies consistency
- **User Story 3 (P3)**: Can start after User Story 1 completion - Verifies accessibility

### Within Each User Story

- T007-T008 (US1): Sequential - BackHandler import before usage
- T009-T010 (US1): Sequential - Update component before adding semantics
- T011-T015 (US1): Sequential - Add method before adding catch blocks and imports
- T016-T018 (US2): [P] - Can run in parallel (verification tasks)
- T019-T021 (US3): [P] - Can run in parallel (verification tasks)
- T022-T028 (Polish): [P] - Can run in parallel (manual tests)

### Parallel Opportunities

- T016-T018 (US2): All verification tasks can run in parallel
- T019-T021 (US3): All verification tasks can run in parallel
- T022-T028 (Polish): All manual tests can run in parallel

---

## Parallel Example: User Story 1

```bash
# Sequential execution for User Story 1 (no parallel opportunities):
Task: "Add BackHandler import to LoginScreen.kt"
Task: "Add BackHandler component in LoginScreen"
Task: "Update CircularProgressIndicator with 24dp size, white color"
Task: "Add semantics modifier with contentDescription"
Task: "Add cancelLogin() method to LoginViewModel"
Task: "Add SocketTimeoutException and TimeoutException catch blocks"
Task: "Add IOException catch block"
Task: "Add necessary imports"
Task: "Add logging for cancelLogin operation"
```

---

## Parallel Example: User Story 2 (Verification)

```bash
# Launch all verification tasks for User Story 2 together:
Task: "Verify CircularProgressIndicator uses Material Design 3 standard component"
Task: "Verify loading animation color matches app theme"
Task: "Verify loading animation size is consistent with other indicators"
```

---

## Parallel Example: Polish Phase

```bash
# Launch all manual tests together:
Task: "Manual test: Verify loading animation displays correctly"
Task: "Manual test: Verify button is disabled during loading"
Task: "Manual test: Verify loading animation disappears after login"
Task: "Manual test: Verify timeout error displays correctly"
Task: "Manual test: Verify back button cancels login"
Task: "Manual test: Verify screen reader announces loading state"
Task: "Run app build to verify no compilation errors"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T006) - CRITICAL
3. Complete Phase 3: User Story 1 (T007-T015)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - Manual test: Loading animation displays correctly
   - Manual test: Button disabled during loading
   - Manual test: Back button cancels login
   - Manual test: Timeout error displays correctly
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (T001-T006)
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!) (T007-T015)
3. Add User Story 2 → Test independently → Deploy/Demo (T016-T018)
4. Add User Story 3 → Test independently → Deploy/Demo (T019-T021)
5. Complete Polish → Final validation (T022-T028)

### Sequential Execution (Single Developer)

1. T001-T003: Setup
2. T004-T006: Foundational
3. T007-T015: User Story 1 (P1) - MVP
4. T016-T018: User Story 2 (P2) - Verification
5. T019-T021: User Story 3 (P3) - Accessibility
6. T022-T028: Polish and testing

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- User Story 1 is the MVP and delivers core value
- User Stories 2 and 3 are verification and enhancement tasks
- Manual tests are critical for UI validation
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence

---

## Summary

- **Total Tasks**: 28
- **Setup Tasks**: 3
- **Foundational Tasks**: 3
- **User Story 1 Tasks**: 9 (P1 - MVP)
- **User Story 2 Tasks**: 3 (P2 - Verification)
- **User Story 3 Tasks**: 3 (P3 - Accessibility)
- **Polish Tasks**: 7
- **Parallel Opportunities**: 10 tasks can run in parallel
- **MVP Scope**: User Story 1 (T007-T015) - delivers core loading animation fix