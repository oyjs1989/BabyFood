package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 统一的主页面布局组件
 * 自动处理底部导航栏的遮挡问题，保持所有页面布局风格统一
 *
 * @param modifier 修饰符
 * @param content 页面内容
 */
@Composable
fun MainContentLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面内容
        content()

        // 添加底部内边距，确保最后一个项目不会被底部导航栏遮挡
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 带悬浮按钮的主页面布局组件
 * 自动处理底部导航栏的遮挡问题，并显示悬浮按钮
 *
 * @param modifier 修饰符
 * @param floatingActionButtonContent 悬浮按钮内容
 * @param content 页面内容
 */
@Composable
fun MainContentLayoutWithFab(
    modifier: Modifier = Modifier,
    floatingActionButtonContent: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 页面内容
            content()

            // 添加底部内边距，确保最后一个项目不会被底部导航栏遮挡
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 悬浮按钮
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            floatingActionButtonContent()
        }
    }
}