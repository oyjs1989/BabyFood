package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.babyfood.presentation.theme.AvocadoPrimary
import com.example.babyfood.presentation.theme.Charcoal
import com.example.babyfood.presentation.theme.Peach
import com.example.babyfood.presentation.theme.SoftBrown

/**
 * 底部导航栏项定义
 */
sealed class AvocadoNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : AvocadoNavItem("home", Icons.Default.Home, "Home")
    object Recipes : AvocadoNavItem("recipes", Icons.Default.MenuBook, "Recipes")
    object Growth : AvocadoNavItem("plans", Icons.Default.Favorite, "Growth")
    object Profile : AvocadoNavItem("baby", Icons.Default.Person, "Profile")
}

/**
 * 全局底部导航栏组件
 * 设计风格：白色半透明背景 + Peach 阴影 + 中心扫描按钮
 */
@Composable
fun AvocadoBottomBar(
    navController: NavController,
    onScanClick: () -> Unit = {}
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    val items = listOf(
        AvocadoNavItem.Home,
        AvocadoNavItem.Recipes,
        null, // Spacer for Scan button
        AvocadoNavItem.Growth,
        AvocadoNavItem.Profile
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Bar - 白色半透明背景 + Peach 阴影
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    ambientColor = Peach.copy(alpha = 0.3f),
                    spotColor = Peach.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(
                    color = Color.White.copy(alpha = 0.95f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    if (item == null) {
                        // Center placeholder for floating button
                        Spacer(modifier = Modifier.size(64.dp))
                    } else {
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        AvocadoNavItemView(
                            item = item,
                            isSelected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // Centered Scan Button - Peach 背景，白色边框
        Column(
            modifier = Modifier
                .offset(y = (-24).dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onScanClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = Peach.copy(alpha = 0.4f),
                        spotColor = Peach.copy(alpha = 0.4f)
                    )
                    .background(Peach, CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Scan",
                    tint = Charcoal,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "SCAN",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Charcoal,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AvocadoNavItemView(
    item: AvocadoNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 选中：Peach 色，未选中：SoftBrown 50% 透明度
    val color = if (isSelected) Peach else SoftBrown.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = color
        )
    }
}
