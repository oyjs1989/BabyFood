package com.example.babyfood.presentation.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * BabyFood 设计系统 - 尺寸规范
 * 
 * 基于 8dp 网格系统，确保视觉一致性和开发效率
 */

// ===== 间距系统 (Spacing) =====
object Spacing {
    /** 超小间距 4dp - 用于图标与文字的紧密排列 */
    val xs = 4.dp
    
    /** 小间距 8dp - 用于组件内部元素间距 */
    val sm = 8.dp
    
    /** 中间距 16dp - 标准组件间距 */
    val md = 16.dp
    
    /** 大间距 24dp - 用于卡片内部 padding */
    val lg = 24.dp
    
    /** 超大间距 32dp - 用于页面级间距 */
    val xl = 32.dp
    
    /** 特大间距 48dp - 用于大型模块分隔 */
    val xxl = 48.dp
}

// ===== 内边距系统 (Padding) =====
object Padding {
    /** 小内边距 8dp */
    val small = 8.dp
    
    /** 中等内边距 16dp */
    val medium = 16.dp
    
    /** 大内边距 24dp */
    val large = 24.dp
    
    /** 列表项水平内边距 16dp */
    val listHorizontal = 16.dp
    
    /** 列表项垂直内边距 12dp */
    val listVertical = 12.dp
    
    /** 卡片内边距 16dp */
    val card = 16.dp
    
    /** 按钮水平内边距 24dp */
    val buttonHorizontal = 24.dp
    
    /** 按钮垂直内边距 12dp */
    val buttonVertical = 12.dp
}

// ===== 圆角系统 (Corner Radius) =====
object CornerRadius {
    /** 小圆角 4dp - 用于标签、小按钮 */
    val small = 4.dp
    
    /** 中等圆角 8dp - 用于输入框、卡片 */
    val medium = 8.dp
    
    /** 大圆角 12dp - 用于大卡片、对话框 */
    val large = 12.dp
    
    /** 超大圆角 16dp - 用于底部弹窗 */
    val xlarge = 16.dp
    
    /** 圆形 50% - 用于头像、浮动按钮 */
    val circle = 50.dp
}

// ===== 高度/尺寸系统 (Size) =====
object Size {
    /** 小型按钮高度 32dp */
    val buttonSmall = 32.dp
    
    /** 标准按钮高度 44dp */
    val button = 44.dp
    
    /** 大型按钮高度 52dp */
    val buttonLarge = 52.dp
    
    /** 输入框高度 48dp */
    val input = 48.dp
    
    /** 标准图标 24dp */
    val icon = 24.dp
    
    /** 小型图标 16dp */
    val iconSmall = 16.dp
    
    /** 大型图标 32dp */
    val iconLarge = 32.dp
    
    /** 头像小 40dp */
    val avatarSmall = 40.dp
    
    /** 头像中 56dp */
    val avatar = 56.dp
    
    /** 头像大 72dp */
    val avatarLarge = 72.dp
    
    /** 分隔线高度 1dp */
    val divider = 1.dp
    
    /** 底部导航栏高度 56dp */
    val bottomNav = 56.dp
    
    /** 顶部导航栏高度 56dp */
    val topBar = 56.dp
}

// ===== 文字大小系统 (Typography) =====
object TextSize {
    /** 超小文字 10sp - 用于标签、时间戳 */
    val xs = 10.sp
    
    /** 小文字 12sp - 用于辅助说明 */
    val sm = 12.sp
    
    /** 正文 14sp - 标准正文 */
    val body = 14.sp
    
    /** 大正文 16sp - 强调正文 */
    val bodyLarge = 16.sp
    
    /** 标题小 18sp - 卡片标题 */
    val h6 = 18.sp
    
    /** 标题中 20sp - 页面标题 */
    val h5 = 20.sp
    
    /** 标题大 24sp - 大标题 */
    val h4 = 24.sp
    
    /** 标题超大 28sp - 特大标题 */
    val h3 = 28.sp
    
    /** 显示文字 32sp - 首页大标题 */
    val h2 = 32.sp
    
    /** 超大显示 40sp - 特殊强调 */
    val h1 = 40.sp
}

// ===== 行高系统 (Line Height) =====
object LineHeight {
    /** 紧凑行高 1.2 */
    const val tight = 1.2f
    
    /** 标准行高 1.5 */
    const val normal = 1.5f
    
    /** 宽松行高 1.8 */
    const val relaxed = 1.8f
}

// ===== 字重系统 (Font Weight) =====
object FontWeight {
    /** 细体 */
    const val light = 300
    
    /** 常规 */
    const val normal = 400
    
    /** 中等 */
    const val medium = 500
    
    /** 粗体 */
    const val bold = 600
    
    /** 特粗 */
    const val extraBold = 700
}

// ===== 阴影系统 (Elevation) =====
object Elevation {
    /** 无阴影 */
    val none = 0.dp
    
    /** 小阴影 2dp - 卡片默认 */
    val small = 2.dp
    
    /** 中等阴影 4dp - 悬浮按钮 */
    val medium = 4.dp
    
    /** 大阴影 8dp - 对话框 */
    val large = 8.dp
    
    /** 超大阴影 16dp - 模态层 */
    val xlarge = 16.dp
}

// ===== 边框宽度系统 (Border) =====
object BorderWidth {
    /** 细边框 0.5dp */
    val hairline = 0.5f.dp
    
    /** 标准边框 1dp */
    val normal = 1.dp
    
    /** 粗边框 2dp */
    val thick = 2.dp
}

// ===== 图片尺寸系统 (Image) =====
object ImageSize {
    /** 小图标 24dp */
    val iconSmall = 24.dp
    
    /** 中图标 40dp */
    val iconMedium = 40.dp
    
    /** 大图标 64dp */
    val iconLarge = 64.dp
    
    /** 缩略图 80dp */
    val thumbnail = 80.dp
    
    /** 卡片图片 120dp */
    val card = 120.dp
    
    /** 详情页图片 200dp */
    val detail = 200.dp
    
    /** 全宽图片比例 16:9 */
    const val aspectRatioWide = 16f / 9f
    
    /** 标准图片比例 4:3 */
    const val aspectRatioStandard = 4f / 3f
    
    /** 方形图片比例 1:1 */
    const val aspectRatioSquare = 1f
}

// ===== 列表系统 (ListItem) =====
object ListItem {
    /** 列表项高度小 48dp */
    val heightSmall = 48.dp
    
    /** 列表项高度标准 56dp */
    val height = 56.dp
    
    /** 列表项高度大 72dp */
    val heightLarge = 72.dp
    
    /** 列表项间距 0dp */
    val spacing = 0.dp
    
    /** 分组间距 8dp */
    val sectionSpacing = 8.dp
}

// ===== 触摸目标 (Touch Target) =====
object TouchTarget {
    /** 最小触摸目标 48dp (Material Design 标准) */
    val minimum = 48.dp
    
    /** 推荐触摸目标 56dp */
    val recommended = 56.dp
}

// ===== 响应式断点 (Breakpoints) =====
object Breakpoints {
    /** 手机竖屏 */
    const val compact = 600
    
    /** 手机横屏/平板竖屏 */
    const val medium = 840
    
    /** 平板横屏/桌面 */
    const val expanded = 1200
}
