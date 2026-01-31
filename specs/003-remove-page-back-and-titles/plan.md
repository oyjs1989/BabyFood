# Implementation Plan: 移除页面返回和页面名称

**Branch**: `003-remove-page-back-and-titles` | **Date**: 2026-01-31 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-remove-page-back-and-titles/spec.md`

## Summary

Remove the page title bar (AppTopAppBar) from all pages using AppScaffold to free up ~64dp of screen space for content and simplify the navigation model. Users will navigate using the global Header ("BabyFood" logo) to return home, bottom navigation bar for main sections, and auto-navigation after form completion. Deep nested pages will auto-return to parent lists after actions, unsaved changes will trigger a confirmation dialog, and navigation works normally in offline mode.

**Technical Approach**:
- Refactor AppScaffold to remove topBar parameter and AppBarConfig dependency
- Update all ~20+ screens using AppScaffold to remove appBarConfig usage
- Enhance AppHeader navigation for home navigation
- Implement form auto-navigation logic
- Add unsaved changes confirmation dialog

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose 1.6+, Material Design 3, AndroidX Navigation Compose, Hilt
**Storage**: Room Database (SQLite) - no changes required
**Testing**: JUnit 4.13, Compose Testing, Espresso
**Target Platform**: Android 7.0+ (API 24-34)
**Project Type**: Mobile (Android)
**Performance Goals**: Navigation transitions <200ms, form save + auto-navigate <1s (SC-003)
**Constraints**: Android system UI guidelines, Material Design 3 compliance, offline-capable navigation
**Scale/Scope**: ~20+ screen files affected, ~5 components modified (AppScaffold, AppTopAppBar, AppBarConfig, AppHeader, Form screens)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Core Principles Assessment

| Principle | Status | Notes |
|-----------|--------|-------|
| Test-First | ⚠️ Deferred | Test strategy to be defined in Phase 2 (tasks.md) |
| Simplicity | ✅ Pass | Removes components, simplifies navigation model |
| Consistency | ✅ Pass | Unified navigation via Header + Bottom Nav |

### Quality Gates

| Gate | Status | Notes |
|------|--------|-------|
| No breaking changes without migration | ❌ Violation | Breaking change for all AppScaffold users (~20+ screens) |
| All changes backward compatible | ❌ Violation | AppBarConfig parameter removal is breaking |
| Clear migration path | ⚠️ Deferred | Migration strategy to be documented in tasks.md |

**Rationale for Violations**:
- **Breaking Change**: This is a UX overhaul that fundamentally changes the navigation model. Cannot be made backward compatible without defeating the purpose (removing title bar).
- **Migration**: All affected screens must be updated simultaneously as part of a single feature branch. Cannot be gradual.

**Mitigation**:
- Comprehensive testing before merge
- Update all screens in one commit
- Document breaking change in release notes

## Project Structure

### Documentation (this feature)

```
specs/003-remove-page-back-and-titles/
├── plan.md              # This file
├── research.md          # Phase 0 output (✅ Complete)
├── data-model.md        # Phase 1 output (✅ Complete)
├── quickstart.md        # Phase 1 output (✅ Complete)
├── contracts/           # Phase 1 output (N/A - no new API contracts)
└── tasks.md             # Phase 2 output (to be created by /speckit.tasks)
```

### Source Code (repository root)

```
app/src/main/java/com/example/babyfood/
├── presentation/
│   ├── ui/
│   │   ├── common/
│   │   │   ├── AppScaffold.kt          # MODIFIED: Remove topBar parameter
│   │   │   ├── AppTopAppBar.kt         # MODIFIED: Mark as deprecated or remove
│   │   │   ├── AppBarConfig.kt         # MODIFIED: Mark as deprecated or remove
│   │   │   └── AppHeader.kt            # MODIFIED: Ensure home navigation works
│   │   ├── baby/
│   │   │   ├── BabyDetailScreen.kt     # MODIFIED: Remove appBarConfig
│   │   │   ├── BabyFormScreen.kt       # MODIFIED: Remove appBarConfig, add auto-nav
│   │   │   ├── BabyListScreen.kt       # MODIFIED: Verify no AppScaffold usage
│   │   │   ├── AllergyManagementScreen.kt    # MODIFIED: Remove appBarConfig
│   │   │   └── PreferenceManagementScreen.kt # MODIFIED: Remove appBarConfig
│   │   ├── recipes/
│   │   │   ├── RecipeDetailScreen.kt   # MODIFIED: Remove appBarConfig
│   │   │   ├── RecipeFormScreen.kt     # MODIFIED: Remove appBarConfig, add auto-nav
│   │   │   └── RecipesListScreen.kt    # MODIFIED: Verify no AppScaffold usage
│   │   ├── plans/
│   │   │   ├── PlanDetailScreen.kt     # MODIFIED: Remove appBarConfig
│   │   │   ├── PlanFormScreen.kt       # MODIFIED: Remove appBarConfig, add auto-nav
│   │   │   ├── PlanListScreen.kt       # MODIFIED: Verify no AppScaffold usage
│   │   │   └── RecommendationEditorScreen.kt # MODIFIED: Remove appBarConfig
│   │   ├── health/
│   │   │   ├── HealthRecordListScreen.kt   # MODIFIED: Remove appBarConfig
│   │   │   └── HealthRecordFormScreen.kt   # MODIFIED: Remove appBarConfig, add auto-nav
│   │   ├── growth/
│   │   │   └── GrowthCurveScreen.kt    # MODIFIED: Remove appBarConfig
│   │   ├── inventory/
│   │   │   ├── InventoryListScreen.kt  # MODIFIED: Verify no AppScaffold usage
│   │   │   └── InventoryFormScreen.kt  # MODIFIED: Remove appBarConfig, add auto-nav
│   │   ├── ai/
│   │   │   ├── AiSettingsScreen.kt     # MODIFIED: Remove appBarConfig
│   │   │   └── RecommendationScreen.kt # MODIFIED: Remove appBarConfig
│   │   └── auth/
│   │       ├── LoginScreen.kt          # MODIFIED: Remove appBarConfig
│   │       └── RegisterScreen.kt      # MODIFIED: Remove appBarConfig
│   └── MainScreen.kt                  # MODIFIED: Verify Header navigation
```

**Structure Decision**: Mobile Android app using Jetpack Compose. All modifications are within the existing `presentation/ui` structure. No new directories or modules added.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Breaking change for ~20+ screens | UX overhaul requires removing title bar from all pages simultaneously | Gradual migration would leave inconsistent navigation states, confusing users |
| Form auto-navigation logic | Required for smooth UX without back button | Manual navigation would add friction, violating success criteria (SC-003) |
| Unsaved changes confirmation | Prevents data loss when navigating via Header | Auto-saving would complicate state management and not address all scenarios |

## Implementation Phases

### Phase 0: Research ✅ Complete
- [x] Analyzed current AppScaffold, AppTopAppBar, AppBarConfig implementation
- [x] Resolved all NEEDS CLARIFICATION in spec
- [x] Research findings documented in [research.md](./research.md)

### Phase 1: Design & Contracts ✅ Complete
- [x] Data model documented in [data-model.md](./data-model.md)
- [x] Quickstart guide created in [quickstart.md](./quickstart.md)
- [x] No new API contracts required (internal UI refactoring only)
- [x] Agent context updated

### Phase 2: Task Decomposition (Pending)
- Generate detailed task breakdown in `tasks.md` using `/speckit.tasks` command
- Include migration strategy for all 20+ screens
- Define test cases for navigation flows
- Define rollback plan if issues arise

## Dependencies

### Internal Dependencies
- AppHeader component (must support home navigation)
- NavController (MainScreen navigation system)
- All screens using AppScaffold (~20+ files)

### External Dependencies
- Jetpack Compose 1.6+ (already in use)
- Material Design 3 (already in use)
- AndroidX Navigation Compose (already in use)

### No new dependencies required

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| User confusion about navigation | High | Clear affordances in Header + Bottom Nav, user education if needed |
| Regression in navigation flows | Medium | Comprehensive testing of all affected screens |
| Deep navigation edge cases | Medium | Auto-return to parent list implemented (FR-010) |
| Data loss from unsaved changes | Medium | Confirmation dialog implemented (FR-011) |

## Success Criteria (from spec)

- **SC-001**: All sub-page content area height increases by ~64dp
- **SC-002**: Users can return to home via Header in 3 seconds
- **SC-003**: System auto-returns to list within 1 second after form save
- **SC-004**: User satisfaction improves by 15% (measured post-release)

## Next Steps

1. Execute `/speckit.tasks` to generate detailed task breakdown
2. Review and approve task list
3. Begin implementation following task order
4. Execute comprehensive testing before merge
5. Update release notes with breaking change information