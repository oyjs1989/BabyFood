package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.ButtonPrimary

/**
 * 登录按钮组件
 *
 * 在 Header 右侧显示的登录按钮
 *
 * @param onClick 点击回调（必填）
 * @param modifier 修饰符（可选）
 */
@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .semantics {
                contentDescription = "登录"
                role = Role.Button
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonPrimary,
            contentColor = Color.White
        )
    ) {
        Text(text = "登录")
    }
}