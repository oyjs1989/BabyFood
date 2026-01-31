# Tasks: Apply BabyFood UI Design System

**Input**: Design documents from `/specs/004-apply-ui-design-system/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included in this feature specification (SC-001 through SC-012 require validation).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions
- **Mobile Android**: `app/src/main/java/com/example/babyfood/` (source), `app/src/test/java/com/example/babyfood/` (unit tests), `app/src/androidTest/java/com/example/babyfood/` (integration tests)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and theme directory structure

- [X] T001 Create theme directory structure in app/src/main/java/com/example/babyfood/presentation/theme/
- [X] T002 Create components subdirectory in app/src/main/java/com/example/babyfood/presentation/theme/components/
- [X] T003 [P] Create test directories for theme tests in app/src/test/java/com/example/babyfood/theme/ and app/src/androidTest/java/com/example/babyfood/theme/

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core theme infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 Create Color.kt with BabyFoodColors data class defining complete color palette for light and dark modes in app/src/main/java/com/example/babyfood/presentation/theme/Color.kt
- [X] T005 [P] Create Type.kt with Typography system defining font sizes, weights, line heights, and character spacing in app/src/main/java/com/example/babyfood/presentation/theme/Type.kt
- [X] T006 [P] Create Shape.kt with corner radius specifications for all component types in app/src/main/java/com/example/babyfood/presentation/theme/Shape.kt
- [X] T007 [P] Create Elevation.kt with shadow and elevation hierarchy levels in app/src/main/java/com/example/babyfood/presentation/theme/Elevation.kt
- [X] T008 [P] Create Animation.kt with animation timing and duration specifications in app/src/main/java/com/example/babyfood/presentation/theme/Animation.kt
- [X] T009 Create Theme.kt composing BabyFoodTheme with Material3 integration and light/dark mode switching in app/src/main/java/com/example/babyfood/presentation/theme/Theme.kt
- [X] T010 Update MainActivity.kt to wrap content with BabyFoodTheme in app/src/main/java/com/example/babyfood/MainActivity.kt

**Checkpoint**: Foundation ready - theme infrastructure is complete and user story implementation can now begin

---

## Phase 3: User Story 1 - Apply Soft Eye-Friendly Color System (Priority: P1) 🎯 MVP

**Goal**: Implement the complete color palette with all specified colors for light and dark modes, ensuring all screens display correct color values

**Independent Test**: Launch the app on a device and verify that all screens display the correct color values for primary buttons, text, backgrounds, and functional elements. Test both light and dark modes.

### Tests for User Story 1

- [X] T011 [P] [US1] Create ColorTest.kt with color contrast ratio validation tests in app/src/test/java/com/example/babyfood/theme/ColorTest.kt
- [X] T012 [P] [US1] Create AccessibilityTest.kt with WCAG contrast ratio compliance tests in app/src/test/java/com/example/babyfood/theme/AccessibilityTest.kt

### Implementation for User Story 1

- [X] T013 [US1] Verify all color values in Color.kt match design specifications exactly (FR-001 through FR-014)
- [X] T014 [US1] Verify light/dark mode color pairs meet WCAG AA contrast ratio (≥4.5:1) using automated contrast checker
- [X] T015 [US1] Test theme switching on physical device by toggling system dark mode setting
- [X] T016 [US1] Validate color contrast ratios meet WCAG AA standard (≥4.5:1 for body text, ≥3:1 for large text) with 90% achieving AAA (≥7:1)

**Checkpoint**: At this point, User Story 1 should be fully functional - all colors are correctly defined and theme switching works

---

## Phase 4: User Story 2 - Implement Rounded Component Design (Priority: P1)

**Goal**: Implement consistent rounded corners for all UI components (buttons, cards, inputs, dialogs) with specified radius values

**Independent Test**: Visually inspect all interactive components (buttons, cards, input fields, dialogs) and measure their corner radius values to verify they match specifications

### Tests for User Story 2

- [X] T017 [P] [US2] Create ShapeTest.kt with corner radius validation tests in app/src/test/java/com/example/babyfood/theme/ShapeTest.kt
- [X] T018 [P] [US2] Create UI test for component corner radius rendering in app/src/androidTest/java/com/example/babyfood/theme/ShapeRenderingTest.kt

### Implementation for User Story 2

- [X] T019 [P] [US2] Verify Shape.kt defines all corner radius values correctly: 24dp (large cards/primary buttons), 20dp (small cards), 16dp (inputs/tags), 28dp (dialogs), 12dp (small icons), 16dp (medium icons)
- [X] T020 [P] [US2] Create Button.kt component with 24dp corner radius for primary buttons and 16dp for secondary buttons in app/src/main/java/com/example/babyfood/presentation/theme/components/Button.kt
- [X] T021 [P] [US2] Create Card.kt component with 24dp corner radius for large cards and 20dp for small cards in app/src/main/java/com/example/babyfood/presentation/theme/components/Card.kt
- [X] T022 [US2] Create InputField.kt component with 16dp corner radius in app/src/main/java/com/example/babyfood/presentation/theme/components/InputField.kt
- [X] T023 [P] [US2] Create Dialog.kt component with 28dp corner radius in app/src/main/java/com/example/babyfood/presentation/theme/components/Dialog.kt
- [X] T024 [P] [US2] Create Icon.kt component with 12dp and 16dp corner radius for icon containers in app/src/main/java/com/example/babyfood/presentation/theme/components/Icon.kt
- [X] T025 [US2] Update home screen cards to use BabyFoodLargeCard and BabyFoodSmallCard components in app/src/main/java/com/example/babyfood/presentation/ui/home/
- [X] T026 [US2] Update all button components across screens to use BabyFoodPrimaryButton or BabyFoodSecondaryButton

**Checkpoint**: At this point, User Story 2 should be fully functional - all components have correct rounded corners

---

## Phase 5: User Story 4 - Implement Accessibility Standards (Priority: P1)

**Goal**: Ensure all UI elements meet WCAG accessibility standards including text contrast, touch target sizes, and screen reader support

**Independent Test**: Use accessibility inspection tools to verify contrast ratios, touch target sizes, and text readability across all screens

### Tests for User Story 4

- [X] T027 [P] [US4] Create AccessibilityTest.kt with touch target size validation tests in app/src/test/java/com/example/babyfood/theme/AccessibilityTest.kt
- [X] T028 [P] [US4] Create integration test for screen reader support in app/src/androidTest/java/com/example/babyfood/theme/ScreenReaderTest.kt

### Implementation for User Story 4

- [X] T029 [US4] Verify all text color and background combinations achieve WCAG AA contrast ratio (≥4.5:1 for body text, ≥3:1 for large text)
- [X] T030 [US4] Verify 90% of color combinations achieve WCAG AAA standard (≥7:1)
- [X] T031 [US4] Apply Modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp) to all interactive elements to ensure minimum touch target size
- [X] T032 [US4] Verify preferred touch target size of 48dp x 48dp is achieved for 95% of interactive elements
- [X] T033 [US4] Add semantics modifier with content descriptions for all images, icons, and interactive elements for screen reader support
- [X] T034 [US4] Verify body text is at least 14sp in size and button text is at least 14sp
- [X] T035 [US4] Verify line height is 1.4-1.6 times font size for body text and 1.2-1.4 times for headings
- [X] T036 [US4] Add additional visual cues (patterns, labels, icons) beyond color alone for growth curve charts to support color blindness

**Checkpoint**: At this point, User Story 4 should be fully functional - all accessibility requirements are met

---

## Phase 6: User Story 3 - Enable Dark Mode Support (Priority: P2)

**Goal**: Implement automatic dark mode switching with all colors adapting correctly while maintaining readability and visual hierarchy

**Independent Test**: Toggle system dark mode setting and verify that all screens, components, and visual elements correctly adapt their colors while maintaining readability

### Tests for User Story 3

- [X] T037 [P] [US3] Create ThemeSwitchingTest.kt with dark mode transition tests in app/src/androidTest/java/com/example/babyfood/theme/ThemeSwitchingTest.kt
- [X] T038 [P] [US3] Create integration test for dark mode color adaptation in app/src/androidTest/java/com/example/babyfood/theme/DarkModeColorTest.kt

### Implementation for User Story 3

- [X] T039 [US3] Verify BabyFoodTheme automatically switches between light and dark ColorScheme based on isSystemInDarkTheme()
- [X] T040 [US3] Verify all dark mode color values match HSL adjustment formulas: background brightness 95%→5%, primary color brightness 75%→65% with 20% saturation reduction
- [X] T041 [US3] Verify functional status colors (success, warning, error) adjust with saturation reduced by 30% and brightness increased by 10%
- [X] T042 [US3] Verify text colors for dark mode have brightness values of 90% (primary), 80% (secondary), and 70% (auxiliary)
- [X] T043 [US3] Verify border/divider colors for dark mode have brightness increased to 25%
- [X] T044 [US3] Verify transparency elements for dark mode adjust to 80% opacity to reduce nighttime glare
- [X] T045 [US3] Test dark mode on devices that don't support it (should remain in light mode with all color specifications applied correctly)

**Checkpoint**: At this point, User Story 3 should be fully functional - dark mode switches automatically and all colors adapt correctly

---

## Phase 7: User Story 5 - Apply Smooth Animations and Transitions (Priority: P2)

**Goal**: Implement smooth animations with specified durations and timing functions, achieving 60fps with maximum 2 frame drops per second

**Independent Test**: Navigate through the app and interact with various UI elements to verify animations are smooth, consistent, and provide appropriate feedback

### Tests for User Story 5

- [ ] T046 [P] [US5] Create AnimationTest.kt with animation duration and timing function tests in app/src/test/java/com/example/babyfood/theme/AnimationTest.kt
- [ ] T047 [P] [US5] Create performance test for animation frame rate in app/src/androidTest/java/com/example/babyfood/theme/AnimationPerformanceTest.kt

### Implementation for User Story 5

- [X] T048 [US5] Verify Animation.kt defines all animation specifications: page transitions (600ms total), button clicks (150ms), card expansion (350ms), loading (1000ms/cycle), refresh (200ms)
- [X] T049 [US5] Verify all timing functions match specifications: ease-in-out, ease-out-back, linear, spring
- [X] T050 [US5] Implement page transition animation with 300ms fade-in followed by 300ms horizontal slide using AnimatedVisibility
- [X] T051 [US5] Implement button click animation with 0.95→1.0 scale over 150ms using spring animation
- [X] T052 [US5] Implement card expand/collapse animation with 0→target height transition over 350ms using ease-out-back
- [X] T053 [US5] Implement loading indicator animation with 1000ms rotation cycle using linear timing function and infinite looping
- [X] T054 [US5] Implement pull-to-refresh bounce animation with 200ms duration using ease-out timing function
- [X] T055 [US5] Optimize animations to achieve 60fps with maximum 2 frame drops per second (16.6ms per frame average)
- [X] T056 [US5] Respect system reduced motion preference by disabling or simplifying animations while maintaining core functionality

**Checkpoint**: At this point, User Story 5 should be fully functional - all animations are smooth and meet performance targets

---

## Phase 8: User Story 6 - Implement Responsive Layout Adaptation (Priority: P2)

**Goal**: Implement responsive layouts that adapt to different screen sizes (320dp minimum, 360dp standard, 412dp maximum) without breaking

**Independent Test**: Run the app on devices with different screen sizes and verify layouts adapt correctly without breaking or causing usability issues

### Tests for User Story 6

- [ ] T057 [P] [US6] Create LayoutAdaptationTest.kt with responsive layout tests for different screen sizes in app/src/androidTest/java/com/example/babyfood/theme/LayoutAdaptationTest.kt
- [ ] T058 [P] [US6] Create orientation test for landscape layout switching in app/src/androidTest/java/com/example/babyfood/theme/OrientationTest.kt

### Implementation for User Story 6

- [X] T059 [US6] Verify 8dp grid system is used globally with all spacing, margins, and component sizes as integer multiples of 8dp
- [ ] T060 [US6] Implement layout adaptation for small phones (320dp minimum) by compressing spacing to minimum 8dp grid without clipping core content
- [ ] T061 [US6] Implement standard layouts for standard phones (360dp width) with all components displaying normally
- [ ] T062 [US6] Implement centered content with side margins for large phones (412dp maximum) without stretching components beyond intended size
- [ ] T063 [US6] Implement two-column layout with fixed left navigation bar and right content area in landscape orientation
- [ ] T064 [US6] Implement tablet-level layout with module columns for foldable devices (unfolded state)
- [ ] T065 [US6] Test layouts on very small screens (below 320dp) to ensure core functionality is maintained with minimal spacing
- [ ] T066 [US6] Test layouts on very large screens (above 412dp) to ensure content is centered with appropriate margins and not stretched

**Checkpoint**: At this point, User Story 6 should be fully functional - layouts adapt correctly across all screen sizes

---

## Phase 9: User Story 7 - Apply Shadow and Elevation Hierarchy (Priority: P3)

**Goal**: Implement consistent shadow and elevation levels that distinguish between different UI layers (cards, buttons, modals)

**Independent Test**: Visually inspect all components and verify shadows are applied consistently according to the defined hierarchy levels

### Tests for User Story 7

- [ ] T067 [P] [US7] Create ElevationTest.kt with shadow and elevation level tests in app/src/androidTest/java/com/example/babyfood/theme/ElevationTest.kt

### Implementation for User Story 7

- [X] T068 [US7] Verify Elevation.kt defines three hierarchy levels: Level 1 (2dp elevation, 4dp blur), Level 2 (8dp elevation, 16dp blur), Level 3 (24dp elevation, 32dp blur)
- [X] T069 [US7] Apply Level 1 shadow to ordinary list cards, tags, and icon containers
- [X] T070 [US7] Apply Level 2 shadow to dialogs, bottom navigation bars, and floating action buttons
- [X] T071 [US7] Apply Level 3 shadow to modal layers, full-screen overlays, and important alert dialogs
- [X] T072 [US7] Implement hover state shadow intensity increase for buttons
- [X] T073 [US7] Ensure disabled buttons have no shadow to visually indicate non-interactive state
- [X] T074 [US7] Verify blur radius is 2x elevation value for all levels

**Checkpoint**: At this point, User Story 7 should be fully functional - shadows and elevations are applied consistently

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T075 [P] Create ProgressBar.kt component with nutrition progress bar styling (horizontal gradient from #88C999 to #52C41A) in app/src/main/java/com/example/babyfood/presentation/theme/components/ProgressBar.kt
- [X] T076 [P] Create Chart.kt component with growth curve chart styling (WHO line #88C999, China line #73A6FF, baby line #FF9F69 with 20% transparency fill) in app/src/main/java/com/example/babyfood/presentation/theme/components/Chart.kt
- [X] T077 Apply gradients to primary buttons (45-degree gradient from #FF9F69 to #FFD4BD) in Button.kt
- [ ] T078 Apply gradients to large cards (vertical 90-degree gradient from #FFFFFF to #FDFBF8) in Card.kt
- [ ] T079 Update all screens (~20+ screens) to use design system components following priority order: home, baby profile, recipes, health records, growth curves
- [ ] T080 [P] Run automated color testing tools to verify all screens display correct color values
- [ ] T081 [P] Run accessibility inspection tools to verify WCAG compliance
- [ ] T082 Run performance profiling to verify animations achieve 60fps with max 2 frame drops per second
- [ ] T083 [P] Test on multiple devices with different screen sizes (320dp, 360dp, 412dp)
- [ ] T084 [P] Test on both light and dark modes
- [ ] T085 Verify all 55 functional requirements are implemented
- [ ] T086 Verify all 12 success criteria are met
- [ ] T087 Run quickstart.md validation checklist
- [ ] T088 Code cleanup and refactoring for consistency

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-9)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed) or sequentially in priority order
  - P1 stories (US1, US2, US4) should be completed before P2 stories (US3, US5, US6)
  - P3 story (US7) should be completed last
- **Polish (Phase 10)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 4 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 3 (P2)**: Can start after US1 is complete - Depends on color palette being defined
- **User Story 5 (P2)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 6 (P2)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 7 (P3)**: Can start after US2 is complete - Depends on components being created

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD approach)
- Color definitions before component creation
- Component creation before screen updates
- Screen updates before integration testing
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (T005-T008)
- Tests for each user story marked [P] can run in parallel (within each story)
- Component creation tasks marked [P] can run in parallel (within each story)
- Polish tasks marked [P] can run in parallel (T075-T076, T080-T081, T083-T084)

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Create ColorTest.kt with color contrast ratio validation tests"
Task: "Create AccessibilityTest.kt with WCAG contrast ratio compliance tests"

# Launch all tests for User Story 2 together:
Task: "Create ShapeTest.kt with corner radius validation tests"
Task: "Create UI test for component corner radius rendering"
```

---

## Parallel Example: User Story 2 Component Creation

```bash
# Launch all component creation tasks for User Story 2 together:
Task: "Create Button.kt component with corner radius specifications"
Task: "Create Card.kt component with corner radius specifications"
Task: "Create InputField.kt component with 16dp corner radius"
Task: "Create Dialog.kt component with 28dp corner radius"
Task: "Create Icon.kt component with icon container corner radius"
```

---

## Implementation Strategy

### MVP First (P1 Stories Only - US1, US2, US4)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T010) - CRITICAL
3. Complete Phase 3: User Story 1 (T011-T016)
4. Complete Phase 4: User Story 2 (T017-T026)
5. Complete Phase 5: User Story 4 (T027-T036)
6. **STOP and VALIDATE**: Test all P1 stories independently
7. Deploy/demo MVP with core design system

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Validate colors
3. Add User Story 2 → Test independently → Validate rounded corners
4. Add User Story 4 → Test independently → Validate accessibility
5. Add User Story 3 → Test independently → Validate dark mode
6. Add User Story 5 → Test independently → Validate animations
7. Add User Story 6 → Test independently → Validate responsive layouts
8. Add User Story 7 → Test independently → Validate shadows
9. Polish and integrate all components

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Color System)
   - Developer B: User Story 2 (Rounded Components)
   - Developer C: User Story 4 (Accessibility)
3. After P1 stories complete:
   - Developer A: User Story 3 (Dark Mode)
   - Developer B: User Story 5 (Animations)
   - Developer C: User Story 6 (Responsive Layouts)
4. Finally: User Story 7 (Shadows) + Polish phase

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD approach)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- P1 stories (US1, US2, US4) form the MVP foundation
- P2 stories (US3, US5, US6) enhance the experience
- P3 story (US7) provides polish
- Total task count: 88 tasks
- Estimated effort: 5-7 days for single developer, 3-4 days with team parallelization