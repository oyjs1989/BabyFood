package com.example.babyfood.presentation.theme

import androidx.compose.ui.graphics.Color

// ===== 主色调 - Apple 风格柔和珊瑚色系 =====
// 保留珊瑚色主题，但降低饱和度，符合 Apple 克制的设计原则
val Primary = Color(0xFFFF7A65)              // 柔和珊瑚色（降低饱和度）
val PrimaryDark = Color(0xFFE64A19)         // 深珊瑚色
val PrimaryLight = Color(0xFFFFAB91)        // 浅珊瑚色
val PrimaryContainer = Color(0xFFFFD8C8)    // 主色容器
val OnPrimaryContainer = Color(0xFF5C1D08)  // 主色容器文字

// ===== 次要色调 - iOS 风格绿色系 =====
val Secondary = Color(0xFF34C759)           // iOS 标准绿
val SecondaryDark = Color(0xFF28A745)       // 深绿色
val SecondaryLight = Color(0xFF66BB6A)      // 浅绿色
val SecondaryContainer = Color(0xFFE8F5E9)  // 次要色容器
val OnSecondaryContainer = Color(0xFF1B5E20)// 次要色容器文字

// ===== 第三色调 - iOS 风格橙色系 =====
val Tertiary = Color(0xFFFF9500)            // iOS 标准橙
val TertiaryDark = Color(0xFFF57C00)        // 深橙色
val TertiaryLight = Color(0xFFFFB74D)       // 浅橙色
val TertiaryContainer = Color(0xFFFFE8D6)  // 第三色容器
val OnTertiaryContainer = Color(0xFFE65100) // 第三色容器文字

// ===== 背景色系 - Apple 风格纯净白色 =====
val Background = Color(0xFFFAFAFA)          // 接近纯白的背景（Apple 风格）
val OnBackground = Color(0xFF000000)        // 纯黑文字（提升对比度）
val Surface = Color(0xFFFFFFFF)             // 纯白表面
val SurfaceVariant = Color(0xFFF5F5F7)      // iOS 风格灰白
val OnSurface = Color(0xFF000000)           // 纯黑文字
val OnSurfaceVariant = Color(0xFF8E8E93)    // iOS 次要文字灰

// ===== 文字颜色 =====
val OnPrimary = Color(0xFFFFFFFF)           // 主色文字
val OnSecondary = Color(0xFFFFFFFF)         // 次要色文字
val OnTertiary = Color(0xFFFFFFFF)          // 第三色文字

// ===== 状态颜色 - iOS 标准色系 =====
val Success = Color(0xFF34C759)             // iOS 绿色
val SuccessContainer = Color(0xFFE8F5E9)    // 成功容器
val OnSuccessContainer = Color(0xFF2E7D32)  // 成功容器文字

val Warning = Color(0xFFFF9500)             // iOS 橙色
val WarningContainer = Color(0xFFFFF3E0)   // 警告容器
val OnWarningContainer = Color(0xFFEF6C00)  // 警告容器文字

val Error = Color(0xFFFF3B30)               // iOS 红色
val ErrorContainer = Color(0xFFFFEBEE)     // 错误容器
val OnErrorContainer = Color(0xFFC62828)   // 错误容器文字

val Info = Color(0xFF5AC8FA)                // iOS 蓝色
val InfoContainer = Color(0xFFE3F2FD)      // 信息容器
val OnInfoContainer = Color(0xFF1565C0)    // 信息容器文字

// ===== 柔和渐变色 - Apple 风格 =====
val SoftPink = Color(0xFFFFCCBC)            // 柔和珊瑚粉色
val SoftBlue = Color(0xFFB3E5FC)            // 柔和蓝色
val SoftPurple = Color(0xFFD1C4E9)         // 柔和紫色
val SoftTeal = Color(0xFFC8E6C9)            // 柔和绿色
val SoftAmber = Color(0xFFFFE0B2)           // 柔和琥珀色

// ===== 功能性颜色 - iOS 风格 =====
val Accent = Color(0xFFFF7A65)              // 强调色（与 Primary 一致）
val Outline = Color(0xFFC7C7CC)             // iOS 轮廓灰
val OutlineVariant = Color(0xFFE5E5EA)     // iOS 轮廓线变体
val Divider = Color(0xFFC6C6C8)             // iOS 分割线灰
val Scrim = Color(0x99000000)               // 遮罩（半透明黑）

// ===== 营养元素颜色 - iOS 彩虹色系 =====
val NutritionProtein = Color(0xFF007AFF)    // 蛋白质（iOS 蓝）
val NutritionCalories = Color(0xFFFF9500)  // 热量（iOS 橙）
val NutritionCalcium = Color(0xFF34C759)    // 钙（iOS 绿）
val NutritionIron = Color(0xFFFF3B30)       // 铁（iOS 红）
val NutritionVitaminC = Color(0xFFFFCC00)  // 维生素C（iOS 黄）
val NutritionFiber = Color(0xFFAF52DE)      // 纤维（iOS 紫）

// ===== 暗色主题专用颜色 - Apple 风格 =====
val DarkPrimary = Color(0xFFFF8A65)         // 暗色主题主色
val DarkOnPrimary = Color(0xFF3E2723)       // 暗色主题主色文字
val DarkPrimaryContainer = Color(0xFF5C1D08) // 暗色主题主色容器
val DarkOnPrimaryContainer = Color(0xFFFFD8CC) // 暗色主题主色容器文字

val DarkSecondary = Color(0xFF30D158)       // 暗色主题次要色（iOS 绿）
val DarkOnSecondary = Color(0xFF1B5E20)     // 暗色主题次要色文字
val DarkSecondaryContainer = Color(0xFF2E7D32) // 暗色主题次要色容器
val DarkOnSecondaryContainer = Color(0xFFC8E6C9) // 暗色主题次要色容器文字

val DarkTertiary = Color(0xFFFF9F0A)        // 暗色主题第三色（iOS 橙）
val DarkOnTertiary = Color(0xFFBF360C)      // 暗色主题第三色文字
val DarkTertiaryContainer = Color(0xFFE65100) // 暗色主题第三色容器
val DarkOnTertiaryContainer = Color(0xFFFFE0B2) // 暗色主题第三色容器文字

val DarkBackground = Color(0xFF000000)      // 暗色主题背景（纯黑）
val DarkOnBackground = Color(0xFFFFFFFF)    // 暗色主题背景文字（纯白）
val DarkSurface = Color(0xFF1C1C1E)         // 暗色主题表面（iOS 深灰）
val DarkOnSurface = Color(0xFFFFFFFF)       // 暗色主题表面文字（纯白）
val DarkSurfaceVariant = Color(0xFF2C2C2E)  // 暗色主题表面变体
val DarkOnSurfaceVariant = Color(0xFF8E8E93) // 暗色主题表面变体文字