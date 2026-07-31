# BabyFood Mini Program Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a WeChat Mini Program MVP for BabyFood (宝宝辅食智能助手) with authentication, baby profile management, recipe browsing with AI recommendations, and today's meal plan dashboard.

**Architecture:** Taro 4 + React + TypeScript, layered MVVM mirroring the Android project: `pages` (View) → `store` (Zustand, ViewModel) → `services` (Repository) → `models` (Domain). Same REST API backend as Android: `http://39.108.143.232:8080/`.

**Tech Stack:** Taro 4.x, React 18, TypeScript 5, NutUI-React-Taro, Zustand 4, SCSS, Jest

---

## File Map

```
D:\workspace\BabyFoodMini\             ← new project root (alongside BabyFood\)
├── src/
│   ├── pages/
│   │   ├── auth/login/index.tsx       # Login page
│   │   ├── auth/register/index.tsx    # Register page
│   │   ├── home/index.tsx             # Today's menu (TabBar)
│   │   ├── recipes/index/index.tsx    # Recipe list + AI rec (TabBar)
│   │   ├── recipes/detail/index.tsx   # Recipe detail
│   │   ├── baby/index/index.tsx       # Baby list (TabBar)
│   │   ├── baby/form/index.tsx        # Create/Edit baby
│   │   └── user/index.tsx             # Profile (TabBar)
│   ├── custom-tab-bar/index.tsx       # Custom tab bar (NutUI icons, no PNG needed)
│   ├── components/
│   │   ├── RecipeCard/index.tsx
│   │   ├── BabyCard/index.tsx
│   │   ├── MealPeriodTag/index.tsx
│   │   └── NutritionBar/index.tsx
│   ├── services/
│   │   ├── request.ts                 # JWT interceptor + base HTTP
│   │   ├── authService.ts
│   │   ├── babyService.ts
│   │   ├── recipeService.ts
│   │   └── planService.ts
│   ├── models/
│   │   ├── auth.ts
│   │   ├── baby.ts
│   │   ├── recipe.ts
│   │   └── plan.ts
│   ├── store/
│   │   ├── authStore.ts
│   │   ├── babyStore.ts
│   │   └── homeStore.ts
│   ├── utils/
│   │   ├── date.ts
│   │   └── storage.ts
│   ├── constants/
│   │   ├── api.ts
│   │   └── theme.ts
│   ├── app.tsx
│   ├── app.config.ts
│   └── app.scss
├── __tests__/
│   ├── services/request.test.ts
│   ├── services/authService.test.ts
│   └── store/authStore.test.ts
├── project.config.json
├── babel.config.js
└── tsconfig.json
```

---

## Task 1: Project Initialization

**Files:**
- Create: `D:\workspace\BabyFoodMini\` (entire project via Taro CLI)

- [ ] **Step 1: Run Taro CLI init**

```bash
cd /d/workspace
npx @tarojs/cli@latest init BabyFoodMini
```

When prompted, answer:
```
项目名称: BabyFoodMini
项目介绍: 宝宝辅食智能助手微信小程序
框架: React
TypeScript: Yes
CSS: Sass/Scss
模板源: （选 Gitee 或 Github 均可）
模板: default
```

- [ ] **Step 2: Install additional dependencies**

```bash
cd /d/workspace/BabyFoodMini
npm install zustand @nutui/nutui-react-taro @nutui/icons-react-taro
npm install --save-dev babel-plugin-import
```

- [ ] **Step 3: Verify initial build**

```bash
npm run dev:weapp
```

Expected: Build completes without errors, generates `dist/` folder.

- [ ] **Step 4: Initialize git**

```bash
git init
echo "dist/\nnode_modules/\n.taro-cache/" > .gitignore
git add .
git commit -m "chore: init Taro React TS project"
```

---

## Task 2: Project Configuration Files

**Files:**
- Modify: `src/app.config.ts`
- Modify: `babel.config.js`
- Modify: `project.config.json`
- Modify: `tsconfig.json`

- [ ] **Step 1: Write app.config.ts**

Replace `src/app.config.ts` entirely:

```typescript
export default defineAppConfig({
  pages: [
    'pages/home/index',
    'pages/recipes/index/index',
    'pages/baby/index/index',
    'pages/user/index',
    'pages/auth/login/index',
    'pages/auth/register/index',
    'pages/recipes/detail/index',
    'pages/baby/form/index',
  ],
  tabBar: {
    custom: true,
    color: '#999999',
    selectedColor: '#FF9A3C',
    backgroundColor: '#FFFFFF',
    list: [
      { pagePath: 'pages/home/index', text: '首页' },
      { pagePath: 'pages/recipes/index/index', text: '食谱' },
      { pagePath: 'pages/baby/index/index', text: '宝宝' },
      { pagePath: 'pages/user/index', text: '我的' },
    ],
  },
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#FF9A3C',
    navigationBarTitleText: '宝宝辅食',
    navigationBarTextStyle: 'white',
  },
})
```

- [ ] **Step 2: Update babel.config.js for NutUI on-demand import**

Replace `babel.config.js` entirely:

```javascript
module.exports = {
  presets: [['taro', { framework: 'react', ts: true }]],
  plugins: [
    [
      'import',
      {
        libraryName: '@nutui/nutui-react-taro',
        libraryDirectory: 'dist/esm',
        style: true,
        camel2DashComponentName: false,
      },
      'nutui-react-taro',
    ],
  ],
}
```

- [ ] **Step 3: Update project.config.json**

Set appid (use tourist/test appid for now):

```json
{
  "appid": "touristappid",
  "projectname": "BabyFoodMini",
  "description": "宝宝辅食智能助手微信小程序",
  "setting": {
    "urlCheck": false,
    "es6": true,
    "enhance": true,
    "postcss": true,
    "preloadBackgroundData": false,
    "minified": false,
    "newFeature": false,
    "coverView": true,
    "nodeModules": false,
    "autoAudits": false,
    "showShadowRootInWxmlPanel": true,
    "scopeDataCheck": false,
    "uglifyFileName": false,
    "checkInvalidKey": true,
    "checkSiteMap": false,
    "uploadWithSourceMap": true,
    "compileHotReLoad": false,
    "lazyloadPlaceholderEnable": false,
    "useMultiFrameRuntime": true,
    "useApiHook": true,
    "useApiEventHook": true,
    "condition": false
  },
  "compileType": "miniprogram",
  "libVersion": "3.0.0",
  "srcMiniprogramRoot": "dist/",
  "condition": {}
}
```

- [ ] **Step 4: Update tsconfig.json to add strict mode and path aliases**

Merge into existing `tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2017",
    "module": "CommonJS",
    "strict": true,
    "jsx": "react-jsx",
    "moduleResolution": "node",
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    },
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "experimentalDecorators": true,
    "noEmit": true,
    "resolveJsonModule": true,
    "lib": ["ES2017", "DOM"]
  },
  "include": ["src", "__tests__"]
}
```

- [ ] **Step 5: Create page directory stubs so the build doesn't fail**

```bash
mkdir -p src/pages/auth/login src/pages/auth/register
mkdir -p src/pages/home
mkdir -p src/pages/recipes/index src/pages/recipes/detail
mkdir -p src/pages/baby/index src/pages/baby/form
mkdir -p src/pages/user
mkdir -p src/custom-tab-bar
mkdir -p src/components/RecipeCard src/components/BabyCard src/components/MealPeriodTag src/components/NutritionBar
mkdir -p src/services src/models src/store src/utils src/constants
mkdir -p __tests__/services __tests__/store
```

Create a stub for each page (just enough for the build):

`src/pages/home/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function Home() { return <View>Home</View> }
```

`src/pages/recipes/index/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function Recipes() { return <View>Recipes</View> }
```

`src/pages/baby/index/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function Baby() { return <View>Baby</View> }
```

`src/pages/user/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function User() { return <View>User</View> }
```

`src/pages/auth/login/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function Login() { return <View>Login</View> }
```

`src/pages/auth/register/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function Register() { return <View>Register</View> }
```

`src/pages/recipes/detail/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function RecipeDetail() { return <View>RecipeDetail</View> }
```

`src/pages/baby/form/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function BabyForm() { return <View>BabyForm</View> }
```

`src/custom-tab-bar/index.tsx`:
```tsx
import { View } from '@tarojs/components'
export default function CustomTabBar() { return <View /> }
```

- [ ] **Step 6: Verify build with all pages**

```bash
npm run dev:weapp
```

Expected: Build completes, `dist/` has all page directories.

- [ ] **Step 7: Commit**

```bash
git add .
git commit -m "chore: configure app pages, tabBar, babel for NutUI"
```

---

## Task 3: Constants, Theme & Utilities

**Files:**
- Create: `src/constants/api.ts`
- Create: `src/constants/theme.ts`
- Create: `src/utils/storage.ts`
- Create: `src/utils/date.ts`
- Modify: `src/app.scss`

- [ ] **Step 1: Create src/constants/api.ts**

```typescript
export const BASE_URL = 'http://39.108.143.232:8080'

export const API = {
  auth: {
    login: '/api/v1/auth/login',
    register: '/api/v1/auth/register',
    logout: '/api/v1/auth/logout',
    refresh: '/api/v1/auth/refresh',
  },
  babies: {
    list: '/api/v1/babies',
    detail: (id: number) => `/api/v1/babies/${id}`,
  },
  recipes: {
    list: '/api/v1/recipes',
    detail: (id: number) => `/api/v1/recipes/${id}`,
    candidates: '/api/v1/recipes/candidates',
  },
  plans: {
    list: '/api/v1/plans',
    detail: (id: number) => `/api/v1/plans/${id}`,
    batch: '/api/v1/plans/batch',
  },
}
```

- [ ] **Step 2: Create src/constants/theme.ts**

```typescript
// Mirrors Android presentation/theme/Color.kt
export const colors = {
  primary: '#FF9A3C',
  primaryLight: '#FFB067',
  primaryDark: '#E67E22',
  secondary: '#FFD166',
  background: '#FFF9F0',
  surface: '#FFFFFF',
  textMain: '#3D2B1F',
  textSecondary: '#7A6055',
  textHint: '#B0A098',
  success: '#78C6A3',
  warning: '#FFD166',
  error: '#FF6B6B',
  info: '#87CEEB',
  // Nutrition colors
  protein: '#78C6A3',
  calcium: '#8ECAE6',
  calories: '#FFB067',
  iron: '#FF9A3C',
  // Meal period colors
  breakfast: '#FFB347',
  lunch: '#FFD166',
  dinner: '#E67E22',
  snack: '#78C6A3',
}

export const MealPeriodColor: Record<string, string> = {
  BREAKFAST: colors.breakfast,
  LUNCH: colors.lunch,
  DINNER: colors.dinner,
  SNACK: colors.snack,
}

export const MealPeriodLabel: Record<string, string> = {
  BREAKFAST: '早餐',
  LUNCH: '午餐',
  DINNER: '晚餐',
  SNACK: '加餐',
}
```

- [ ] **Step 3: Create src/utils/storage.ts**

```typescript
import Taro from '@tarojs/taro'

const TOKEN_KEY = 'babyfood_token'
const USER_KEY = 'babyfood_user'

export const storage = {
  getToken(): string | null {
    return Taro.getStorageSync(TOKEN_KEY) || null
  },
  setToken(token: string): void {
    Taro.setStorageSync(TOKEN_KEY, token)
  },
  removeToken(): void {
    Taro.removeStorageSync(TOKEN_KEY)
  },
  getUser<T>(): T | null {
    const data = Taro.getStorageSync(USER_KEY)
    return data ? (data as T) : null
  },
  setUser<T>(user: T): void {
    Taro.setStorageSync(USER_KEY, user)
  },
  removeUser(): void {
    Taro.removeStorageSync(USER_KEY)
  },
  clear(): void {
    Taro.clearStorageSync()
  },
}
```

- [ ] **Step 4: Create src/utils/date.ts**

```typescript
// Calculate baby age in months from birth date string (ISO 8601)
export function calcAgeInMonths(birthDate: string): number {
  const birth = new Date(birthDate)
  const now = new Date()
  return (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
}

// Format date to YYYY-MM-DD
export function formatDate(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

// Format ISO date string to readable Chinese format: YYYY年MM月DD日
export function formatDateCN(dateStr: string): string {
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

// Get today as YYYY-MM-DD
export function today(): string {
  return formatDate(new Date())
}

// Get age label string like "6个月"
export function ageLabel(birthDate: string): string {
  const months = calcAgeInMonths(birthDate)
  if (months < 12) return `${months}个月`
  const years = Math.floor(months / 12)
  const rem = months % 12
  return rem > 0 ? `${years}岁${rem}个月` : `${years}岁`
}
```

- [ ] **Step 5: Update src/app.scss with NutUI theme override**

Replace `src/app.scss` entirely:

```scss
// NutUI theme variable override — must be before any NutUI import
:root {
  --nutui-color-primary: #FF9A3C;
  --nutui-color-primary-stop-1: #FFB067;
  --nutui-color-primary-stop-2: #E67E22;
  --nutui-color-primary-light: #FFF3E8;
  --nutui-button-primary-background-color: #FF9A3C;
  --nutui-button-primary-border-color: #FF9A3C;
}

page {
  background-color: #FFF9F0;
  color: #3D2B1F;
  font-family: -apple-system, 'PingFang SC', 'Helvetica Neue', sans-serif;
}

// Global utility classes
.card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(61, 43, 31, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #3D2B1F;
  margin-bottom: 12px;
}

.text-hint {
  color: #B0A098;
  font-size: 13px;
}
```

- [ ] **Step 6: Commit**

```bash
git add src/constants/ src/utils/ src/app.scss
git commit -m "feat: add constants, theme, storage and date utilities"
```

---

## Task 4: TypeScript Models

**Files:**
- Create: `src/models/auth.ts`
- Create: `src/models/baby.ts`
- Create: `src/models/recipe.ts`
- Create: `src/models/plan.ts`

These mirror Android's `domain/model/` classes.

- [ ] **Step 1: Create src/models/auth.ts**

```typescript
export interface LoginRequest {
  phone: string
  password: string
}

export interface RegisterRequest {
  phone: string
  password: string
  nickname: string
}

export interface LoginResponse {
  token: string
  refreshToken: string
  user: User
}

export interface User {
  id: number
  phone: string
  nickname: string
  avatarUrl?: string
  createdAt: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  token: string
  refreshToken: string
}
```

- [ ] **Step 2: Create src/models/baby.ts**

```typescript
export interface Baby {
  id: number
  userId: number
  name: string
  birthDate: string          // ISO 8601: "YYYY-MM-DD"
  ageInMonths: number
  gender?: 'MALE' | 'FEMALE'
  weight?: number            // kg
  height?: number            // cm
  allergies: string[]        // e.g. ["牛奶", "鸡蛋"]
  preferences: string[]      // e.g. ["偏甜", "软烂"]
  createdAt: string
  updatedAt: string
}

export interface CreateBabyRequest {
  name: string
  birthDate: string
  gender?: 'MALE' | 'FEMALE'
  allergies: string[]
  preferences: string[]
}

export interface UpdateBabyRequest extends Partial<CreateBabyRequest> {}

// Common allergy options matching Android's data
export const ALLERGY_OPTIONS = [
  '牛奶', '鸡蛋', '大豆', '花生', '坚果', '小麦', '鱼', '贝壳类',
  '芝麻', '猕猴桃', '桃子', '芒果',
]

export const PREFERENCE_OPTIONS = [
  '偏甜', '偏咸', '软烂', '颗粒感', '清淡',
]
```

- [ ] **Step 3: Create src/models/recipe.ts**

```typescript
export interface Recipe {
  id: number
  name: string
  description?: string
  minAgeMonths: number
  maxAgeMonths?: number
  textureType: TextureType
  ingredients: Ingredient[]
  steps: string[]
  nutrition: Nutrition
  tags: string[]
  imageUrl?: string
  isIronRich: boolean
  safetyNotes?: string
  createdAt: string
}

export type TextureType = 'PUREE' | 'MASH' | 'MINCED' | 'SOFT_PIECES' | 'REGULAR'

export const TextureTypeLabel: Record<TextureType, string> = {
  PUREE: '泥状',
  MASH: '泥糊状',
  MINCED: '细碎',
  SOFT_PIECES: '软块',
  REGULAR: '普通',
}

export interface Ingredient {
  name: string
  amount: string
  unit: string
}

export interface Nutrition {
  calories: number     // kcal
  protein: number      // g
  fat: number          // g
  carbohydrates: number // g
  calcium: number      // mg
  iron: number         // mg
}

// AI Candidate Recipes — mirrors Android CandidateRecipesRequest/Response
export interface CandidateRecipesRequest {
  ageInMonths: number
  allergies: string[]
  preferences: string[]
  availableIngredients: string[]
  avoidIngredients: string[]
  useAvailableIngredientsOnly: boolean
}

export interface CandidateRecipeItem {
  id: number
  name: string
  textureType: TextureType
  minAgeMonths: number
  mainIngredients: string[]
  nutrition: Nutrition
  imageUrl?: string
}

export interface CandidateRecipesResponse {
  breakfast: CandidateRecipeItem[]
  lunch: CandidateRecipeItem[]
  dinner: CandidateRecipeItem[]
  snack: CandidateRecipeItem[]
  totalCount: number
  filteredCount: number
}
```

- [ ] **Step 4: Create src/models/plan.ts**

```typescript
import { Recipe } from './recipe'

export type MealPeriod = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK'
export type PlanStatus = 'PLANNED' | 'COMPLETED' | 'SKIPPED'

export interface Plan {
  id: number
  babyId: number
  recipeId: number
  date: string            // YYYY-MM-DD
  mealPeriod: MealPeriod
  status: PlanStatus
  feedback?: MealFeedback
  createdAt: string
}

export type MealFeedback = 'LOVED' | 'LIKED' | 'NEUTRAL' | 'DISLIKED' | 'REFUSED'

export interface PlanWithRecipe extends Plan {
  recipe: Recipe
}

export interface CreatePlanRequest {
  babyId: number
  recipeId: number
  date: string
  mealPeriod: MealPeriod
}

export interface NutritionIntake {
  calories: number
  protein: number
  fat: number
  carbohydrates: number
  calcium: number
  iron: number
}

export const EMPTY_NUTRITION: NutritionIntake = {
  calories: 0, protein: 0, fat: 0, carbohydrates: 0, calcium: 0, iron: 0,
}
```

- [ ] **Step 5: Commit**

```bash
git add src/models/
git commit -m "feat: add TypeScript domain models (auth, baby, recipe, plan)"
```

---

## Task 5: HTTP Request Layer

**Files:**
- Create: `src/services/request.ts`
- Create: `__tests__/services/request.test.ts`

- [ ] **Step 1: Write failing test**

Create `__tests__/services/request.test.ts`:

```typescript
jest.mock('@tarojs/taro', () => ({
  request: jest.fn(),
  getStorageSync: jest.fn(() => null),
  setStorageSync: jest.fn(),
  removeStorageSync: jest.fn(),
  navigateTo: jest.fn(),
  reLaunch: jest.fn(),
}))

import Taro from '@tarojs/taro'
import { http } from '../../src/services/request'

const mockRequest = Taro.request as jest.Mock

describe('http request', () => {
  beforeEach(() => jest.clearAllMocks())

  test('adds Authorization header when token exists', async () => {
    ;(Taro.getStorageSync as jest.Mock).mockReturnValue('test-token')
    mockRequest.mockResolvedValue({ statusCode: 200, data: { result: 'ok' } })

    await http<{ result: string }>({ url: '/api/test', method: 'GET' })

    expect(mockRequest).toHaveBeenCalledWith(
      expect.objectContaining({
        header: expect.objectContaining({ Authorization: 'Bearer test-token' }),
      })
    )
  })

  test('does not add Authorization header when no token', async () => {
    ;(Taro.getStorageSync as jest.Mock).mockReturnValue(null)
    mockRequest.mockResolvedValue({ statusCode: 200, data: { ok: true } })

    await http({ url: '/api/test', method: 'GET' })

    const call = mockRequest.mock.calls[0][0]
    expect(call.header.Authorization).toBeUndefined()
  })

  test('throws error on 400+ status', async () => {
    ;(Taro.getStorageSync as jest.Mock).mockReturnValue(null)
    mockRequest.mockResolvedValue({ statusCode: 404, data: { message: 'Not found' } })

    await expect(http({ url: '/api/missing', method: 'GET' })).rejects.toThrow('Not found')
  })
})
```

- [ ] **Step 2: Run test to verify it fails**

```bash
npx jest __tests__/services/request.test.ts
```

Expected: FAIL — `Cannot find module '../../src/services/request'`

- [ ] **Step 3: Implement src/services/request.ts**

```typescript
import Taro from '@tarojs/taro'
import { BASE_URL, API } from '../constants/api'
import { storage } from '../utils/storage'
import { RefreshTokenResponse } from '../models/auth'

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  data?: Record<string, unknown> | unknown[]
}

let isRefreshing = false
let pendingQueue: Array<(token: string) => void> = []

async function refreshToken(): Promise<string> {
  const refreshToken = Taro.getStorageSync('babyfood_refresh_token') as string | null
  if (!refreshToken) throw new Error('No refresh token')

  const res = await Taro.request({
    url: `${BASE_URL}${API.auth.refresh}`,
    method: 'POST',
    data: { refreshToken },
    header: { 'Content-Type': 'application/json' },
  })

  if (res.statusCode !== 200) throw new Error('Token refresh failed')
  const data = res.data as RefreshTokenResponse
  storage.setToken(data.token)
  Taro.setStorageSync('babyfood_refresh_token', data.refreshToken)
  return data.token
}

export async function http<T>(options: RequestOptions): Promise<T> {
  const token = storage.getToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const response = await Taro.request({
    url: `${BASE_URL}${options.url}`,
    method: options.method ?? 'GET',
    data: options.data,
    header: headers,
  })

  if (response.statusCode === 401) {
    // Try to refresh once
    if (!isRefreshing) {
      isRefreshing = true
      try {
        const newToken = await refreshToken()
        isRefreshing = false
        pendingQueue.forEach((resolve) => resolve(newToken))
        pendingQueue = []
        // Retry original request with new token
        const retryResponse = await Taro.request({
          url: `${BASE_URL}${options.url}`,
          method: options.method ?? 'GET',
          data: options.data,
          header: { ...headers, Authorization: `Bearer ${newToken}` },
        })
        if (retryResponse.statusCode >= 400) {
          throw new Error((retryResponse.data as { message?: string })?.message ?? '请求失败')
        }
        return retryResponse.data as T
      } catch {
        isRefreshing = false
        pendingQueue = []
        storage.clear()
        Taro.reLaunch({ url: '/pages/auth/login/index' })
        throw new Error('登录已过期，请重新登录')
      }
    } else {
      // Wait for current refresh
      return new Promise<T>((resolve, reject) => {
        pendingQueue.push(async (newToken) => {
          try {
            const retryResponse = await Taro.request({
              url: `${BASE_URL}${options.url}`,
              method: options.method ?? 'GET',
              data: options.data,
              header: { ...headers, Authorization: `Bearer ${newToken}` },
            })
            resolve(retryResponse.data as T)
          } catch (e) {
            reject(e)
          }
        })
      })
    }
  }

  if (response.statusCode >= 400) {
    const msg = (response.data as { message?: string })?.message ?? '请求失败'
    throw new Error(msg)
  }

  return response.data as T
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
npx jest __tests__/services/request.test.ts
```

Expected: PASS (3 tests)

- [ ] **Step 5: Commit**

```bash
git add src/services/request.ts __tests__/services/request.test.ts
git commit -m "feat: add JWT-aware HTTP request layer with 401 auto-refresh"
```

---

## Task 6: API Services

**Files:**
- Create: `src/services/authService.ts`
- Create: `src/services/babyService.ts`
- Create: `src/services/recipeService.ts`
- Create: `src/services/planService.ts`
- Create: `__tests__/services/authService.test.ts`

- [ ] **Step 1: Write failing test for authService**

Create `__tests__/services/authService.test.ts`:

```typescript
jest.mock('../../src/services/request', () => ({
  http: jest.fn(),
}))
jest.mock('@tarojs/taro', () => ({
  setStorageSync: jest.fn(),
  getStorageSync: jest.fn(),
  removeStorageSync: jest.fn(),
}))

import { http } from '../../src/services/request'
import { authService } from '../../src/services/authService'

const mockHttp = http as jest.Mock

describe('authService', () => {
  beforeEach(() => jest.clearAllMocks())

  test('login calls correct endpoint and returns response', async () => {
    const mockResponse = { token: 'jwt123', refreshToken: 'ref456', user: { id: 1, phone: '13800000000', nickname: 'Test' } }
    mockHttp.mockResolvedValue(mockResponse)

    const result = await authService.login({ phone: '13800000000', password: 'pass123' })

    expect(mockHttp).toHaveBeenCalledWith({
      url: '/api/v1/auth/login',
      method: 'POST',
      data: { phone: '13800000000', password: 'pass123' },
    })
    expect(result.token).toBe('jwt123')
  })
})
```

- [ ] **Step 2: Run test to verify it fails**

```bash
npx jest __tests__/services/authService.test.ts
```

Expected: FAIL — `Cannot find module '../../src/services/authService'`

- [ ] **Step 3: Create src/services/authService.ts**

```typescript
import { http } from './request'
import { API } from '../constants/api'
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../models/auth'

export const authService = {
  async login(req: LoginRequest): Promise<LoginResponse> {
    return http<LoginResponse>({ url: API.auth.login, method: 'POST', data: req as unknown as Record<string, unknown> })
  },

  async register(req: RegisterRequest): Promise<LoginResponse> {
    return http<LoginResponse>({ url: API.auth.register, method: 'POST', data: req as unknown as Record<string, unknown> })
  },

  async logout(token: string): Promise<void> {
    await http({ url: API.auth.logout, method: 'POST', data: { token } as unknown as Record<string, unknown> })
  },

  async getMe(): Promise<User> {
    return http<User>({ url: '/api/v1/auth/me', method: 'GET' })
  },
}
```

- [ ] **Step 4: Create src/services/babyService.ts**

```typescript
import { http } from './request'
import { API } from '../constants/api'
import { Baby, CreateBabyRequest, UpdateBabyRequest } from '../models/baby'

export const babyService = {
  async list(): Promise<Baby[]> {
    return http<Baby[]>({ url: API.babies.list, method: 'GET' })
  },

  async get(id: number): Promise<Baby> {
    return http<Baby>({ url: API.babies.detail(id), method: 'GET' })
  },

  async create(req: CreateBabyRequest): Promise<Baby> {
    return http<Baby>({ url: API.babies.list, method: 'POST', data: req as unknown as Record<string, unknown> })
  },

  async update(id: number, req: UpdateBabyRequest): Promise<Baby> {
    return http<Baby>({ url: API.babies.detail(id), method: 'PUT', data: req as unknown as Record<string, unknown> })
  },

  async delete(id: number): Promise<void> {
    await http({ url: API.babies.detail(id), method: 'DELETE' })
  },
}
```

- [ ] **Step 5: Create src/services/recipeService.ts**

```typescript
import { http } from './request'
import { API } from '../constants/api'
import { Recipe, CandidateRecipesRequest, CandidateRecipesResponse } from '../models/recipe'

export const recipeService = {
  async list(params?: { ageInMonths?: number; page?: number; pageSize?: number }): Promise<Recipe[]> {
    const query = params
      ? '?' + Object.entries(params).map(([k, v]) => `${k}=${v}`).join('&')
      : ''
    return http<Recipe[]>({ url: API.recipes.list + query, method: 'GET' })
  },

  async get(id: number): Promise<Recipe> {
    return http<Recipe>({ url: API.recipes.detail(id), method: 'GET' })
  },

  async getCandidates(req: CandidateRecipesRequest): Promise<CandidateRecipesResponse> {
    return http<CandidateRecipesResponse>({
      url: API.recipes.candidates,
      method: 'POST',
      data: req as unknown as Record<string, unknown>,
    })
  },
}
```

- [ ] **Step 6: Create src/services/planService.ts**

```typescript
import { http } from './request'
import { API } from '../constants/api'
import { Plan, PlanWithRecipe, CreatePlanRequest } from '../models/plan'

export const planService = {
  async list(params: { babyId: number; date: string }): Promise<PlanWithRecipe[]> {
    return http<PlanWithRecipe[]>({
      url: `${API.plans.list}?babyId=${params.babyId}&date=${params.date}`,
      method: 'GET',
    })
  },

  async create(req: CreatePlanRequest): Promise<Plan> {
    return http<Plan>({ url: API.plans.list, method: 'POST', data: req as unknown as Record<string, unknown> })
  },

  async delete(id: number): Promise<void> {
    await http({ url: API.plans.detail(id), method: 'DELETE' })
  },

  async updateStatus(id: number, status: string): Promise<Plan> {
    return http<Plan>({
      url: API.plans.detail(id),
      method: 'PUT',
      data: { status } as unknown as Record<string, unknown>,
    })
  },
}
```

- [ ] **Step 7: Run authService test to verify it passes**

```bash
npx jest __tests__/services/authService.test.ts
```

Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/services/ __tests__/services/
git commit -m "feat: add API services (auth, baby, recipe, plan)"
```

---

## Task 7: Zustand Stores

**Files:**
- Create: `src/store/authStore.ts`
- Create: `src/store/babyStore.ts`
- Create: `src/store/homeStore.ts`
- Create: `__tests__/store/authStore.test.ts`

- [ ] **Step 1: Write failing test for authStore**

Create `__tests__/store/authStore.test.ts`:

```typescript
jest.mock('../../src/services/authService', () => ({
  authService: {
    login: jest.fn(),
    logout: jest.fn(),
  },
}))
jest.mock('@tarojs/taro', () => ({
  setStorageSync: jest.fn(),
  getStorageSync: jest.fn(() => null),
  removeStorageSync: jest.fn(),
  clearStorageSync: jest.fn(),
  reLaunch: jest.fn(),
  navigateTo: jest.fn(),
}))
jest.mock('../../src/utils/storage', () => ({
  storage: {
    setToken: jest.fn(),
    removeToken: jest.fn(),
    setUser: jest.fn(),
    removeUser: jest.fn(),
    getToken: jest.fn(() => null),
    getUser: jest.fn(() => null),
    clear: jest.fn(),
  },
}))

import { authService } from '../../src/services/authService'
import { useAuthStore } from '../../src/store/authStore'

const mockLogin = authService.login as jest.Mock

describe('authStore', () => {
  beforeEach(() => {
    useAuthStore.setState({ token: null, user: null, isLoggedIn: false })
    jest.clearAllMocks()
  })

  test('login sets token and user on success', async () => {
    mockLogin.mockResolvedValue({
      token: 'jwt123',
      refreshToken: 'ref456',
      user: { id: 1, phone: '13800000000', nickname: 'TestUser' },
    })

    await useAuthStore.getState().login('13800000000', 'password')

    const state = useAuthStore.getState()
    expect(state.isLoggedIn).toBe(true)
    expect(state.token).toBe('jwt123')
    expect(state.user?.nickname).toBe('TestUser')
  })

  test('logout clears state', () => {
    useAuthStore.setState({
      token: 'jwt123',
      user: { id: 1, phone: '13800000000', nickname: 'Test', createdAt: '' },
      isLoggedIn: true,
    })

    useAuthStore.getState().logout()

    const state = useAuthStore.getState()
    expect(state.isLoggedIn).toBe(false)
    expect(state.token).toBeNull()
    expect(state.user).toBeNull()
  })
})
```

- [ ] **Step 2: Run test to verify it fails**

```bash
npx jest __tests__/store/authStore.test.ts
```

Expected: FAIL — `Cannot find module '../../src/store/authStore'`

- [ ] **Step 3: Create src/store/authStore.ts**

```typescript
import { create } from 'zustand'
import { authService } from '../services/authService'
import { storage } from '../utils/storage'
import { User } from '../models/auth'
import Taro from '@tarojs/taro'

interface AuthState {
  token: string | null
  user: User | null
  isLoggedIn: boolean
  isLoading: boolean
  error: string | null
}

interface AuthActions {
  login: (phone: string, password: string) => Promise<void>
  register: (phone: string, password: string, nickname: string) => Promise<void>
  logout: () => void
  initFromStorage: () => void
  clearError: () => void
}

export const useAuthStore = create<AuthState & AuthActions>((set, get) => ({
  token: null,
  user: null,
  isLoggedIn: false,
  isLoading: false,
  error: null,

  initFromStorage: () => {
    const token = storage.getToken()
    const user = storage.getUser<User>()
    if (token && user) {
      set({ token, user, isLoggedIn: true })
    }
  },

  login: async (phone, password) => {
    set({ isLoading: true, error: null })
    try {
      const response = await authService.login({ phone, password })
      storage.setToken(response.token)
      Taro.setStorageSync('babyfood_refresh_token', response.refreshToken)
      storage.setUser(response.user)
      set({ token: response.token, user: response.user, isLoggedIn: true, isLoading: false })
    } catch (err) {
      set({ isLoading: false, error: (err as Error).message })
      throw err
    }
  },

  register: async (phone, password, nickname) => {
    set({ isLoading: true, error: null })
    try {
      const response = await authService.register({ phone, password, nickname })
      storage.setToken(response.token)
      Taro.setStorageSync('babyfood_refresh_token', response.refreshToken)
      storage.setUser(response.user)
      set({ token: response.token, user: response.user, isLoggedIn: true, isLoading: false })
    } catch (err) {
      set({ isLoading: false, error: (err as Error).message })
      throw err
    }
  },

  logout: () => {
    storage.clear()
    set({ token: null, user: null, isLoggedIn: false })
    Taro.reLaunch({ url: '/pages/auth/login/index' })
  },

  clearError: () => set({ error: null }),
}))
```

- [ ] **Step 4: Create src/store/babyStore.ts**

```typescript
import { create } from 'zustand'
import { babyService } from '../services/babyService'
import { Baby, CreateBabyRequest, UpdateBabyRequest } from '../models/baby'
import Taro from '@tarojs/taro'

const SELECTED_BABY_KEY = 'babyfood_selected_baby_id'

interface BabyState {
  babies: Baby[]
  selectedBaby: Baby | null
  isLoading: boolean
  error: string | null
}

interface BabyActions {
  fetchBabies: () => Promise<void>
  selectBaby: (baby: Baby) => void
  createBaby: (req: CreateBabyRequest) => Promise<void>
  updateBaby: (id: number, req: UpdateBabyRequest) => Promise<void>
  deleteBaby: (id: number) => Promise<void>
  initSelectedBaby: (babies: Baby[]) => void
}

export const useBabyStore = create<BabyState & BabyActions>((set, get) => ({
  babies: [],
  selectedBaby: null,
  isLoading: false,
  error: null,

  initSelectedBaby: (babies) => {
    const savedId = Taro.getStorageSync(SELECTED_BABY_KEY) as number | null
    const found = savedId ? babies.find((b) => b.id === savedId) : null
    set({ selectedBaby: found ?? babies[0] ?? null })
  },

  selectBaby: (baby) => {
    Taro.setStorageSync(SELECTED_BABY_KEY, baby.id)
    set({ selectedBaby: baby })
  },

  fetchBabies: async () => {
    set({ isLoading: true, error: null })
    try {
      const babies = await babyService.list()
      const { initSelectedBaby, selectedBaby } = get()
      if (!selectedBaby) initSelectedBaby(babies)
      set({ babies, isLoading: false })
    } catch (err) {
      set({ isLoading: false, error: (err as Error).message })
    }
  },

  createBaby: async (req) => {
    const baby = await babyService.create(req)
    set((state) => ({
      babies: [...state.babies, baby],
      selectedBaby: state.selectedBaby ?? baby,
    }))
  },

  updateBaby: async (id, req) => {
    const updated = await babyService.update(id, req)
    set((state) => ({
      babies: state.babies.map((b) => (b.id === id ? updated : b)),
      selectedBaby: state.selectedBaby?.id === id ? updated : state.selectedBaby,
    }))
  },

  deleteBaby: async (id) => {
    await babyService.delete(id)
    set((state) => {
      const babies = state.babies.filter((b) => b.id !== id)
      const selectedBaby = state.selectedBaby?.id === id ? (babies[0] ?? null) : state.selectedBaby
      return { babies, selectedBaby }
    })
  },
}))
```

- [ ] **Step 5: Create src/store/homeStore.ts**

```typescript
import { create } from 'zustand'
import { planService } from '../services/planService'
import { recipeService } from '../services/recipeService'
import { PlanWithRecipe, NutritionIntake, EMPTY_NUTRITION, CreatePlanRequest, MealPeriod } from '../models/plan'
import { CandidateRecipesResponse } from '../models/recipe'
import { Baby } from '../models/baby'
import { today } from '../utils/date'

interface HomeState {
  date: string
  todayPlans: PlanWithRecipe[]
  candidates: CandidateRecipesResponse | null
  nutritionIntake: NutritionIntake
  isLoadingPlans: boolean
  isLoadingCandidates: boolean
  error: string | null
}

interface HomeActions {
  setDate: (date: string) => void
  fetchTodayPlans: (baby: Baby) => Promise<void>
  fetchCandidates: (baby: Baby) => Promise<void>
  addPlan: (req: CreatePlanRequest) => Promise<void>
  removePlan: (planId: number) => Promise<void>
}

function calcNutrition(plans: PlanWithRecipe[]): NutritionIntake {
  return plans.reduce((acc, plan) => {
    const n = plan.recipe.nutrition
    return {
      calories: acc.calories + n.calories,
      protein: acc.protein + n.protein,
      fat: acc.fat + n.fat,
      carbohydrates: acc.carbohydrates + n.carbohydrates,
      calcium: acc.calcium + n.calcium,
      iron: acc.iron + n.iron,
    }
  }, { ...EMPTY_NUTRITION })
}

export const useHomeStore = create<HomeState & HomeActions>((set, get) => ({
  date: today(),
  todayPlans: [],
  candidates: null,
  nutritionIntake: { ...EMPTY_NUTRITION },
  isLoadingPlans: false,
  isLoadingCandidates: false,
  error: null,

  setDate: (date) => set({ date }),

  fetchTodayPlans: async (baby) => {
    set({ isLoadingPlans: true, error: null })
    try {
      const plans = await planService.list({ babyId: baby.id, date: get().date })
      set({ todayPlans: plans, nutritionIntake: calcNutrition(plans), isLoadingPlans: false })
    } catch (err) {
      set({ isLoadingPlans: false, error: (err as Error).message })
    }
  },

  fetchCandidates: async (baby) => {
    set({ isLoadingCandidates: true })
    try {
      const candidates = await recipeService.getCandidates({
        ageInMonths: baby.ageInMonths,
        allergies: baby.allergies,
        preferences: baby.preferences,
        availableIngredients: [],
        avoidIngredients: [],
        useAvailableIngredientsOnly: false,
      })
      set({ candidates, isLoadingCandidates: false })
    } catch {
      set({ isLoadingCandidates: false })
    }
  },

  addPlan: async (req) => {
    const plan = await planService.create(req)
    // Reload plans to get the full PlanWithRecipe object
    const plans = await planService.list({ babyId: req.babyId, date: req.date })
    set({ todayPlans: plans, nutritionIntake: calcNutrition(plans) })
  },

  removePlan: async (planId) => {
    await planService.delete(planId)
    set((state) => {
      const todayPlans = state.todayPlans.filter((p) => p.id !== planId)
      return { todayPlans, nutritionIntake: calcNutrition(todayPlans) }
    })
  },
}))
```

- [ ] **Step 6: Run authStore test to verify it passes**

```bash
npx jest __tests__/store/authStore.test.ts
```

Expected: PASS (2 tests)

- [ ] **Step 7: Commit**

```bash
git add src/store/ __tests__/store/
git commit -m "feat: add Zustand stores (auth, baby, home)"
```

---

## Task 8: App Entry + Custom TabBar

**Files:**
- Modify: `src/app.tsx`
- Create: `src/custom-tab-bar/index.tsx`

- [ ] **Step 1: Rewrite src/app.tsx**

```tsx
import { useEffect } from 'react'
import Taro from '@tarojs/taro'
import { useAuthStore } from './store/authStore'
import './app.scss'
// NutUI global CSS — import once here
import '@nutui/nutui-react-taro/dist/style.css'

function App({ children }: { children: React.ReactNode }) {
  const { initFromStorage, isLoggedIn } = useAuthStore()

  useEffect(() => {
    initFromStorage()
  }, [])

  return <>{children}</>
}

export default App
```

- [ ] **Step 2: Create src/custom-tab-bar/index.tsx**

This uses NutUI's Tabbar component with icon imports — no PNG files required.

```tsx
import { Component } from 'react'
import Taro from '@tarojs/taro'
import { View } from '@tarojs/components'
import { Tabbar, TabbarItem } from '@nutui/nutui-react-taro'
import { Home, Search, My, User } from '@nutui/icons-react-taro'

interface State {
  selected: number
}

const TAB_PAGES = [
  '/pages/home/index',
  '/pages/recipes/index/index',
  '/pages/baby/index/index',
  '/pages/user/index',
]

export default class CustomTabBar extends Component<Record<string, never>, State> {
  state: State = { selected: 0 }

  switchTab(index: number) {
    this.setState({ selected: index })
    Taro.switchTab({ url: TAB_PAGES[index] })
  }

  render() {
    const { selected } = this.state
    return (
      <View style={{ position: 'fixed', bottom: 0, left: 0, right: 0, zIndex: 100 }}>
        <Tabbar value={selected} onSwitch={(index) => this.switchTab(index as number)} style={{ '--nutui-tabbar-active-color': '#FF9A3C' } as React.CSSProperties}>
          <TabbarItem title="首页" icon={<Home />} />
          <TabbarItem title="食谱" icon={<Search />} />
          <TabbarItem title="宝宝" icon={<My />} />
          <TabbarItem title="我的" icon={<User />} />
        </Tabbar>
      </View>
    )
  }
}
```

- [ ] **Step 3: Build and verify**

```bash
npm run dev:weapp
```

Expected: `dist/custom-tab-bar/` directory is created. No build errors.

- [ ] **Step 4: Commit**

```bash
git add src/app.tsx src/custom-tab-bar/
git commit -m "feat: add app entry with auth init and custom tab bar"
```

---

## Task 9: Auth Pages (Login + Register)

**Files:**
- Modify: `src/pages/auth/login/index.tsx`
- Modify: `src/pages/auth/register/index.tsx`

Each page needs: auth guard redirect, form UI, store action call, error display.

- [ ] **Step 1: Implement src/pages/auth/login/index.tsx**

```tsx
import { useState, useEffect } from 'react'
import Taro from '@tarojs/taro'
import { View, Text } from '@tarojs/components'
import { Button, Input, Toast } from '@nutui/nutui-react-taro'
import { useAuthStore } from '../../../store/authStore'
import { colors } from '../../../constants/theme'
import './index.scss'

export default function Login() {
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const { login, isLoading, isLoggedIn } = useAuthStore()

  useEffect(() => {
    if (isLoggedIn) {
      Taro.switchTab({ url: '/pages/home/index' })
    }
  }, [isLoggedIn])

  const handleLogin = async () => {
    if (!phone || !password) {
      Toast.show({ content: '请填写手机号和密码', icon: 'fail' })
      return
    }
    try {
      await login(phone, password)
      // redirect handled by useEffect above
    } catch (err) {
      Toast.show({ content: (err as Error).message, icon: 'fail' })
    }
  }

  return (
    <View className="login-page">
      <View className="login-header">
        <Text className="login-title">宝宝辅食</Text>
        <Text className="login-subtitle">智能辅食管理助手</Text>
      </View>

      <View className="login-form card">
        <Input
          name="phone"
          placeholder="手机号"
          type="number"
          maxLength={11}
          value={phone}
          onChange={(val) => setPhone(val)}
        />
        <View style={{ height: 12 }} />
        <Input
          name="password"
          placeholder="密码"
          type="password"
          value={password}
          onChange={(val) => setPassword(val)}
        />
        <View style={{ height: 20 }} />
        <Button
          type="primary"
          block
          loading={isLoading}
          onClick={handleLogin}
          style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
        >
          登录
        </Button>
        <View style={{ height: 12 }} />
        <Button
          type="default"
          block
          onClick={() => Taro.navigateTo({ url: '/pages/auth/register/index' })}
        >
          注册新账号
        </Button>
      </View>
    </View>
  )
}
```

Create `src/pages/auth/login/index.scss`:

```scss
.login-page {
  min-height: 100vh;
  background-color: #FFF9F0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 24px 24px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #FF9A3C;
  margin-bottom: 8px;
}

.login-subtitle {
  display: block;
  font-size: 14px;
  color: #7A6055;
}

.login-form {
  width: 100%;
}
```

- [ ] **Step 2: Implement src/pages/auth/register/index.tsx**

```tsx
import { useState } from 'react'
import Taro from '@tarojs/taro'
import { View } from '@tarojs/components'
import { Button, Input, Toast, NavBar } from '@nutui/nutui-react-taro'
import { useAuthStore } from '../../../store/authStore'
import { colors } from '../../../constants/theme'

export default function Register() {
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')
  const { register, isLoading } = useAuthStore()

  const handleRegister = async () => {
    if (!phone || !password || !nickname) {
      Toast.show({ content: '请填写所有字段', icon: 'fail' })
      return
    }
    if (phone.length !== 11) {
      Toast.show({ content: '请输入11位手机号', icon: 'fail' })
      return
    }
    if (password.length < 6) {
      Toast.show({ content: '密码至少6位', icon: 'fail' })
      return
    }
    try {
      await register(phone, password, nickname)
      Taro.switchTab({ url: '/pages/home/index' })
    } catch (err) {
      Toast.show({ content: (err as Error).message, icon: 'fail' })
    }
  }

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0' }}>
      <NavBar
        title="注册账号"
        left={<View onClick={() => Taro.navigateBack()}>返回</View>}
      />
      <View style={{ padding: '24px' }}>
        <View className="card">
          <Input name="nickname" placeholder="昵称" value={nickname} onChange={setNickname} />
          <View style={{ height: 12 }} />
          <Input name="phone" placeholder="手机号" type="number" maxLength={11} value={phone} onChange={setPhone} />
          <View style={{ height: 12 }} />
          <Input name="password" placeholder="密码（至少6位）" type="password" value={password} onChange={setPassword} />
          <View style={{ height: 20 }} />
          <Button
            type="primary"
            block
            loading={isLoading}
            onClick={handleRegister}
            style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
          >
            注册
          </Button>
        </View>
      </View>
    </View>
  )
}
```

- [ ] **Step 3: Add auth guard to TabBar pages**

In each TabBar page stub, add auth guard. Add this hook to each page. Example for `src/pages/home/index.tsx` (update the stub):

```tsx
import { useDidShow } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { View, Text } from '@tarojs/components'
import { useAuthStore } from '../../store/authStore'

export default function Home() {
  const { isLoggedIn } = useAuthStore()

  useDidShow(() => {
    if (!isLoggedIn) {
      Taro.reLaunch({ url: '/pages/auth/login/index' })
    }
    // Update custom tab bar selection
    const tabBar = Taro.getTabBar(Taro.getCurrentInstance().page)
    if (tabBar) (tabBar as { setState: Function }).setState({ selected: 0 })
  })

  return <View><Text>Home (placeholder)</Text></View>
}
```

Apply the same `useDidShow` auth guard and `getTabBar` selection update to the other 3 TabBar page stubs with `selected: 1, 2, 3` respectively.

- [ ] **Step 4: Build and verify**

```bash
npm run dev:weapp
```

Expected: Build succeeds. Open in WeChat DevTools: login page shows, form inputs work.

- [ ] **Step 5: Commit**

```bash
git add src/pages/auth/
git commit -m "feat: add login and register pages with form validation"
```

---

## Task 10: Shared Components

**Files:**
- Create: `src/components/RecipeCard/index.tsx` + `index.scss`
- Create: `src/components/BabyCard/index.tsx` + `index.scss`
- Create: `src/components/MealPeriodTag/index.tsx`
- Create: `src/components/NutritionBar/index.tsx` + `index.scss`

- [ ] **Step 1: Create src/components/RecipeCard/index.tsx**

```tsx
import { View, Text, Image } from '@tarojs/components'
import { Tag } from '@nutui/nutui-react-taro'
import { CandidateRecipeItem, TextureTypeLabel } from '../../models/recipe'
import { colors } from '../../constants/theme'
import './index.scss'

interface Props {
  recipe: CandidateRecipeItem
  onPress?: () => void
}

export default function RecipeCard({ recipe, onPress }: Props) {
  return (
    <View className="recipe-card card" onClick={onPress}>
      <View className="recipe-card__header">
        <Text className="recipe-card__name">{recipe.name}</Text>
        <Tag type="primary" style={{ backgroundColor: colors.primaryLight }}>{TextureTypeLabel[recipe.textureType]}</Tag>
      </View>
      <Text className="recipe-card__age">{recipe.minAgeMonths}个月以上</Text>
      <Text className="recipe-card__ingredients">
        主要食材：{recipe.mainIngredients.slice(0, 3).join('、')}
      </Text>
      <View className="recipe-card__nutrition">
        <Text className="nutrition-chip">{recipe.nutrition.calories} kcal</Text>
        <Text className="nutrition-chip">蛋白质 {recipe.nutrition.protein}g</Text>
        <Text className="nutrition-chip">钙 {recipe.nutrition.calcium}mg</Text>
      </View>
    </View>
  )
}
```

Create `src/components/RecipeCard/index.scss`:

```scss
.recipe-card {
  cursor: pointer;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6px;
  }

  &__name {
    font-size: 15px;
    font-weight: 600;
    color: #3D2B1F;
  }

  &__age {
    display: block;
    font-size: 12px;
    color: #7A6055;
    margin-bottom: 4px;
  }

  &__ingredients {
    display: block;
    font-size: 12px;
    color: #B0A098;
    margin-bottom: 8px;
  }

  &__nutrition {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
  }
}

.nutrition-chip {
  background: #FFF3E8;
  color: #FF9A3C;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}
```

- [ ] **Step 2: Create src/components/BabyCard/index.tsx**

```tsx
import { View, Text } from '@tarojs/components'
import { Baby } from '../../models/baby'
import { ageLabel } from '../../utils/date'
import './index.scss'

interface Props {
  baby: Baby
  selected?: boolean
  onPress?: () => void
}

export default function BabyCard({ baby, selected, onPress }: Props) {
  return (
    <View className={`baby-card card ${selected ? 'baby-card--selected' : ''}`} onClick={onPress}>
      <View className="baby-card__avatar">
        <Text className="baby-card__avatar-text">{baby.name[0]}</Text>
      </View>
      <View className="baby-card__info">
        <Text className="baby-card__name">{baby.name}</Text>
        <Text className="baby-card__age">{ageLabel(baby.birthDate)}</Text>
        {baby.allergies.length > 0 && (
          <Text className="baby-card__allergies">过敏：{baby.allergies.join('、')}</Text>
        )}
      </View>
    </View>
  )
}
```

Create `src/components/BabyCard/index.scss`:

```scss
.baby-card {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;

  &--selected {
    border: 2px solid #FF9A3C;
  }

  &__avatar {
    width: 48px;
    height: 48px;
    border-radius: 24px;
    background: #FFB067;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__avatar-text {
    font-size: 20px;
    color: white;
    font-weight: 700;
  }

  &__info {
    flex: 1;
  }

  &__name {
    display: block;
    font-size: 15px;
    font-weight: 600;
    color: #3D2B1F;
  }

  &__age {
    display: block;
    font-size: 13px;
    color: #7A6055;
  }

  &__allergies {
    display: block;
    font-size: 12px;
    color: #FF6B6B;
    margin-top: 2px;
  }
}
```

- [ ] **Step 3: Create src/components/MealPeriodTag/index.tsx**

```tsx
import { View, Text } from '@tarojs/components'
import { MealPeriod } from '../../models/plan'
import { MealPeriodColor, MealPeriodLabel } from '../../constants/theme'

interface Props {
  period: MealPeriod
}

export default function MealPeriodTag({ period }: Props) {
  return (
    <View style={{
      backgroundColor: MealPeriodColor[period],
      borderRadius: 12,
      padding: '2px 10px',
      display: 'inline-flex',
    }}>
      <Text style={{ fontSize: 12, color: 'white', fontWeight: 600 }}>
        {MealPeriodLabel[period]}
      </Text>
    </View>
  )
}
```

- [ ] **Step 4: Create src/components/NutritionBar/index.tsx**

```tsx
import { View, Text } from '@tarojs/components'
import { NutritionIntake } from '../../models/plan'
import { colors } from '../../constants/theme'
import './index.scss'

interface Props {
  intake: NutritionIntake
  // Daily goals for a 6-12 month baby (approximate)
  goals?: NutritionIntake
}

const DEFAULT_GOALS: NutritionIntake = {
  calories: 700, protein: 20, fat: 25, carbohydrates: 90, calcium: 400, iron: 10,
}

function ProgressBar({ value, max, color }: { value: number; max: number; color: string }) {
  const pct = Math.min((value / max) * 100, 100)
  return (
    <View className="progress-track">
      <View className="progress-fill" style={{ width: `${pct}%`, backgroundColor: color }} />
    </View>
  )
}

export default function NutritionBar({ intake, goals = DEFAULT_GOALS }: Props) {
  const items = [
    { label: '热量', value: intake.calories, max: goals.calories, unit: 'kcal', color: colors.calories },
    { label: '蛋白质', value: intake.protein, max: goals.protein, unit: 'g', color: colors.protein },
    { label: '钙', value: intake.calcium, max: goals.calcium, unit: 'mg', color: colors.calcium },
    { label: '铁', value: intake.iron, max: goals.iron, unit: 'mg', color: colors.iron },
  ]

  return (
    <View className="nutrition-bar card">
      <Text className="section-title">今日营养</Text>
      {items.map((item) => (
        <View key={item.label} className="nutrition-row">
          <View className="nutrition-label-row">
            <Text className="nutrition-label">{item.label}</Text>
            <Text className="nutrition-value">{item.value.toFixed(0)} / {item.max} {item.unit}</Text>
          </View>
          <ProgressBar value={item.value} max={item.max} color={item.color} />
        </View>
      ))}
    </View>
  )
}
```

Create `src/components/NutritionBar/index.scss`:

```scss
.nutrition-bar {
  .nutrition-row {
    margin-bottom: 10px;
  }

  .nutrition-label-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 4px;
  }

  .nutrition-label {
    font-size: 13px;
    color: #3D2B1F;
  }

  .nutrition-value {
    font-size: 12px;
    color: #7A6055;
  }
}

.progress-track {
  height: 6px;
  background: #F0E8E0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}
```

- [ ] **Step 5: Build and verify no import errors**

```bash
npm run dev:weapp
```

Expected: Successful build.

- [ ] **Step 6: Commit**

```bash
git add src/components/
git commit -m "feat: add shared components (RecipeCard, BabyCard, MealPeriodTag, NutritionBar)"
```

---

## Task 11: Baby Pages (List + Form)

**Files:**
- Modify: `src/pages/baby/index/index.tsx` + `index.scss`
- Modify: `src/pages/baby/form/index.tsx`

- [ ] **Step 1: Implement src/pages/baby/index/index.tsx**

```tsx
import { useEffect } from 'react'
import Taro, { useDidShow } from '@tarojs/taro'
import { View, Text } from '@tarojs/components'
import { Button, Empty } from '@nutui/nutui-react-taro'
import { useBabyStore } from '../../../store/babyStore'
import { useAuthStore } from '../../../store/authStore'
import BabyCard from '../../../components/BabyCard'
import { colors } from '../../../constants/theme'

export default function BabyList() {
  const { babies, selectedBaby, fetchBabies, selectBaby, isLoading } = useBabyStore()
  const { isLoggedIn } = useAuthStore()

  useDidShow(() => {
    if (!isLoggedIn) {
      Taro.reLaunch({ url: '/pages/auth/login/index' })
      return
    }
    const tabBar = Taro.getTabBar(Taro.getCurrentInstance().page)
    if (tabBar) (tabBar as { setState: Function }).setState({ selected: 2 })
    fetchBabies()
  })

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0', padding: '16px 16px 80px' }}>
      <View style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Text className="section-title" style={{ marginBottom: 0 }}>宝宝档案</Text>
        <Button
          size="small"
          type="primary"
          style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
          onClick={() => Taro.navigateTo({ url: '/pages/baby/form/index' })}
        >
          + 添加宝宝
        </Button>
      </View>

      {babies.length === 0 && !isLoading ? (
        <Empty description="还没有宝宝档案，点击右上角添加" />
      ) : (
        babies.map((baby) => (
          <BabyCard
            key={baby.id}
            baby={baby}
            selected={selectedBaby?.id === baby.id}
            onPress={() => selectBaby(baby)}
          />
        ))
      )}

      {babies.map((baby) => (
        <View key={`actions-${baby.id}`} style={{ display: 'flex', gap: 8, marginTop: -8, marginBottom: 12 }}>
          <Button
            size="mini"
            onClick={() => Taro.navigateTo({ url: `/pages/baby/form/index?id=${baby.id}` })}
          >
            编辑
          </Button>
        </View>
      ))}
    </View>
  )
}
```

- [ ] **Step 2: Implement src/pages/baby/form/index.tsx**

```tsx
import { useState, useEffect } from 'react'
import Taro from '@tarojs/taro'
import { View } from '@tarojs/components'
import { Button, Input, NavBar, Checkbox, DatePicker, Toast } from '@nutui/nutui-react-taro'
import { useBabyStore } from '../../../store/babyStore'
import { ALLERGY_OPTIONS, PREFERENCE_OPTIONS } from '../../../models/baby'
import { colors } from '../../../constants/theme'
import { formatDate } from '../../../utils/date'

export default function BabyForm() {
  const router = Taro.getCurrentInstance().router
  const editId = router?.params?.id ? Number(router.params.id) : null

  const { babies, createBaby, updateBaby } = useBabyStore()
  const existing = editId ? babies.find((b) => b.id === editId) : null

  const [name, setName] = useState(existing?.name ?? '')
  const [birthDate, setBirthDate] = useState(existing?.birthDate ?? '')
  const [allergies, setAllergies] = useState<string[]>(existing?.allergies ?? [])
  const [preferences, setPreferences] = useState<string[]>(existing?.preferences ?? [])
  const [showDatePicker, setShowDatePicker] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    if (!name.trim()) {
      Toast.show({ content: '请输入宝宝姓名', icon: 'fail' })
      return
    }
    if (!birthDate) {
      Toast.show({ content: '请选择出生日期', icon: 'fail' })
      return
    }
    setIsSubmitting(true)
    try {
      const payload = { name: name.trim(), birthDate, allergies, preferences }
      if (editId) {
        await updateBaby(editId, payload)
      } else {
        await createBaby(payload)
      }
      Toast.show({ content: editId ? '已更新' : '宝宝已添加', icon: 'success' })
      Taro.navigateBack()
    } catch (err) {
      Toast.show({ content: (err as Error).message, icon: 'fail' })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0' }}>
      <NavBar title={editId ? '编辑宝宝' : '添加宝宝'} left={<View onClick={() => Taro.navigateBack()}>返回</View>} />

      <View style={{ padding: '16px' }}>
        <View className="card" style={{ marginBottom: 12 }}>
          <Input name="name" placeholder="宝宝姓名" value={name} onChange={setName} />
        </View>

        <View className="card" style={{ marginBottom: 12 }} onClick={() => setShowDatePicker(true)}>
          <Input
            name="birthDate"
            placeholder="出生日期（点击选择）"
            value={birthDate}
            readOnly
          />
        </View>

        <DatePicker
          title="选择出生日期"
          type="date"
          visible={showDatePicker}
          onClose={() => setShowDatePicker(false)}
          onConfirm={(_options, values) => {
            const [y, m, d] = values as string[]
            setBirthDate(`${y}-${m.padStart(2, '0')}-${d.padStart(2, '0')}`)
            setShowDatePicker(false)
          }}
        />

        <View className="card" style={{ marginBottom: 12 }}>
          <View className="section-title">过敏食材（可多选）</View>
          <Checkbox.Group value={allergies} onChange={(val) => setAllergies(val as string[])}>
            <View style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {ALLERGY_OPTIONS.map((opt) => (
                <Checkbox key={opt} value={opt} label={opt} />
              ))}
            </View>
          </Checkbox.Group>
        </View>

        <View className="card" style={{ marginBottom: 20 }}>
          <View className="section-title">饮食偏好（可多选）</View>
          <Checkbox.Group value={preferences} onChange={(val) => setPreferences(val as string[])}>
            <View style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {PREFERENCE_OPTIONS.map((opt) => (
                <Checkbox key={opt} value={opt} label={opt} />
              ))}
            </View>
          </Checkbox.Group>
        </View>

        <Button
          type="primary"
          block
          loading={isSubmitting}
          onClick={handleSubmit}
          style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
        >
          {editId ? '保存修改' : '添加宝宝'}
        </Button>
      </View>
    </View>
  )
}
```

- [ ] **Step 3: Build and verify**

```bash
npm run dev:weapp
```

Expected: Baby list and form pages compile. No type errors.

- [ ] **Step 4: Commit**

```bash
git add src/pages/baby/
git commit -m "feat: add baby list and form pages"
```

---

## Task 12: Recipe Pages (List + Detail)

**Files:**
- Modify: `src/pages/recipes/index/index.tsx` + `index.scss`
- Modify: `src/pages/recipes/detail/index.tsx`

- [ ] **Step 1: Implement src/pages/recipes/index/index.tsx**

```tsx
import { useState, useEffect } from 'react'
import Taro, { useDidShow } from '@tarojs/taro'
import { View, Text, ScrollView } from '@tarojs/components'
import { Button, Tabs, Empty, Loading, Toast } from '@nutui/nutui-react-taro'
import { recipeService } from '../../../services/recipeService'
import { useAuthStore } from '../../../store/authStore'
import { useBabyStore } from '../../../store/babyStore'
import { useHomeStore } from '../../../store/homeStore'
import RecipeCard from '../../../components/RecipeCard'
import { CandidateRecipeItem, CandidateRecipesResponse } from '../../../models/recipe'
import { MealPeriod } from '../../../models/plan'
import { colors, MealPeriodLabel } from '../../../constants/theme'

const MEAL_PERIODS: MealPeriod[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK']

export default function RecipesIndex() {
  const { isLoggedIn } = useAuthStore()
  const { selectedBaby } = useBabyStore()
  const { candidates, fetchCandidates, isLoadingCandidates } = useHomeStore()
  const [activeTab, setActiveTab] = useState('BREAKFAST')
  const [mode, setMode] = useState<'browse' | 'recommend'>('recommend')

  useDidShow(() => {
    if (!isLoggedIn) {
      Taro.reLaunch({ url: '/pages/auth/login/index' })
      return
    }
    const tabBar = Taro.getTabBar(Taro.getCurrentInstance().page)
    if (tabBar) (tabBar as { setState: Function }).setState({ selected: 1 })

    if (selectedBaby && !candidates) {
      fetchCandidates(selectedBaby)
    }
  })

  const handleRefreshCandidates = async () => {
    if (!selectedBaby) {
      Toast.show({ content: '请先添加宝宝档案', icon: 'fail' })
      return
    }
    await fetchCandidates(selectedBaby)
  }

  const currentList: CandidateRecipeItem[] = candidates
    ? candidates[activeTab.toLowerCase() as keyof CandidateRecipesResponse] as CandidateRecipeItem[]
    : []

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0', paddingBottom: 80 }}>
      <View style={{ padding: '16px 16px 0' }}>
        {selectedBaby && (
          <Text style={{ fontSize: 13, color: '#7A6055' }}>
            为 {selectedBaby.name}（{selectedBaby.ageInMonths}个月）推荐
          </Text>
        )}
        <View style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 8 }}>
          <Button
            size="small"
            type="primary"
            loading={isLoadingCandidates}
            style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
            onClick={handleRefreshCandidates}
          >
            刷新推荐
          </Button>
        </View>
      </View>

      <Tabs
        value={activeTab}
        onChange={(val) => setActiveTab(val as string)}
        activeColor={colors.primary}
      >
        {MEAL_PERIODS.map((period) => (
          <Tabs.TabPane key={period} title={MealPeriodLabel[period]} value={period}>
            <View style={{ padding: '12px 16px' }}>
              {isLoadingCandidates ? (
                <Loading type="spinner">加载中...</Loading>
              ) : currentList.length === 0 ? (
                <Empty description="暂无推荐，点击刷新推荐" />
              ) : (
                currentList.map((recipe) => (
                  <RecipeCard
                    key={recipe.id}
                    recipe={recipe}
                    onPress={() => Taro.navigateTo({ url: `/pages/recipes/detail/index?id=${recipe.id}` })}
                  />
                ))
              )}
            </View>
          </Tabs.TabPane>
        ))}
      </Tabs>
    </View>
  )
}
```

- [ ] **Step 2: Implement src/pages/recipes/detail/index.tsx**

```tsx
import { useState, useEffect } from 'react'
import Taro from '@tarojs/taro'
import { View, Text, ScrollView } from '@tarojs/components'
import { NavBar, Loading, Tag } from '@nutui/nutui-react-taro'
import { recipeService } from '../../../services/recipeService'
import { Recipe, TextureTypeLabel } from '../../../models/recipe'
import { colors } from '../../../constants/theme'

export default function RecipeDetail() {
  const router = Taro.getCurrentInstance().router
  const id = router?.params?.id ? Number(router.params.id) : null
  const [recipe, setRecipe] = useState<Recipe | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (!id) return
    recipeService.get(id)
      .then(setRecipe)
      .catch(() => Taro.showToast({ title: '加载失败', icon: 'error' }))
      .finally(() => setIsLoading(false))
  }, [id])

  if (isLoading) return <Loading type="spinner" style={{ marginTop: 100, justifyContent: 'center' }}>加载中...</Loading>
  if (!recipe) return <View style={{ padding: 16 }}><Text>食谱不存在</Text></View>

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0' }}>
      <NavBar title={recipe.name} left={<View onClick={() => Taro.navigateBack()}>返回</View>} />

      <ScrollView scrollY style={{ height: '100vh', paddingTop: 46 }}>
        <View style={{ padding: 16 }}>
          {/* Header */}
          <View className="card" style={{ marginBottom: 12 }}>
            <Text style={{ fontSize: 18, fontWeight: 700, color: '#3D2B1F', display: 'block', marginBottom: 8 }}>{recipe.name}</Text>
            <View style={{ display: 'flex', gap: 8, marginBottom: 8 }}>
              <Tag type="primary" style={{ backgroundColor: colors.primaryLight }}>{TextureTypeLabel[recipe.textureType]}</Tag>
              <Tag>{recipe.minAgeMonths}个月以上</Tag>
              {recipe.isIronRich && <Tag style={{ backgroundColor: colors.iron, color: 'white' }}>高铁</Tag>}
            </View>
            {recipe.description && <Text style={{ fontSize: 13, color: '#7A6055' }}>{recipe.description}</Text>}
          </View>

          {/* Nutrition */}
          <View className="card" style={{ marginBottom: 12 }}>
            <Text className="section-title">营养成分（每份）</Text>
            <View style={{ display: 'flex', flexWrap: 'wrap', gap: 12 }}>
              {[
                { label: '热量', value: `${recipe.nutrition.calories} kcal`, color: colors.calories },
                { label: '蛋白质', value: `${recipe.nutrition.protein}g`, color: colors.protein },
                { label: '脂肪', value: `${recipe.nutrition.fat}g`, color: '#DDA0DD' },
                { label: '碳水', value: `${recipe.nutrition.carbohydrates}g`, color: '#FFD700' },
                { label: '钙', value: `${recipe.nutrition.calcium}mg`, color: colors.calcium },
                { label: '铁', value: `${recipe.nutrition.iron}mg`, color: colors.iron },
              ].map((item) => (
                <View key={item.label} style={{ textAlign: 'center', minWidth: '25%' }}>
                  <Text style={{ display: 'block', fontSize: 15, fontWeight: 600, color: item.color }}>{item.value}</Text>
                  <Text style={{ display: 'block', fontSize: 12, color: '#7A6055' }}>{item.label}</Text>
                </View>
              ))}
            </View>
          </View>

          {/* Ingredients */}
          <View className="card" style={{ marginBottom: 12 }}>
            <Text className="section-title">食材清单</Text>
            {recipe.ingredients.map((ing, i) => (
              <View key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: i < recipe.ingredients.length - 1 ? '1px solid #F0E8E0' : 'none' }}>
                <Text style={{ color: '#3D2B1F' }}>{ing.name}</Text>
                <Text style={{ color: '#7A6055' }}>{ing.amount}{ing.unit}</Text>
              </View>
            ))}
          </View>

          {/* Steps */}
          <View className="card">
            <Text className="section-title">制作步骤</Text>
            {recipe.steps.map((step, i) => (
              <View key={i} style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
                <View style={{ width: 24, height: 24, borderRadius: 12, backgroundColor: colors.primary, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <Text style={{ color: 'white', fontSize: 13, fontWeight: 700 }}>{i + 1}</Text>
                </View>
                <Text style={{ flex: 1, fontSize: 14, color: '#3D2B1F', lineHeight: '22px' }}>{step}</Text>
              </View>
            ))}
          </View>

          {recipe.safetyNotes && (
            <View style={{ backgroundColor: '#FFF3CD', borderRadius: 8, padding: 12, marginTop: 12 }}>
              <Text style={{ fontSize: 13, color: '#856404' }}>⚠️ {recipe.safetyNotes}</Text>
            </View>
          )}
        </View>
      </ScrollView>
    </View>
  )
}
```

- [ ] **Step 3: Build and verify**

```bash
npm run dev:weapp
```

Expected: Recipe pages compile. Tabs for meal periods render correctly.

- [ ] **Step 4: Commit**

```bash
git add src/pages/recipes/
git commit -m "feat: add recipe list with AI recommendations and detail page"
```

---

## Task 13: Home Page (Today's Menu)

**Files:**
- Modify: `src/pages/home/index.tsx` + `index.scss`

- [ ] **Step 1: Implement src/pages/home/index.tsx**

```tsx
import { useState } from 'react'
import Taro, { useDidShow } from '@tarojs/taro'
import { View, Text, ScrollView } from '@tarojs/components'
import { Button, Dialog, Loading, Empty, Toast } from '@nutui/nutui-react-taro'
import { useAuthStore } from '../../store/authStore'
import { useBabyStore } from '../../store/babyStore'
import { useHomeStore } from '../../store/homeStore'
import NutritionBar from '../../components/NutritionBar'
import MealPeriodTag from '../../components/MealPeriodTag'
import RecipeCard from '../../components/RecipeCard'
import { MealPeriod, PlanWithRecipe } from '../../models/plan'
import { CandidateRecipeItem } from '../../models/recipe'
import { colors, MealPeriodLabel } from '../../constants/theme'
import { today, formatDateCN } from '../../utils/date'
import './index.scss'

const MEAL_PERIODS: MealPeriod[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK']

export default function Home() {
  const { isLoggedIn } = useAuthStore()
  const { babies, selectedBaby, fetchBabies, selectBaby } = useBabyStore()
  const {
    todayPlans, candidates, nutritionIntake,
    isLoadingPlans, fetchTodayPlans, fetchCandidates, addPlan, removePlan,
  } = useHomeStore()

  const [addingPeriod, setAddingPeriod] = useState<MealPeriod | null>(null)
  const [showCandidates, setShowCandidates] = useState(false)

  useDidShow(async () => {
    if (!isLoggedIn) {
      Taro.reLaunch({ url: '/pages/auth/login/index' })
      return
    }
    const tabBar = Taro.getTabBar(Taro.getCurrentInstance().page)
    if (tabBar) (tabBar as { setState: Function }).setState({ selected: 0 })

    await fetchBabies()
    if (selectedBaby) {
      fetchTodayPlans(selectedBaby)
      if (!candidates) fetchCandidates(selectedBaby)
    }
  })

  const handleAddMeal = (period: MealPeriod) => {
    if (!candidates) {
      Toast.show({ content: '正在加载推荐，请稍候', icon: 'loading' })
      return
    }
    setAddingPeriod(period)
    setShowCandidates(true)
  }

  const handleSelectRecipe = async (recipe: CandidateRecipeItem) => {
    if (!selectedBaby || !addingPeriod) return
    setShowCandidates(false)
    try {
      await addPlan({ babyId: selectedBaby.id, recipeId: recipe.id, date: today(), mealPeriod: addingPeriod })
      Toast.show({ content: '已添加到今日菜单', icon: 'success' })
    } catch (err) {
      Toast.show({ content: (err as Error).message, icon: 'fail' })
    }
  }

  const handleRemovePlan = (plan: PlanWithRecipe) => {
    Dialog.confirm({
      title: '删除餐次',
      content: `确认从今日菜单删除「${plan.recipe.name}」？`,
      onConfirm: async () => {
        await removePlan(plan.id)
        Toast.show({ content: '已删除', icon: 'success' })
      },
    })
  }

  const getPlansByPeriod = (period: MealPeriod) =>
    todayPlans.filter((p) => p.mealPeriod === period)

  const getCandidatesByPeriod = (period: MealPeriod): CandidateRecipeItem[] => {
    if (!candidates) return []
    return candidates[period.toLowerCase() as keyof typeof candidates] as CandidateRecipeItem[]
  }

  if (!selectedBaby && babies.length === 0) {
    return (
      <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 32 }}>
        <Empty description="请先添加宝宝档案" />
        <Button
          type="primary"
          style={{ marginTop: 16, backgroundColor: colors.primary, borderColor: colors.primary }}
          onClick={() => Taro.navigateTo({ url: '/pages/baby/form/index' })}
        >
          添加宝宝
        </Button>
      </View>
    )
  }

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0' }}>
      {/* Header */}
      <View style={{ backgroundColor: colors.primary, padding: '16px 16px 24px' }}>
        <Text style={{ color: 'white', fontSize: 13, display: 'block', marginBottom: 4 }}>
          {formatDateCN(today())}
        </Text>
        {selectedBaby && (
          <Text style={{ color: 'white', fontSize: 18, fontWeight: 700 }}>
            {selectedBaby.name} 的今日菜单
          </Text>
        )}
        {babies.length > 1 && (
          <ScrollView scrollX style={{ marginTop: 8 }}>
            <View style={{ display: 'flex', gap: 8 }}>
              {babies.map((baby) => (
                <View
                  key={baby.id}
                  onClick={() => {
                    selectBaby(baby)
                    fetchTodayPlans(baby)
                    fetchCandidates(baby)
                  }}
                  style={{
                    backgroundColor: selectedBaby?.id === baby.id ? 'white' : 'rgba(255,255,255,0.3)',
                    color: selectedBaby?.id === baby.id ? colors.primary : 'white',
                    borderRadius: 16, padding: '4px 12px', fontSize: 13, fontWeight: 600,
                  }}
                >
                  <Text>{baby.name}</Text>
                </View>
              ))}
            </View>
          </ScrollView>
        )}
      </View>

      <ScrollView scrollY style={{ height: '100vh', marginTop: -12 }}>
        <View style={{ borderRadius: '12px 12px 0 0', backgroundColor: '#FFF9F0', padding: 16, paddingBottom: 80 }}>
          {/* Nutrition Summary */}
          <NutritionBar intake={nutritionIntake} />

          {/* Meal Periods */}
          {isLoadingPlans ? (
            <Loading type="spinner" style={{ justifyContent: 'center', marginTop: 20 }}>加载中...</Loading>
          ) : (
            MEAL_PERIODS.map((period) => {
              const plans = getPlansByPeriod(period)
              return (
                <View key={period} className="card" style={{ marginBottom: 12 }}>
                  <View style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
                    <MealPeriodTag period={period} />
                    <Button
                      size="mini"
                      type="primary"
                      style={{ backgroundColor: colors.primary, borderColor: colors.primary }}
                      onClick={() => handleAddMeal(period)}
                    >
                      + 添加
                    </Button>
                  </View>

                  {plans.length === 0 ? (
                    <Text style={{ fontSize: 13, color: '#B0A098' }}>点击添加{MealPeriodLabel[period]}食谱</Text>
                  ) : (
                    plans.map((plan) => (
                      <View key={plan.id} style={{ marginBottom: 8 }}>
                        <View style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid #F0E8E0' }}>
                          <View>
                            <Text style={{ fontSize: 14, fontWeight: 600, color: '#3D2B1F' }}>{plan.recipe.name}</Text>
                            <Text style={{ display: 'block', fontSize: 12, color: '#7A6055' }}>{plan.recipe.nutrition.calories} kcal</Text>
                          </View>
                          <Button size="mini" type="danger" onClick={() => handleRemovePlan(plan)}>删除</Button>
                        </View>
                      </View>
                    ))
                  )}
                </View>
              )
            })
          )}
        </View>
      </ScrollView>

      {/* Recipe Picker Dialog */}
      <Dialog
        title={`添加${addingPeriod ? MealPeriodLabel[addingPeriod] : ''}食谱`}
        visible={showCandidates}
        onClose={() => setShowCandidates(false)}
        footer={null}
        style={{ '--nutui-dialog-width': '90%' } as React.CSSProperties}
      >
        <ScrollView scrollY style={{ maxHeight: '60vh' }}>
          {addingPeriod && getCandidatesByPeriod(addingPeriod).map((recipe) => (
            <RecipeCard
              key={recipe.id}
              recipe={recipe}
              onPress={() => handleSelectRecipe(recipe)}
            />
          ))}
          {addingPeriod && getCandidatesByPeriod(addingPeriod).length === 0 && (
            <Empty description="暂无推荐食谱" />
          )}
        </ScrollView>
      </Dialog>
    </View>
  )
}
```

Create `src/pages/home/index.scss`:

```scss
// Page-specific styles for home; global styles live in app.scss
```

- [ ] **Step 2: Build and verify**

```bash
npm run dev:weapp
```

Expected: Home page compiles. No type errors.

- [ ] **Step 3: Commit**

```bash
git add src/pages/home/
git commit -m "feat: add home page with today's menu and nutrition summary"
```

---

## Task 14: User Page + Final Verification

**Files:**
- Modify: `src/pages/user/index.tsx`

- [ ] **Step 1: Implement src/pages/user/index.tsx**

```tsx
import Taro, { useDidShow } from '@tarojs/taro'
import { View, Text } from '@tarojs/components'
import { Button, Cell, CellGroup, Avatar, Dialog } from '@nutui/nutui-react-taro'
import { useAuthStore } from '../../store/authStore'
import { colors } from '../../constants/theme'

export default function UserPage() {
  const { user, isLoggedIn, logout } = useAuthStore()

  useDidShow(() => {
    if (!isLoggedIn) {
      Taro.reLaunch({ url: '/pages/auth/login/index' })
      return
    }
    const tabBar = Taro.getTabBar(Taro.getCurrentInstance().page)
    if (tabBar) (tabBar as { setState: Function }).setState({ selected: 3 })
  })

  const handleLogout = () => {
    Dialog.confirm({
      title: '退出登录',
      content: '确定要退出登录吗？',
      onConfirm: () => logout(),
    })
  }

  if (!user) return null

  return (
    <View style={{ minHeight: '100vh', backgroundColor: '#FFF9F0', paddingBottom: 80 }}>
      {/* Profile Header */}
      <View style={{ backgroundColor: colors.primary, padding: '40px 24px 32px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Avatar size="large" style={{ backgroundColor: 'white', color: colors.primary, fontSize: 24, fontWeight: 700 }}>
          {user.nickname[0]}
        </Avatar>
        <Text style={{ color: 'white', fontSize: 18, fontWeight: 700, marginTop: 12 }}>{user.nickname}</Text>
        <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 }}>{user.phone}</Text>
      </View>

      <View style={{ padding: 16, marginTop: 16 }}>
        <CellGroup>
          <Cell title="手机号" extra={user.phone} />
          <Cell title="昵称" extra={user.nickname} />
        </CellGroup>
      </View>

      <View style={{ padding: '0 16px' }}>
        <CellGroup>
          <Cell
            title="宝宝档案"
            isLink
            onClick={() => Taro.switchTab({ url: '/pages/baby/index/index' })}
          />
          <Cell
            title="AI 推荐食谱"
            isLink
            onClick={() => Taro.switchTab({ url: '/pages/recipes/index/index' })}
          />
        </CellGroup>
      </View>

      <View style={{ padding: 24 }}>
        <Button
          block
          type="danger"
          onClick={handleLogout}
        >
          退出登录
        </Button>
      </View>
    </View>
  )
}
```

- [ ] **Step 2: Run all tests**

```bash
npx jest
```

Expected: All tests pass (request tests, authService test, authStore test).

- [ ] **Step 3: Full project build**

```bash
npm run build:weapp
```

Expected: Production build completes without errors.

- [ ] **Step 4: Open in WeChat DevTools and verify golden path**

Open WeChat DevTools → Import project from `dist/` folder → Verify:

1. App opens on Login page
2. Register with phone + password + nickname → redirects to Home tab
3. Home shows empty meal periods and zero nutrition
4. Add baby: navigate to Baby tab → "添加宝宝" → fill name + birth date + allergy → save → baby appears in list
5. Go to Home → baby's name appears in header → AI recommendations load (candidates API called)
6. Tap "+ 添加" on BREAKFAST → recipe picker dialog opens → select recipe → recipe appears in meal plan → nutrition updates
7. Recipe tab → AI推荐 tab shows 4 meal period tabs with candidate recipes → tap recipe → detail page shows
8. User tab shows profile, logout works → returns to login

- [ ] **Step 5: Final commit**

```bash
git add .
git commit -m "feat: add user page and complete MVP implementation"
```

- [ ] **Step 6: Create summary tag**

```bash
git tag v0.1.0 -m "BabyFood Mini Program MVP v0.1.0"
```

---

## Verification Checklist

- [ ] `npm run dev:weapp` builds without errors
- [ ] `npx jest` — all tests pass
- [ ] Login / Register flow works end-to-end against backend
- [ ] Baby create, edit visible in list
- [ ] AI candidate recipes load based on selected baby's age + allergies
- [ ] Today's plan: add recipe → nutrition bar updates
- [ ] Custom tab bar highlights correct tab on each page
- [ ] 401 → auto token refresh → seamless retry (or redirect to login if refresh fails)
