# Feature Specification: Apply BabyFood UI Design System

**Feature Branch**: `004-apply-ui-design-system`  
**Created**: 2026-01-31  
**Status**: Draft  
**Input**: User description: "基于柔和护眼、低饱和度、层次清晰原则，适配婴幼儿辅食场景，兼顾浅色模式为主、深色模式适配，区分主色、辅助色、中性色、功能色四大板块，匹配移动端视觉与可读性要求。"

## Clarifications

### Session 2026-01-31
- Q: 动画性能：在动画被认为有"可感知的延迟"之前，最大可接受的掉帧率是多少？ → A: 最多每秒掉2帧（平均16.6ms，60fps目标）

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Apply Soft Eye-Friendly Color System (Priority: P1)

Parents using the BabyFood app during nighttime or low-light conditions should experience a comfortable, non-glaring visual experience with soft, low-saturation colors that reduce eye strain while maintaining excellent readability for menu items, nutrition data, and recipe instructions.

**Why this priority**: This is the foundation of the entire design system. The color palette affects every screen and component, directly impacting user comfort, especially for parents who may use the app in various lighting conditions (including nighttime feeding sessions). Poor color choices can cause eye fatigue and reduce app usage.

**Independent Test**: Can be fully tested by launching the app on a device and verifying that all screens display the correct color values for primary buttons, text, backgrounds, and functional elements. The test delivers value immediately by improving visual comfort and reducing eye strain for users.

**Acceptance Scenarios**:

1. **Given** a parent opens the BabyFood app during daytime, **When** viewing the home screen, **Then** the page background displays #FDFBF8 (warm cream), card backgrounds display #FFFFFF (pure white), and primary text displays #333333 (dark gray) with a contrast ratio of at least 13.5:1
2. **Given** a parent opens the app during nighttime, **When** the system detects dark mode is enabled, **Then** the page background switches to #121212 (near black), card backgrounds switch to #1E1E1E (dark gray), and primary text switches to #F5F5F5 (off-white) while maintaining readability
3. **Given** a parent views nutrition progress bars, **When** progress is at 50%, **Then** the completed segment displays #FF9F69 (warm orange), the incomplete segment displays #E5E5E5 (neutral gray), and the container background displays #FDFBF8 (page background color)
4. **Given** a parent views growth curve charts, **When** multiple data series are displayed, **Then** the WHO standard line displays #88C999 (soft green), the China standard line displays #73A6FF (fresh blue), and the baby's actual data line displays #FF9F69 (warm orange) with 20% transparency fill below the curve

---

### User Story 2 - Implement Rounded Component Design (Priority: P1)

Users interacting with buttons, cards, and input fields should experience a soft, friendly, and approachable interface with consistent rounded corners that eliminate sharp edges, creating a warm and safe visual experience appropriate for a baby/parenting application.

**Why this priority**: Rounded corners are a fundamental UI pattern that affects user perception of the app's personality. Sharp edges can feel cold and clinical, while rounded corners convey warmth, safety, and approachability—essential qualities for a parenting app. This applies to every interactive element across the entire application.

**Independent Test**: Can be fully tested by visually inspecting all interactive components (buttons, cards, input fields, dialogs) and measuring their corner radius values. The test delivers immediate visual value by creating a more welcoming and child-friendly interface.

**Acceptance Scenarios**:

1. **Given** a parent views the home screen, **When** looking at meal menu cards, **Then** large cards (today's menu, recipe details) display with 24dp corner radius and small cards (recipe list items, inventory items) display with 20dp corner radius
2. **Given** a parent taps a primary action button, **When** the button is in default state, **Then** it displays with 24dp corner radius, #FF9F69 background color, #FFFFFF text color, and Level 1 shadow (4dp blur radius)
3. **Given** a parent taps a secondary action button, **When** the button is in default state, **Then** it displays with 16dp corner radius
4. **Given** a parent fills out a form, **When** an input field is in default state, **Then** it displays with 16dp corner radius, #E5E5E5 border color (1dp width), and #FFFFFF background color
5. **Given** a modal dialog appears, **When** the dialog is displayed, **Then** it displays with 28dp corner radius

---

### User Story 3 - Enable Dark Mode Support (Priority: P2)

Parents using the app during nighttime or preferring dark interfaces should be able to switch between light and dark modes, with all colors, components, and visual elements automatically adapting to maintain readability, contrast, and visual hierarchy.

**Why this priority**: Dark mode is an increasingly expected feature in mobile applications, especially for parents who may use the app during nighttime feedings or in low-light environments. While not as critical as the base color system, it significantly enhances user experience and reduces eye strain during nighttime use.

**Independent Test**: Can be fully tested by toggling the system dark mode setting and verifying that all screens, components, and visual elements correctly adapt their colors while maintaining readability and visual hierarchy. The test delivers value by enabling comfortable nighttime usage.

**Acceptance Scenarios**:

1. **Given** a parent enables dark mode on their device, **When** the BabyFood app launches, **Then** all screens automatically switch to dark color palette with page background #121212, card background #1E1E1E, and primary text #F5F5F5
2. **Given** a parent uses the app in dark mode, **When** viewing nutrition progress bars, **Then** the completed segment color adjusts to #E67A4A (darker warm orange), the incomplete segment remains #E5E5E5, and contrast ratios meet WCAG AA standards
3. **Given** a parent uses the app in dark mode, **When** viewing growth curve charts, **Then** all color values adjust according to HSL formula (primary color saturation reduced by 20%, brightness reduced by 20%) while maintaining visual distinction between data series
4. **Given** a parent uses the app in dark mode, **When** viewing functional status colors (success, warning, error), **Then** saturation is reduced by 30% and brightness increased by 10% to prevent eye strain in low-light conditions

---

### User Story 4 - Implement Accessibility Standards (Priority: P1)

Parents with visual impairments, color blindness, or other accessibility needs should be able to use the BabyFood app effectively with proper text contrast, touch target sizes, and clear visual hierarchy that meets WCAG accessibility standards.

**Why this priority**: Accessibility is a fundamental requirement for modern applications. Parents may have varying visual abilities, and ensuring accessibility demonstrates inclusive design and broadens the app's user base. Non-compliant accessibility can also lead to legal and reputational issues.

**Independent Test**: Can be fully tested by using accessibility inspection tools to verify contrast ratios, touch target sizes, and text readability. The test delivers immediate value by making the app usable for a wider range of users.

**Acceptance Scenarios**:

1. **Given** a parent with color blindness views the app, **When** looking at growth curve charts, **Then** data series are distinguished not only by color but also by line patterns or labels to ensure differentiation
2. **Given** a parent with visual impairments views the app, **When** reading primary text, **Then** the text color #333333 on background #FDFBF8 achieves a contrast ratio of at least 13.5:1 (meeting WCAG AAA standard)
3. **Given** a parent with visual impairments views the app, **When** reading secondary text, **Then** the text color #666666 on background #FDFBF8 achieves a contrast ratio of at least 6.5:1 (meeting WCAG AA standard)
4. **Given** a parent with motor impairments interacts with the app, **When** tapping any interactive element, **Then** the touch target area is at least 44dp x 44dp (minimum) and preferably 48dp x 48dp (recommended)
5. **Given** a parent with visual impairments views the app, **When** reading text, **Then** body text is at least 14sp in size, button text is at least 14sp, and line height is 1.4-1.6 times the font size

---

### User Story 5 - Apply Smooth Animations and Transitions (Priority: P2)

Users navigating between screens and interacting with UI elements should experience smooth, natural animations that provide visual feedback, enhance perceived performance, and create a polished, professional feel without causing motion sickness or distraction.

**Why this priority**: Well-designed animations significantly enhance the perceived quality and responsiveness of an application. However, poorly implemented animations can cause frustration, motion sickness, or slow down the user experience. This priority ensures animations add value without detracting from usability.

**Independent Test**: Can be fully tested by navigating through the app and interacting with various UI elements to verify that animations are smooth, consistent, and provide appropriate feedback. The test delivers value by creating a more engaging and polished user experience.

**Acceptance Scenarios**:

1. **Given** a parent navigates between screens, **When** the transition occurs, **Then** the animation consists of 300ms fade-in followed by 300ms horizontal slide with ease-in-out timing function
2. **Given** a parent taps a button, **When** the button is pressed, **Then** it scales from 0.95 to 1.0 over 150ms using a spring animation for elastic feedback
3. **Given** a parent expands a card, **When** the expansion occurs, **Then** the height animates from 0 to target height over 350ms with ease-out-back timing function
4. **Given** a parent waits for data to load, **When** a loading indicator appears, **Then** it rotates once every 1000ms with linear timing function and infinite looping
5. **Given** a parent performs a pull-to-refresh action, **When** the refresh completes, **Then** the bounce animation lasts 200ms with ease-out timing function

---

### User Story 6 - Implement Responsive Layout Adaptation (Priority: P2)

Parents using the BabyFood app on devices with different screen sizes (small phones, large phones, foldable devices) should experience a consistent and optimized layout that adapts to their device without breaking or causing usability issues.

**Why this priority**: With the variety of Android devices available, responsive design ensures the app remains usable and visually appealing across different screen sizes. While not as critical as core functionality, poor responsiveness can lead to broken layouts, inaccessible content, and frustrated users.

**Independent Test**: Can be fully tested by running the app on devices with different screen sizes (320dp minimum, 360dp standard, 412dp maximum) and verifying that layouts adapt correctly without breaking or causing usability issues. The test delivers value by ensuring consistent experience across devices.

**Acceptance Scenarios**:

1. **Given** a parent uses the app on a small phone (320dp minimum width), **When** viewing any screen, **Then** component spacing compresses to minimum 8dp grid and no core content is clipped or hidden
2. **Given** a parent uses the app on a standard phone (360dp width), **When** viewing any screen, **Then** all components display with standard spacing and layout as designed
3. **Given** a parent uses the app on a large phone (412dp maximum width), **When** viewing any screen, **Then** content is centered with side margins and components are not stretched beyond their intended size
4. **Given** a parent uses the app in landscape orientation, **When** the screen rotates, **Then** the layout switches to a two-column design with fixed left navigation bar and right content area
5. **Given** a parent uses the app on a foldable device (unfolded state), **When** the device is unfolded, **Then** the layout switches to a tablet-level design with module columns to maximize space utilization

---

### User Story 7 - Apply Shadow and Elevation Hierarchy (Priority: P3)

Users viewing the app should perceive a clear visual hierarchy through consistent shadow and elevation levels that distinguish between different UI layers (cards, buttons, modals) and create depth without overwhelming the interface.

**Why this priority**: Shadows and elevation provide important visual cues about element hierarchy and interactivity. While not as critical as color or accessibility, proper shadow implementation enhances the polished feel and helps users understand which elements are interactive or elevated.

**Independent Test**: Can be fully tested by visually inspecting all components and verifying that shadows are applied consistently according to the defined hierarchy levels. The test delivers value by creating a more organized and visually clear interface.

**Acceptance Scenarios**:

1. **Given** a parent views list cards, tags, or icon containers, **When** these elements are displayed, **Then** they apply Level 1 shadow with 2dp elevation and 4dp blur radius
2. **Given** a parent views a modal dialog, bottom navigation bar, or floating action button, **When** these elements are displayed, **Then** they apply Level 2 shadow with 8dp elevation and 16dp blur radius
3. **Given** a parent views a modal layer or full-screen overlay, **When** these elements are displayed, **Then** they apply Level 3 shadow with 24dp elevation and 32dp blur radius
4. **Given** a parent taps a button, **When** the button is in hover state, **Then** the shadow intensity slightly increases to provide visual feedback
5. **Given** a parent views a disabled button, **When** the button is in disabled state, **Then** no shadow is applied to visually indicate non-interactive state

---

### Edge Cases

- What happens when the user's device does not support dark mode? The app should remain in light mode with all color specifications applied correctly.
- What happens when the user has high contrast mode enabled at the system level? The app should respect the system setting while maintaining the defined color palette as much as possible.
- What happens when the user has reduced motion preference enabled? The app should disable or simplify animations while maintaining core functionality.
- What happens when the user has color blindness? The app should use additional visual cues (patterns, labels, icons) beyond color alone to distinguish elements, especially in charts and status indicators.
- What happens when the user has a very small screen (below 320dp)? The app should maintain core functionality with minimal spacing and potentially hide non-essential decorative elements.
- What happens when the user has a very large screen (above 412dp)? The app should center content with appropriate margins and not stretch components beyond their intended proportions.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display the page background color as #FDFBF8 (warm cream) in light mode and #121212 (near black) in dark mode
- **FR-002**: System MUST display card and module backgrounds as #FFFFFF (pure white) in light mode and #1E1E1E (dark gray) in dark mode
- **FR-003**: System MUST display primary text as #333333 (dark gray) in light mode and #F5F5F5 (off-white) in dark mode
- **FR-004**: System MUST display secondary text as #666666 (medium gray) in light mode and #C1C1C1 (light gray) in dark mode
- **FR-005**: System MUST display auxiliary text as #999999 (light gray) in light mode and #8E8E8E (gray) in dark mode
- **FR-006**: System MUST display divider/border lines as #E5E5E5 (light gray) in light mode and #3D3D3D (dark gray) in dark mode
- **FR-007**: System MUST use #FF9F69 (warm orange) as the primary brand color for core UI elements
- **FR-008**: System MUST use #FFD4BD (light orange) as the extended brand color for gradients and hover states
- **FR-009**: System MUST use #88C999 (soft green) for health/natural themed elements and WHO standard reference lines
- **FR-010**: System MUST use #73A6FF (fresh blue) for technology/intelligence themed elements and China standard reference lines
- **FR-011**: System MUST use #FFE8A3 (cream yellow) for warm accent and highlighting elements
- **FR-012**: System MUST use #52C41A (success green) for completed status, checkmark icons, and success labels
- **FR-013**: System MUST use #FAAD14 (warning orange) for in-progress status, pending tasks, and warning indicators
- **FR-014**: System MUST use #F5222D (error red) for error states, danger alerts, and delete operations
- **FR-015**: System MUST display nutrition progress bars with completed segment as #FF9F69, incomplete segment as #E5E5E5, and container background as #FDFBF8
- **FR-016**: System MUST display growth curve charts with WHO standard line as #88C999, China standard line as #73A6FF, and baby's actual data line as #FF9F69 with 20% transparency fill below the curve
- **FR-017**: System MUST apply 24dp corner radius to large cards (today's menu, recipe details), primary buttons, and core action buttons
- **FR-018**: System MUST apply 20dp corner radius to small cards (recipe list items, inventory items)
- **FR-019**: System MUST apply 16dp corner radius to small tag buttons, secondary function buttons, nutrition labels, status labels, text input fields, search boxes, and form input fields
- **FR-020**: System MUST apply 28dp corner radius to global dialogs, modal windows, and bottom floating layers
- **FR-021**: System MUST apply 12dp corner radius to small icon containers
- **FR-022**: System MUST apply 16dp corner radius to medium and large icon containers
- **FR-023**: System MUST apply Level 1 shadow (2dp elevation, 4dp blur radius) to ordinary list cards, tags, and icon containers
- **FR-024**: System MUST apply Level 2 shadow (8dp elevation, 16dp blur radius) to dialogs, bottom navigation bars, and floating action buttons
- **FR-025**: System MUST apply Level 3 shadow (24dp elevation, 32dp blur radius) to modal layers, full-screen overlays, and important alert dialogs
- **FR-026**: System MUST apply a 45-degree gradient from #FF9F69 to #FFD4BD for primary action buttons and floating action button backgrounds
- **FR-027**: System MUST apply a vertical (90-degree) gradient from #FFFFFF to #FDFBF8 for page large cards and module container backgrounds
- **FR-028**: System MUST apply a horizontal (0-degree) gradient from #88C999 to #52C41A for nutrition progress bar fills when goals are met
- **FR-029**: System MUST ensure all touch target areas are at least 44dp x 44dp (minimum) and preferably 48dp x 48dp (recommended)
- **FR-030**: System MUST ensure body text is at least 12sp in size, with button text not below 14sp
- **FR-031**: System MUST ensure line height is 1.4-1.6 times the font size for body text and 1.2-1.4 times for headings
- **FR-032**: System MUST ensure character spacing is 0.1sp for body text (preferably 0.3sp) and 0-0.2sp for headings
- **FR-033**: System MUST ensure color contrast ratios meet WCAG AA standard (≥4.5:1 for body text, ≥3:1 for large text) and prefer AAA standard (≥7:1 for body text)
- **FR-034**: System MUST display linear icons with 1.5dp stroke width, rounded endpoints, and 2dp element rounded corners
- **FR-035**: System MUST display filled icons with no stroke outline and 4dp element rounded corners
- **FR-036**: System MUST use icon sizes of 24dp (small), 32dp (medium), and 48dp (large) with 8dp spacing between icon and text
- **FR-037**: System MUST display illustrations in a flat minimalist style with soft rounded lines (no sharp angles), using only primary, auxiliary, and neutral colors with maximum 3 main colors per illustration
- **FR-038**: System MUST ensure illustration elements have 8-16dp rounded corners and use only Level 1 shadows
- **FR-039**: System MUST adapt illustrations to mobile screen width with 16-24dp left and right margins
- **FR-040**: System MUST automatically switch between light and dark mode based on system settings
- **FR-041**: System MUST adjust colors for dark mode using the HSL formula: background brightness reduced from 95% to 5%, primary color brightness reduced from 75% to 65% with 20% saturation reduction, functional color saturation reduced by 30% with 10% brightness increase
- **FR-042**: System MUST adjust text colors for dark mode with brightness values of 90% (primary), 80% (secondary), and 70% (auxiliary)
- **FR-043**: System MUST adjust border/divider colors for dark mode with brightness increased to 25%
- **FR-044**: System MUST adjust transparency elements for dark mode to 80% opacity to reduce nighttime glare
- **FR-045**: System MUST apply 300ms fade-in followed by 300ms horizontal slide with ease-in-out timing function for page transitions
- **FR-046**: System MUST apply 0.95→1.0 scale over 150ms with spring animation for button click feedback
- **FR-047**: System MUST apply 0→target height transition over 350ms with ease-out-back timing function for card expand/collapse
- **FR-048**: System MUST apply 1000ms rotation cycle with linear timing function and infinite looping for loading indicators
- **FR-049**: System MUST apply 200ms bounce animation with ease-out timing function for pull-to-refresh
- **FR-050**: System MUST adapt layouts for small phones (320dp minimum) by compressing spacing to minimum 8dp grid without clipping core content
- **FR-051**: System MUST display standard layouts for standard phones (360dp width) with all components displaying normally
- **FR-052**: System MUST center content with side margins for large phones (412dp maximum) without stretching components beyond intended size
- **FR-053**: System MUST switch to two-column layout with fixed left navigation and right content area in landscape orientation
- **FR-054**: System MUST switch to tablet-level layout with module columns for foldable devices (unfolded state)
- **FR-055**: System MUST use 8dp grid system globally with all spacing, margins, and component sizes as integer multiples of 8dp

### Key Entities

- **Color Palette**: Defines the complete color system including primary, auxiliary, neutral, and functional colors for both light and dark modes
- **Component Specification**: Defines visual properties (corner radius, shadows, gradients) for all UI components (buttons, cards, inputs, dialogs)
- **Typography System**: Defines font sizes, weights, line heights, character spacing, and color hierarchy for text elements
- **Animation Definition**: Specifies timing functions, durations, and behaviors for all UI animations and transitions
- **Layout Constraint**: Defines responsive layout rules for different screen sizes and orientations
- **Accessibility Standard**: Specifies contrast ratios, touch target sizes, and readability requirements

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All screens display correct color values as specified in the design system document, verified by automated color testing tools
- **SC-002**: All text color and background combinations achieve WCAG AA contrast ratio of at least 4.5:1 for body text and 3:1 for large text, with 90% of combinations achieving AAA standard of 7:1
- **SC-003**: All interactive elements (buttons, cards, inputs) have touch target areas of at least 44dp x 44dp, with 95% achieving the recommended 48dp x 48dp
- **SC-004**: All component corner radii match the specified values (24dp for large cards/primary buttons, 20dp for small cards, 16dp for inputs/tags, 28dp for dialogs) with 100% accuracy
- **SC-005**: All shadows and elevation levels are applied consistently according to the 3-level hierarchy (Level 1: 2dp/4dp, Level 2: 8dp/16dp, Level 3: 24dp/32dp) across all components
- **SC-006**: Dark mode switches automatically based on system settings, with all colors adapting correctly and maintaining readability and visual hierarchy
- **SC-007**: All animations complete within their specified durations (page transitions: 600ms total, button clicks: 150ms, card expansion: 350ms, loading: 1000ms/cycle, refresh: 200ms) with maximum 2 frame drops per second at 60fps target (average 16.6ms per frame)
- **SC-008**: Layout adapts correctly across different screen sizes (320dp minimum, 360dp standard, 412dp maximum) without breaking or causing usability issues
- **SC-009**: Users can successfully navigate between screens and complete primary tasks (viewing menu, adding recipes, checking growth data) within 3 minutes on first use
- **SC-010**: 90% of users report that the app's visual design is comfortable, easy to read, and appropriate for a parenting application based on user feedback surveys
- **SC-011**: All growth curve charts display data series with distinguishable colors and visual patterns that remain clear for users with color blindness
- **SC-012**: All gradients are applied correctly with specified angles and color combinations (primary buttons: 45°, card backgrounds: 90°, nutrition progress: 0°)

## Assumptions

- The app runs on Android devices with minimum SDK 24 (Android 7.0) and target SDK 34 (Android 14)
- Users have devices that support both light and dark modes (Android 10+)
- The app uses Jetpack Compose for UI rendering, which supports modern theming and styling capabilities
- Users prefer a warm, soft visual aesthetic for a baby/parenting application over high-contrast or clinical designs
- Users primarily use the app on mobile phones (320dp-412dp width) rather than tablets or foldable devices
- System-level accessibility settings (high contrast, reduced motion, text size) should be respected where possible
- The design system should prioritize light mode as the primary experience with dark mode as an optional enhancement
- Color contrast ratios should meet WCAG AA standards as a minimum, with AAA as the preferred target
- All measurements (dp, sp) follow Android's density-independent pixel specifications
- Animations should enhance user experience without causing motion sickness or distraction