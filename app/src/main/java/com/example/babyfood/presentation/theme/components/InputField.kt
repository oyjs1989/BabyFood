package com.example.babyfood.presentation.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.Error
import com.example.babyfood.presentation.theme.InputFieldRadius
import com.example.babyfood.presentation.theme.InputFieldShape
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.SpacingSM
import com.example.babyfood.presentation.theme.SpacingXS
import com.example.babyfood.presentation.theme.TextPrimary
import com.example.babyfood.presentation.theme.TextSecondary
import com.example.babyfood.presentation.theme.TextTertiary

// ===== 文本输入框组件 - 16dp 圆角 =====
// 应用场景：文本输入框、搜索框、表单输入域
// 默认状态：#E5E5E5 边框，1dp 宽度，#FFFFFF 背景
// 聚焦状态：#FF9F69 边框，2dp 宽度
// 错误状态：#F5222D 边框，2dp 宽度
// 禁用状态：#E5E5E5 边框，#F5F5F5 背景，文字置灰

@Composable
fun BabyFoodInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPassword: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = if (label != null) {
                { Text(label, color = if (isError) Error else TextSecondary) }
            } else null,
            placeholder = if (placeholder != null) {
                { Text(placeholder, color = TextTertiary) }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isError) Error else TextSecondary,
                        modifier = Modifier.padding(SpacingXS)
                    )
                }
            } else null,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                            tint = TextSecondary
                        )
                    }
                } else if (trailingIcon != null) {
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        enabled = onTrailingIconClick != null
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            },
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            shape = InputFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Error else Primary,
                unfocusedBorderColor = if (isError) Error else Outline,
                disabledBorderColor = Outline,
                errorBorderColor = Error,
                focusedContainerColor = if (enabled) Color.White else Color(0xFFF5F5F5),
                unfocusedContainerColor = if (enabled) Color.White else Color(0xFFF5F5F5),
                disabledContainerColor = Color(0xFFF5F5F5),
                cursorColor = if (isError) Error else Primary,
                focusedLabelColor = if (isError) Error else Primary,
                unfocusedLabelColor = if (isError) Error else TextSecondary,
                focusedTextColor = if (enabled) TextPrimary else TextTertiary,
                unfocusedTextColor = if (enabled) TextPrimary else TextTertiary,
                disabledTextColor = TextTertiary,
                focusedPlaceholderColor = TextTertiary,
                unfocusedPlaceholderColor = TextTertiary,
                disabledPlaceholderColor = TextTertiary,
                focusedLeadingIconColor = if (isError) Error else TextSecondary,
                unfocusedLeadingIconColor = if (isError) Error else TextSecondary,
                disabledLeadingIconColor = TextTertiary,
                focusedTrailingIconColor = if (isError) Error else TextSecondary,
                unfocusedTrailingIconColor = if (isError) Error else TextSecondary,
                disabledTrailingIconColor = TextTertiary
            ),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                color = if (enabled) TextPrimary else TextTertiary
            )
        )

        // 错误提示文字
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Error,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = SpacingSM, top = SpacingXS)
            )
        }
    }
}

// ===== 搜索框组件 =====
// 专用于搜索的输入框，带搜索图标

@Composable
fun BabyFoodSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索...",
    onSearch: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    var searchFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextTertiary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = TextSecondary,
                modifier = Modifier.padding(SpacingXS)
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "清除",
                        tint = TextSecondary
                    )
                }
            }
        } else null,
        singleLine = true,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = InputFieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Outline,
            disabledBorderColor = Outline,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFF5F5F5),
            cursorColor = Primary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextTertiary,
            focusedPlaceholderColor = TextTertiary,
            unfocusedPlaceholderColor = TextTertiary,
            disabledPlaceholderColor = TextTertiary,
            focusedLeadingIconColor = TextSecondary,
            unfocusedLeadingIconColor = TextSecondary,
            disabledLeadingIconColor = TextTertiary,
            focusedTrailingIconColor = TextSecondary,
            unfocusedTrailingIconColor = TextSecondary,
            disabledTrailingIconColor = TextTertiary
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch?.invoke()
            }
        )
    )
}