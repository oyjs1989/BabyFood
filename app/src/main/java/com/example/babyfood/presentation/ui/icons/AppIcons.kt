package com.example.babyfood.presentation.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
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
import com.example.babyfood.R

object AppIcons {

    const val BRAND_LOGO_EMOJI = "👶"

    // Bottom navigation icons
    val Home: ImageVector = Icons.Default.Home
    val Recipes: ImageVector = Icons.Default.Favorite
    val Plans: ImageVector = Icons.Default.CalendarMonth
    val Inventory: ImageVector = Icons.Default.Inventory2
    val Baby: ImageVector = Icons.Default.Person

    // Authentication icons
    val Account: ImageVector = Icons.Default.Email
    val Phone: ImageVector = Icons.Default.Phone
    val Password: ImageVector = Icons.Default.Lock
    val VerificationCode: ImageVector = Icons.Default.CheckCircle
    val Visibility: ImageVector = Icons.Default.Visibility
    val VisibilityOff: ImageVector = Icons.Default.VisibilityOff

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
}