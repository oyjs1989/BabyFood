package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.babyfood.data.service.SafetyRiskAnalyzer
import com.example.babyfood.domain.model.RiskLevel

/**
 * 处理建议对话框
 *
 * 显示食材安全风险和处理建议，允许用户查看详细信息和选择忽略警告
 *
 * @param analysis 食谱安全分析结果
 * @param onDismiss 关闭对话框
 * @param onIgnoreWarning 忽略警告回调 (ingredientName: String)
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandlingAdviceDialog(
    analysis: SafetyRiskAnalyzer.RecipeSafetyAnalysis,
    onDismiss: () -> Unit,
    onIgnoreWarning: ((ingredientName: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SafetyWarningIcon(riskLevel = analysis.overallRiskLevel)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "安全风险提示",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // 食谱名称
                Text(
                    text = analysis.recipeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 推荐操作
                Text(
                    text = analysis.recommendedAction,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // 风险列表
                if (analysis.risks.isNotEmpty()) {
                    Text(
                        text = "风险详情",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .weight(1f, fill = false)
                    ) {
                        analysis.risks.forEach { risk ->
                            IngredientRiskCard(
                                risk = risk,
                                onIgnoreWarning = onIgnoreWarning,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "✓ 该食谱食材安全，适合食用",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

/**
 * 食材风险卡片
 *
 * 显示单个食材的风险详情
 */
@Composable
private fun IngredientRiskCard(
    risk: SafetyRiskAnalyzer.IngredientRisk,
    onIgnoreWarning: ((ingredientName: String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val cardColor = when (risk.riskLevel) {
        RiskLevel.FORBIDDEN -> Color(0xFFFFEBEE)  // 浅红色
        RiskLevel.NOT_RECOMMENDED -> Color(0xFFFFF8E1)  // 浅黄色
        RiskLevel.REQUIRES_SPECIAL_HANDLING -> Color(0xFFE3F2FD)  // 浅蓝色
        RiskLevel.CAUTIOUS_INTRODUCTION -> Color(0xFFFFF3E0)  // 浅橙色
        RiskLevel.NORMAL -> Color.Transparent
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 食材名称和风险等级
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SafetyWarningBadge(riskLevel = risk.riskLevel)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = risk.ingredientName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "严重程度: ${risk.severity}/10",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 风险原因
            Text(
                text = "风险原因：${risk.riskReason}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // 处理建议
            if (!risk.handlingAdvice.isNullOrBlank()) {
                Text(
                    text = "处理建议：${risk.handlingAdvice}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 忽略次数提示
            if (risk.ignoreCount > 0) {
                Text(
                    text = "⚠️ 您已忽略此警告 ${risk.ignoreCount} 次",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 忽略警告按钮（仅对非 FORBIDDEN 级别显示）
            if (risk.riskLevel != RiskLevel.FORBIDDEN && onIgnoreWarning != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onIgnoreWarning(risk.ingredientName) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (risk.ignoreCount > 0) "再次忽略" else "忽略此警告",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}