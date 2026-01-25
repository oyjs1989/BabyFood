package com.example.babyfood.presentation.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import com.example.babyfood.R
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 应用图标配置
 * 统一管理应用中使用的所有图标，方便批量调整
 */
object AppIcons {

    // ========== 品牌图标 ==========

    /**
     * 品牌Logo图标（应用图标）
     * 用于登录、注册、设置等页面的品牌展示
     */
    const val BRAND_LOGO_EMOJI = "👶"

    /**
     * 应用图标 Composable
     * 使用自定义的品牌图标（图标-圆形.png）
     */
    @Composable
    fun AppLogo(
        size: Dp = 80.dp,
        modifier: Modifier = Modifier
    ) {
        Image(
            painter = painterResource(id = IconResources.APP_LOGO),
            contentDescription = "应用Logo",
            modifier = modifier.size(size)
        )
    }

    // ========== 输入框图标 ==========

    /**
     * 账号/邮箱图标
     * 用于登录、注册页面的账号输入框
     */
    val Account: ImageVector = Icons.Default.Email

    /**
     * 手机号图标
     * 用于登录、注册页面的手机号输入框
     */
    val Phone: ImageVector = Icons.Default.Phone

    /**
     * 密码图标
     * 用于登录、注册页面的密码输入框
     */
    val Password: ImageVector = Icons.Default.Lock

    /**
     * 验证码图标
     * 用于注册页面的验证码输入框
     */
    val VerificationCode: ImageVector = Icons.Default.CheckCircle

    // ========== 密码可见性图标 ==========

    /**
     * 显示密码图标
     */
    val Visibility: ImageVector = androidx.compose.material.icons.Icons.Default.Visibility

    /**
     * 隐藏密码图标
     */
    val VisibilityOff: ImageVector = androidx.compose.material.icons.Icons.Default.VisibilityOff

    // ========== 底部导航图标 ==========

    /**
     * 首页图标
     */
    val Home: ImageVector = androidx.compose.material.icons.Icons.Default.Home

    /**
     * 食谱图标
     */
    val Recipes: ImageVector = androidx.compose.material.icons.Icons.Default.Favorite

    /**
     * 计划图标
     */
    val Plans: ImageVector = androidx.compose.material.icons.Icons.Default.CalendarMonth

    /**
     * 宝宝图标
     */
    val Baby: ImageVector = androidx.compose.material.icons.Icons.Default.Person
}