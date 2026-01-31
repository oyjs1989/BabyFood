# Quickstart: 移除页面返回和页面名称

**Feature**: 003-remove-page-back-and-titles
**Created**: 2026-01-31

## What is this feature?

This feature removes the page title bar (with page name and back button) from all pages in the BabyFood app. The goal is to provide more screen space for content and simplify the navigation model.

## Key Changes

### What's Removed
- Page titles (e.g., "宝宝详情", "食谱详情")
- Back button (arrow icon) from all detail and form pages
- AppTopAppBar component from AppScaffold

### What's Kept
- Bottom navigation bar (for main sections)
- Global header with "BabyFood" logo (clickable to go home)
- User authentication status in header
- Bottom action buttons in forms

### New Behavior
- Click "BabyFood" logo in header to return to home
- Bottom navigation for switching between main sections
- Auto-navigate to list after form save

## Affected Pages

All pages using `AppScaffold` (~20+ pages):
- Baby screens (detail, form, allergies, preferences)
- Recipe screens (detail, form)
- Plan screens (detail, form, editor)
- Health screens (list, form)
- Growth screens (curve)
- Inventory screens (form)
- AI screens (settings, recommendation)
- Auth screens (login, register)

## How to Test

1. **Navigate to detail page**
   - Open app, go to baby list
   - Tap on a baby to view details
   - Verify: No title bar or back button at top
   - Verify: Content starts from top of screen

2. **Return to home**
   - Tap "BabyFood" text in header
   - Verify: Navigate to home page

3. **Form auto-return**
   - Go to baby list, tap "Add baby"
   - Fill form and save
   - Verify: Auto-return to baby list

4. **Bottom navigation**
   - Tap bottom nav icons to switch sections
   - Verify: Navigate to correct pages

## Implementation Notes

- Need to modify `AppScaffold` component to remove top bar
- Need to remove `appBarConfig` from all screen calls
- Ensure header navigation to home works
- Ensure form auto-navigation after save

## Success Metrics

- Content area height increases by ~64dp
- Users can return to home in 3 seconds
- Forms auto-return to list in 1 second after save
- User satisfaction improves by 15%

## Related Files

- Spec: [spec.md](spec.md)
- Data Model: [data-model.md](data-model.md)
- Research: [research.md](research.md)