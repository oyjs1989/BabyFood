# BabyFood 项目未完成目标

## 📋 已完成

### Apple 风格设计优化（2026-01-29）

#### 第一阶段：核心系统 + 首页 ✅
- [x] Color.kt - 颜色系统优化
- [x] Type.kt - 字体系统优化
- [x] Theme.kt - 主题配置优化
- [x] NutritionGoalCard.kt - 营养目标卡片优化
- [x] MealTimeline.kt - 餐单时间轴卡片优化

#### 第二阶段：食谱与计划 ✅
- [x] RecipesListScreen.kt - 食谱列表优化
- [x] PlanListScreen.kt - 计划列表优化

#### 第三阶段：其他页面 ✅
- [x] InventoryListScreen.kt - 仓库列表优化
- [x] BabyListScreen.kt - 宝宝列表优化
- [x] MainScreen.kt - 底部导航栏优化

---

## 🎯 未完成目标

### 1. 详情页优化 ✅

#### RecipeDetailScreen.kt（食谱详情页）
- [x] 应用 Apple 风格卡片样式
- [x] 移除阴影，添加细边框
- [x] 优化图片展示（尺寸、圆角）
- [x] 优化食材列表样式
- [x] 优化步骤列表样式
- [x] 使用主题颜色替换硬编码颜色

#### PlanDetailScreen.kt（计划详情页）
- [x] 应用 Apple 风格卡片样式
- [x] 移除阴影，添加细边框
- [x] 优化营养汇总展示
- [x] 优化操作按钮样式
- [x] 修改 TopAppBar 背景色

#### BabyDetailScreen.kt（宝宝详情页）
- [x] 应用 Apple 风格卡片样式
- [x] 移除阴影，添加细边框
- [x] 优化营养目标卡片
- [x] 优化黑白名单展示
- [x] 优化头像展示
- [x] 修改用户信息卡片背景色

---

### 2. 表单页优化 ✅

#### PlanFormScreen.kt（计划表单页）
- [x] 应用 Apple 风格输入框
- [x] 优化按钮样式
- [x] 优化日期选择器（添加边框，移除阴影）
- [x] 修改 TopAppBar 背景色

#### BabyFormScreen.kt（宝宝表单页）
- [x] 应用 Apple 风格输入框
- [x] 优化按钮样式
- [x] 优化日期选择器
- [x] 修改 TopAppBar 背景色

#### InventoryFormScreen.kt（仓库表单页）
- [x] 应用 Apple 风格输入框
- [x] 优化按钮样式
- [x] 优化日期选择器
- [x] 修改 TopAppBar 背景色

---

### 3. 对话框优化

#### ConflictResolutionDialog.kt（冲突解决对话框）
- [ ] 应用 Apple 风格对话框
- [ ] 优化按钮样式
- [ ] 优化选项卡片

#### DateRangePickerDialog.kt（日期范围选择对话框）
- [ ] 应用 Apple 风格对话框
- [ ] 优化日期选择器样式

#### RecipeSelectorDialog.kt（食谱选择对话框）
- [ ] 应用 Apple 风格对话框
- [ ] 优化食谱列表样式
- [ ] 优化搜索框

---

### 4. 空状态优化

#### 首页空状态
- [ ] 优化未添加宝宝时的引导
- [ ] 优化无餐单时的提示

#### 列表页空状态
- [ ] 优化食谱库空状态
- [ ] 优化计划列表空状态
- [ ] 优化仓库列表空状态
- [ ] 优化宝宝列表空状态

---

### 5. 其他组件优化

#### WeeklyPlansSection.kt（周计划展示）
- [ ] 应用 Apple 风格卡片样式
- [ ] 移除阴影，添加细边框
- [ ] 优化展开/折叠动画

#### CircularProgressWithValue（环形进度条）
- [ ] 优化颜色和样式
- [ ] 优化动画效果

#### FilterChip（筛选芯片）
- [ ] 优化选中状态样式
- [ ] 优化未选中状态样式

---

## 🎨 设计规范补充

### 圆角规范
- 小元素（标签、按钮）：4dp
- 卡片内部元素：8dp
- 卡片：12dp
- 大卡片、模态框：16dp
- 特殊强调元素：20dp

### 间距规范
- 极小间距：4dp
- 小间距：8dp
- 中等间距：12dp
- 大间距：16dp
- 超大间距：24dp
- 特大间距：32dp

### 字体规范
- Display Large: 34sp, Bold
- Display Medium: 28sp, Bold
- Display Small: 22sp, Bold
- Headline Large: 28sp, Bold
- Headline Medium: 22sp, Bold
- Headline Small: 20sp, Bold
- Title Large: 20sp, Bold
- Title Medium: 16sp, Bold
- Title Small: 14sp, Medium
- Body Large: 17sp, Normal
- Body Medium: 15sp, Normal
- Body Small: 13sp, Normal
- Label Large: 15sp, Medium
- Label Medium: 13sp, Medium
- Label Small: 11sp, Medium

### 颜色规范
- 主色：#FF7A65（柔和珊瑚色）
- 背景：#FAFAFA（接近纯白）
- 表面：#FFFFFF（纯白）
- 表面变体：#F5F5F7（iOS 风格灰白）
- 文字：#000000（纯黑）
- 次要文字：#8E8E93（iOS 次要文字灰）
- 分割线：#C6C6C8（iOS 分割线灰）
- 轮廓：#C7C7CC（iOS 轮廓灰）

---

## 📝 备注

### 优化原则
1. 移除阴影，使用细边框（0.5.dp）
2. 统一圆角规范（12dp）
3. 增加图片尺寸（80dp → 88dp）
4. 更精致的细节处理
5. 更柔和、更克制的色彩
6. 更清晰的文字对比度

### 测试建议
1. 在不同设备上测试
2. 测试浅色和深色模式
3. 测试不同屏幕尺寸
4. 测试用户交互流程

---

**最后更新：** 2026-01-29