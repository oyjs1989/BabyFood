# Data Model: 移除页面返回和页面名称

**Feature**: 003-remove-page-back-and-titles
**Created**: 2026-01-31

## Overview

This feature removes the page title bar (AppTopAppBar) from all pages that use AppScaffold, which simplifies the UI and provides more space for content. The navigation is consolidated into the global Header and bottom navigation bar.

## Affected Components

### AppScaffold
- **Current**: Uses `appBarConfig` to render `AppTopAppBar` with title and back button
- **After Change**: Removes `appBarConfig` parameter, does not render top bar

### AppTopAppBar
- **Current**: Displays page title, back button, and action buttons
- **After Change**: No longer used in AppScaffold (may still be used elsewhere if needed)

### AppBarConfig
- **Current**: Defines title, showBackButton, backIcon, and actions for the top bar
- **After Change**: May become obsolete or used for different purposes

## Navigation Changes

### Current Navigation Flow
```
首页 → 宝宝列表 → 宝宝详情 (with back button)
                ↑
                back button
```

### After Change
```
首页 → 宝宝列表 → 宝宝详情 (no back button)
                ↑
                click "BabyFood" in Header or bottom nav
```

## Screen Components to Modify

All screens that use `AppScaffold` will need to be updated:

1. **Baby Screens**
   - BabyDetailScreen
   - BabyFormScreen
   - BabyListScreen (may not use AppScaffold)
   - AllergyManagementScreen
   - PreferenceManagementScreen

2. **Recipe Screens**
   - RecipeDetailScreen
   - RecipeFormScreen
   - RecipesListScreen (may not use AppScaffold)

3. **Plan Screens**
   - PlanDetailScreen
   - PlanFormScreen
   - PlanListScreen (may not use AppScaffold)
   - RecommendationEditorScreen

4. **Health Screens**
   - HealthRecordListScreen
   - HealthRecordFormScreen

5. **Growth Screens**
   - GrowthCurveScreen

6. **Inventory Screens**
   - InventoryListScreen (may not use AppScaffold)
   - InventoryFormScreen

7. **AI Screens**
   - AiSettingsScreen
   - RecommendationScreen

8. **Auth Screens**
   - LoginScreen
   - RegisterScreen

## Migration Notes

- Remove `appBarConfig` parameter from all `AppScaffold` calls
- Update `AppScaffold` component to not render `AppTopAppBar`
- Ensure Header navigation to homepage is functional
- Ensure bottom navigation is visible on main pages
- Ensure form pages auto-navigate to list after save