# BabyFood 微信小程序 MVP 设计文档

**日期:** 2026-06-13  
**状态:** 待实现  
**参考项目:** `D:\workspace\BabyFood`（Android 版本）

---

## 背景与目标

BabyFood Android 版（宝宝辅食智能助手）已具备完整功能，需基于相同后端 API 开发微信小程序版本，覆盖微信生态用户。小程序 MVP 专注核心功能，后续迭代再补充生长记录、健康档案等高级功能。

**预期成果：** 可运行的微信小程序，用户可完成注册/登录、建立宝宝档案、浏览 AI 推荐食谱、查看今日菜单。

---

## 技术选型

| 项目 | 选择 | 理由 |
|------|------|------|
| 框架 | Taro 4.x | React 语法，一套代码可同时产出小程序和 H5 |
| 语言 | TypeScript | 与 Android 37 个域模型结构一一对应 |
| UI 库 | NutUI-React | 京东出品，专为 Taro 优化，CSS 变量支持主题覆盖 |
| 状态管理 | Zustand | 轻量，store 结构镜像 Android ViewModel + StateFlow |
| API 通信 | Taro.request 封装 | 统一 baseURL、JWT header、401 自动刷新 |
| Token 存储 | wx.setStorageSync | 对应 Android EncryptedSharedPreferences |

**后端：** 复用 Android 同一套 API，baseURL = `http://39.108.143.232:8080/`，JWT 认证。

---

## MVP 功能范围

### 底部 TabBar（4 个入口）
1. **首页** — 今日菜单
2. **食谱** — 食谱列表与 AI 推荐
3. **宝宝** — 宝宝档案管理
4. **我的** — 用户个人中心

### 模块详情

#### 认证（非 TabBar 页）
- 登录：手机号 + 密码 → `POST /api/v1/auth/login` → JWT 存 Storage
- 注册：手机号 + 密码 + 昵称 → `POST /api/v1/auth/register`
- 预留微信授权登录入口（MVP 阶段暂不实现，后续迭代）
- 未登录时拦截跳转至登录页

#### 宝宝管理
- 列表：显示所有宝宝，可切换当前选中宝宝
- 新建/编辑：名字、出生日期（自动计算月龄）、过敏食材（多选）、饮食偏好
- API：`GET/POST/PUT/DELETE /api/v1/babies/`

#### 食谱模块
- 列表：按月龄过滤，卡片展示（食谱名、辅食质地、月龄范围、主要食材）
- 详情：完整信息、营养成分、制作步骤
- AI 推荐：基于当前宝宝月龄+过敏信息调用 `POST /api/v1/recipes/candidates`，分四餐展示候选食谱
- API：`GET /api/v1/recipes/`，`POST /api/v1/recipes/candidates`

#### 今日菜单（首页）
- 展示今日四餐计划（早餐/午餐/晚餐/加餐）
- 每餐可从 AI 推荐候选中添加食谱
- 营养摄入摘要（热量、蛋白质、钙、铁）
- API：`GET/POST/PUT /api/v1/plans/`

---

## 项目目录结构

```
BabyFoodMini/                    # 与 BabyFood/ 并列创建
├── src/
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── login/index.tsx
│   │   │   └── register/index.tsx
│   │   ├── home/index.tsx       # 今日菜单（TabBar page）
│   │   ├── recipes/
│   │   │   ├── index/index.tsx  # 食谱列表（TabBar page）
│   │   │   └── detail/index.tsx # 食谱详情
│   │   ├── baby/
│   │   │   ├── index/index.tsx  # 宝宝列表（TabBar page）
│   │   │   └── form/index.tsx   # 新建/编辑宝宝
│   │   └── user/index.tsx       # 我的（TabBar page）
│   ├── components/
│   │   ├── BabyCard/            # 宝宝卡片
│   │   ├── RecipeCard/          # 食谱卡片
│   │   ├── MealPeriodTag/       # 餐次标签（早/午/晚/加餐）
│   │   └── NutritionBar/        # 营养进度条
│   ├── services/                # 对应 Android remote/api
│   │   ├── request.ts           # 基础请求封装（JWT 拦截、错误处理）
│   │   ├── authService.ts       # 对应 AuthApiService.kt
│   │   ├── babyService.ts       # 对应 BabyApiService.kt
│   │   ├── recipeService.ts     # 对应 RecipeApiService.kt
│   │   └── planService.ts       # 对应 PlanApiService.kt
│   ├── models/                  # TypeScript 接口，对应 Android domain/model
│   │   ├── auth.ts              # LoginRequest, LoginResponse, User
│   │   ├── baby.ts              # Baby, AllergyItem, PreferenceItem
│   │   ├── recipe.ts            # Recipe, CandidateRecipesRequest/Response
│   │   └── plan.ts              # Plan, PlanWithRecipe, MealPeriod, NutritionIntake
│   ├── store/                   # 对应 Android ViewModel + UiState
│   │   ├── authStore.ts         # 认证状态（token, user, isLoggedIn）
│   │   ├── babyStore.ts         # 宝宝列表 + 选中宝宝
│   │   └── homeStore.ts         # 今日计划 + 营养摄入
│   ├── utils/
│   │   ├── date.ts              # 日期格式化、月龄计算
│   │   └── storage.ts           # Token 读写封装
│   └── constants/
│       ├── api.ts               # BASE_URL + 端点常量
│       └── theme.ts             # 暖橙色主题（复用 Android Color.kt 色值）
├── app.tsx                      # 全局配置、登录拦截
├── app.config.ts                # TabBar 配置、页面路由注册
├── app.scss                     # NutUI 主题变量覆盖（--nutui-color-primary: #FF9A3C）
├── project.config.json          # 微信开发者工具配置
├── tsconfig.json
└── package.json
```

---

## 设计系统

直接复用 Android `presentation/theme/Color.kt` 色值：

```scss
// app.scss — NutUI 主题变量覆盖
:root {
  --nutui-color-primary: #FF9A3C;
  --nutui-color-primary-stop-1: #FFB067;
  --nutui-color-primary-stop-2: #E67E22;
  --nutui-background-color: #FFF9F0;
}
```

| Token | 值 | 用途 |
|-------|-----|------|
| primary | #FF9A3C | 按钮、高亮、图标 |
| primaryLight | #FFB067 | 渐变、悬停 |
| background | #FFF9F0 | 页面背景 |
| textMain | #3D2B1F | 正文 |
| success | #78C6A3 | 蛋白质营养色、完成状态 |
| warning | #FFD166 | 午餐标识 |

---

## 数据流架构

```
Page (TSX)
  ↓ 调用 store actions
Zustand Store
  ↓ 调用 services
API Service (request.ts)
  ↓ Taro.request + JWT header
后端 REST API (同 Android)
```

**JWT 拦截流程（对应 Android JwtAuthInterceptor + TokenRefreshAuthenticator）：**
1. 请求前从 Storage 读取 token，注入 Authorization: Bearer {token}
2. 收到 401 → 调用 `POST /api/v1/auth/refresh` → 更新 Storage → 重试原请求
3. 刷新也失败 → 清除 token → 跳转登录页

---

## 验证方案

1. **微信开发者工具**：导入 `BabyFoodMini/`，编译预览，确认 TabBar 和页面路由正常
2. **认证流程**：注册 → 登录 → token 写入 Storage → 首页加载今日计划
3. **宝宝管理**：新建宝宝（设置月龄和过敏食材）→ 列表显示 → 编辑保存
4. **AI 食谱推荐**：进入食谱页 → 点击"AI 推荐" → 按月龄和过敏过滤后显示四餐候选
5. **今日菜单**：从推荐候选添加食谱到今日计划 → 营养摘要更新
6. **网络异常**：断网时显示友好错误提示，恢复后可重试

---

## 不在 MVP 范围内（后续迭代）

- 生长曲线图（需 Vico Charts 对应的小程序图表组件）
- 健康档案与 AI 分析（Health Records）
- 食材库存管理（Inventory）
- 积分系统（Points）
- 微信一键授权登录（需配置服务器域名和 openid 映射）
- 云端数据同步（Sync）
- 图片识别（Image Analysis）
