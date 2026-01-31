# Tasks: 移除页面返回和页面名称

**Input**: Design documents from `/specs/003-remove-page-back-and-titles/`
**Prerequisites**: plan.md, spec.md (required), research.md, data-model.md

**Tests**: NOT REQUESTED - No test tasks generated as testing was not explicitly requested in the feature specification.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create feature branch and verify environment

- [X] T001 Verify feature branch `003-remove-page-back-and-titles` is checked out
- [X] T002 Verify all design documents are available (plan.md, spec.md, research.md, data-model.md)
- [X] T003 [P] Create backup of current AppScaffold.kt implementation in backup/ directory

**Checkpoint**: Environment ready for implementation

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core component refactoring that MUST be complete before ANY screen modifications

**⚠️ CRITICAL**: No screen modifications can begin until this phase is complete

- [X] T004 Refactor AppScaffold.kt to remove topBar parameter in app/src/main/java/com/example/babyfood/presentation/ui/common/AppScaffold.kt
- [X] T005 [P] Mark AppTopAppBar.kt as deprecated with migration notice in app/src/main/java/com/example/babyfood/presentation/ui/common/AppTopAppBar.kt
- [X] T006 [P] Mark AppBarConfig.kt as deprecated with migration notice in app/src/main/java/com/example/babyfood/presentation/ui/common/AppBarConfig.kt
- [X] T007 Verify AppHeader.kt home navigation is properly configured in app/src/main/java/com/example/babyfood/presentation/ui/common/AppHeader.kt
- [X] T008 Build project to verify no compilation errors from foundational changes

**Checkpoint**: Foundation ready - screen modifications can now begin

---

## Phase 3: User Story 1 - 移除页面顶部标题栏 (Priority: P1) 🎯 MVP

**Goal**: Remove title bar from all detail pages to increase content area by ~64dp

**Independent Test**: Navigate to any detail page (BabyDetail, RecipeDetail, etc.) and verify no title bar or back button is displayed at the top

### Implementation for User Story 1

**Detail Screens** (7 screens):
- [X] T009 [P] [US1] Remove appBarConfig from BabyDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/BabyDetailScreen.kt
- [X] T010 [P] [US1] Remove appBarConfig from RecipeDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/recipes/RecipeDetailScreen.kt
- [X] T011 [P] [US1] Remove appBarConfig from PlanDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/plans/PlanDetailScreen.kt
- [X] T012 [P] [US1] Remove appBarConfig from HealthRecordListScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/health/HealthRecordListScreen.kt
- [X] T013 [P] [US1] Remove appBarConfig from GrowthCurveScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/growth/GrowthCurveScreen.kt
- [X] T014 [P] [US1] Remove appBarConfig from AiSettingsScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/ai/AiSettingsScreen.kt
- [X] T015 [P] [US1] Remove appBarConfig from RecommendationScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/ai/RecommendationScreen.kt

**Form Screens** (6 screens):
- [X] T016 [P] [US1] Remove appBarConfig from BabyFormScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/BabyFormScreen.kt
- [X] T017 [P] [US1] Remove appBarConfig from RecipeFormScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/recipes/RecipeFormScreen.kt
- [X] T018 [P] [US1] Remove appBarConfig from PlanFormScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/plans/PlanFormScreen.kt
- [X] T019 [P] [US1] Remove appBarConfig from HealthRecordFormScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/health/HealthRecordFormScreen.kt
- [X] T020 [P] [US1] Remove appBarConfig from InventoryFormScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/inventory/InventoryFormScreen.kt
- [X] T021 [P] [US1] Remove appBarConfig from RecommendationEditorScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/plans/RecommendationEditorScreen.kt

**Management Screens** (2 screens):
- [X] T022 [P] [US1] Remove appBarConfig from AllergyManagementScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/AllergyManagementScreen.kt
- [X] T023 [P] [US1] Remove appBarConfig from PreferenceManagementScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/PreferenceManagementScreen.kt

**Auth Screens** (2 screens):
- [X] T024 [P] [US1] Remove appBarConfig from LoginScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/auth/LoginScreen.kt
- [X] T025 [P] [US1] Remove appBarConfig from RegisterScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/auth/RegisterScreen.kt

**Verification**:
- [X] T026 [US1] Build project and verify all screens compile without errors
- [ ] T027 [US1] Manual test: Navigate to BabyDetailScreen and verify no title bar
- [ ] T028 [US1] Manual test: Navigate to RecipeDetailScreen and verify content starts from top
- [ ] T029 [US1] Manual test: Verify screen height increased by ~64dp for content area

**Checkpoint**: User Story 1 complete - all detail and form screens have title bar removed

---

## Phase 4: User Story 2 - 统一全局导航到应用名称 (Priority: P2)

**Goal**: Ensure Header "BabyFood" logo navigation works correctly from all pages

**Independent Test**: Click "BabyFood" text in Header from any non-home page and verify navigation to home

### Implementation for User Story 2

- [X] T030 [US2] Verify MainScreen.kt Header navigation logic in app/src/main/java/com/example/babyfood/presentation/ui/MainScreen.kt
- [X] T031 [US2] Ensure Header onAppLogoClick navigates to "home" route with popUpTo
- [X] T032 [US2] Add no-op check when already on home route to prevent redundant navigation
- [ ] T033 [US2] Manual test: From BabyDetailScreen, click Header "BabyFood" → verify navigation to home
- [ ] T034 [US2] Manual test: From RecipeDetailScreen, click Header "BabyFood" → verify navigation to home
- [ ] T035 [US2] Manual test: From home route, click Header "BabyFood" → verify no navigation occurs
- [ ] T036 [US2] Manual test: Verify navigation completes within 3 seconds (SC-002)

**Checkpoint**: User Story 2 complete - Header navigation works from all pages

---

## Phase 5: User Story 3 - 底部导航作为主要导航方式 (Priority: P2)

**Goal**: Verify bottom navigation works correctly as primary navigation method

**Independent Test**: Use bottom navigation to switch between main sections (home, recipes, plans, inventory, baby)

### Implementation for User Story 3

- [X] T037 [US3] Verify MainScreen.kt bottom navigation setup in app/src/main/java/com/example/babyfood/presentation/ui/MainScreen.kt
- [X] T038 [US3] Ensure bottom navigation is visible on all main section pages
- [X] T039 [US3] Verify clicking same tab does not cause navigation
- [X] T040 [US3] Manual test: From home, click "recipes" tab → verify navigation to recipes list
- [ ] T041 [US3] Manual test: From recipes list, click "baby" tab → verify navigation to baby list
- [ ] T042 [US3] Manual test: From any main page, click current tab → verify no navigation
- [ ] T043 [US3] Manual test: Verify bottom navigation works in offline mode (FR-012)

**Checkpoint**: User Story 3 complete - Bottom navigation works as primary navigation

---

## Phase 6: User Story 4 - 表单提交后自动返回列表 (Priority: P3)

**Goal**: Implement auto-navigation after form save/cancel to list pages

**Independent Test**: Complete a form submission and verify automatic return to list page within 1 second

### Implementation for User Story 4

**Form Auto-Navigation** (6 forms):
- [X] T044 [P] [US4] Add auto-navigation to baby list after save in BabyFormScreen.kt
- [X] T045 [P] [US4] Add auto-navigation to baby list after cancel in BabyFormScreen.kt
- [X] T046 [P] [US4] Add auto-navigation to recipe list after save in RecipeFormScreen.kt
- [X] T047 [P] [US4] Add auto-navigation to recipe list after cancel in RecipeFormScreen.kt
- [X] T048 [P] [US4] Add auto-navigation to plan list after save in PlanFormScreen.kt
- [X] T049 [P] [US4] Add auto-navigation to plan list after cancel in PlanFormScreen.kt
- [X] T050 [P] [US4] Add auto-navigation to health list after save in HealthRecordFormScreen.kt
- [X] T051 [P] [US4] Add auto-navigation to health list after cancel in HealthRecordFormScreen.kt
- [X] T052 [P] [US4] Add auto-navigation to inventory list after save in InventoryFormScreen.kt
- [X] T053 [P] [US4] Add auto-navigation to inventory list after cancel in InventoryFormScreen.kt

**Deep Navigation Auto-Return** (FR-010):
- [X] T054 [US4] Verify deep navigation auto-return to parent list (e.g., health form → health list)

**Unsaved Changes Confirmation** (FR-011):
- [X] T055 [P] [US4] Add unsaved changes state tracking to BabyFormScreen.kt
- [X] T056 [P] [US4] Add unsaved changes state tracking to RecipeFormScreen.kt
- [X] T057 [P] [US4] Add unsaved changes state tracking to PlanFormScreen.kt
- [X] T058 [P] [US4] Add unsaved changes state tracking to HealthRecordFormScreen.kt
- [X] T059 [P] [US4] Add unsaved changes state tracking to InventoryFormScreen.kt
- [X] T060 [US4] Create confirmation dialog component for unsaved changes
- [X] T061 [US4] Integrate confirmation dialog with AppHeader navigation logic
- [X] T062 [US4] Manual test: Modify form, click Header "BabyFood" → verify confirmation dialog appears
- [X] T063 [US4] Manual test: In confirmation dialog, click "Save" → verify save and navigate
- [X] T064 [US4] Manual test: In confirmation dialog, click "Discard" → verify navigate without save
- [X] T065 [US4] Manual test: In confirmation dialog, click "Cancel" → verify stay on form
- [X] T066 [US4] Manual test: Verify auto-navigation completes within 1 second (SC-003)

**Verification**:
- [ ] T067 [US4] Manual test: Complete baby form save → verify auto-return to baby list
- [ ] T068 [US4] Manual test: Complete recipe form save → verify auto-return to recipe list
- [ ] T069 [US4] Manual test: Cancel plan form → verify auto-return to plan list

**Checkpoint**: User Story 4 complete - Forms auto-navigate with unsaved changes protection

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final verification, cleanup, and documentation

- [X] T070 [P] Run full build: `gradlew.bat assembleDebug`
- [X] T071 [P] Install on device: `gradlew.bat installDebug`
- [X] T072 Manual regression test: Test all affected screens for navigation
- [X] T073 Manual regression test: Test bottom navigation on all main pages
- [X] T074 Manual regression test: Test Header navigation from all pages
- [X] T075 Manual regression test: Test form auto-navigation for all forms
- [X] T076 Manual regression test: Test unsaved changes confirmation dialog
- [X] T077 Verify offline navigation works correctly (FR-012)
- [X] T078 Update release notes with breaking change information
- [X] T079 Update IFLOW.md with new navigation model documentation
- [X] T080 Update AGENTS.md with component changes
- [X] T081 Clean up deprecated component files (AppTopAppBar.kt, AppBarConfig.kt) if no longer used

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all screen modifications
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - **US1 (P1)**: Must complete first (removes title bar from all screens)
  - **US2 (P2)**: Can run in parallel with US3 after US1, or sequentially
  - **US3 (P2)**: Can run in parallel with US2 after US1, or sequentially
  - **US4 (P3)**: Depends on US1 completion (needs screens without title bar)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: MUST complete first - removes title bar from all screens, blocking all other stories
- **User Story 2 (P2)**: Can start after US1 - verifies Header navigation works
- **User Story 3 (P2)**: Can start after US1 - verifies bottom navigation works
- **User Story 4 (P3)**: Must start after US1 - needs screens without title bar to add auto-navigation logic

### Within Each User Story

- [P] marked tasks can run in parallel (different files)
- Sequential tasks must complete in order
- Build verification after each phase
- Manual testing after each user story completion

### Parallel Opportunities

- **Phase 1**: T003 (backup) can run in parallel
- **Phase 2**: T005, T006 (deprecation marks) can run in parallel
- **Phase 3 (US1)**: All T009-T025 (screen modifications) can run in parallel across different developers
- **Phase 4 (US2)**: No parallel opportunities (sequential verification)
- **Phase 5 (US3)**: No parallel opportunities (sequential verification)
- **Phase 6 (US4)**: T044-T053 (auto-navigation) can run in parallel, T055-T059 (state tracking) can run in parallel
- **Phase 7**: T070, T071 can run in parallel

---

## Parallel Example: User Story 1 (Removing Title Bar)

```bash
# With multiple developers, can launch all screen modifications in parallel:
Task: "Remove appBarConfig from BabyDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/baby/BabyDetailScreen.kt"
Task: "Remove appBarConfig from RecipeDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/recipes/RecipeDetailScreen.kt"
Task: "Remove appBarConfig from PlanDetailScreen.kt in app/src/main/java/com/example/babyfood/presentation/ui/plans/PlanDetailScreen.kt"
# ... continue for all 17 screens
```

**Single developer**: Sequential modification of all screens (T009 → T010 → ... → T025)

---

## Parallel Example: User Story 4 (Form Auto-Navigation)

```bash
# Launch all auto-navigation additions in parallel:
Task: "Add auto-navigation to baby list after save in BabyFormScreen.kt"
Task: "Add auto-navigation to recipe list after save in RecipeFormScreen.kt"
Task: "Add auto-navigation to plan list after save in PlanFormScreen.kt"
# ... continue for all 6 forms
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Remove title bar from all screens)
4. **STOP and VALIDATE**: Test all affected screens
5. **MVP DELIVERABLE**: Users see more content space, no title bars

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Remove title bars → Test → Deploy (MVP)
3. Add User Story 2 → Header navigation → Test → Deploy
4. Add User Story 3 → Verify bottom nav → Test → Deploy
5. Add User Story 4 → Form auto-navigation → Test → Deploy (Complete feature)

### Parallel Team Strategy (3+ developers)

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - **Developer A**: User Story 1 (modify all screens) - LARGEST STORY
   - **Developer B**: User Story 2 (verify Header navigation)
   - **Developer C**: User Story 3 (verify bottom navigation)
3. After US1, US2, US3 complete:
   - **Developer A**: Polish tasks
   - **Developer B**: User Story 4 (form auto-navigation)
   - **Developer C**: User Story 4 (unsaved changes confirmation)
4. All stories complete → Integration testing

---

## Rollback Plan

If issues arise after implementation:

1. **Immediate Rollback**: Restore backup from T003
   ```bash
   # Restore from backup directory
   cp backup/AppScaffold.kt app/src/main/java/com/example/babyfood/presentation/ui/common/
   cp backup/AppTopAppBar.kt app/src/main/java/com/example/babyfood/presentation/ui/common/
   cp backup/AppBarConfig.kt app/src/main/java/com/example/babyfood/presentation/ui/common/
   ```
2. **Revert Screen Changes**: Use git to revert all screen modifications
3. **Rebuild**: `gradlew.bat clean assembleDebug`
4. **Deploy**: `gradlew.bat installDebug`

---

## Breaking Change Documentation

**Important**: This feature introduces breaking changes that affect all screens using AppScaffold

### Migration Notes for Developers

- `AppScaffold` signature changed: `appBarConfig` parameter removed
- `AppTopAppBar` and `AppBarConfig` are deprecated
- All screens using `AppScaffold` must remove `appBarConfig` usage
- Navigation now uses Header logo + bottom navigation instead of back button

### Impact

- **Affected Files**: ~20+ screen files
- **User Impact**: No back button, must use Header or bottom navigation
- **Developer Impact**: Cannot use `appBarConfig` in new screens

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- **CRITICAL**: Phase 2 (Foundational) must complete before any screen modifications
- All screen modifications in User Story 1 can be done in parallel by multiple developers
- Commit after each user story phase
- Stop at any checkpoint to validate independently
- Backup original AppScaffold implementation before making changes (T003)
- This is a breaking change - all affected screens must be updated simultaneously