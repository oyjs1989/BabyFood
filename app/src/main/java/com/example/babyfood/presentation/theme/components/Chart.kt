package com.example.babyfood.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.ElevationLevel1
import com.example.babyfood.presentation.theme.SpacingMD

// ===== 生长曲线图表样式组件 =====
// 应用场景：生长曲线可视化、体重/身高/头围对比

/**
 * 生长曲线图表样式配置
 * 
 * 颜色规范：
 * - WHO 标准参考线：#88C999（柔和绿）
 * - 中国标准参考线：#73A6FF（清新蓝）
 * - 宝宝实际数据线：#FF9F69（主暖橙）
 * - 曲线下方填充区域：#FF9F69 20% 透明度
 */

/**
 * WHO 标准线颜色
 * #88C999（柔和绿）
 */
val WhoStandardLineColor = Color(0xFF88C999)

/**
 * 中国标准线颜色
 * #73A6FF（清新蓝）
 */
val ChinaStandardLineColor = Color(0xFF73A6FF)

/**
 * 宝宝实际数据线颜色
 * #FF9F69（主暖橙）
 */
val BabyDataLineColor = Color(0xFFFF9F69)

/**
 * 宝宝数据线填充颜色（20% 透明度）
 * #FF9F69 20% 透明度
 */
val BabyDataFillColor = Color(0x33FF9F69)  // 20% 透明度

/**
 * 生长曲线图表容器
 * @param modifier 修饰符
 * @param content 图表内容
 */
@Composable
fun BabyFoodGrowthChartContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(SpacingMD),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = ElevationLevel1
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingMD)
        ) {
            content()
        }
    }
}

/**
 * 图表图例组件
 * @param legendItems 图例项列表
 */
@Composable
fun GrowthChartLegend(
    legendItems: List<LegendItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMD),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        legendItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = item.color,
                            shape = CircleShape
                        )
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = item.color
                )
            }
        }
    }
}

/**
 * 图例项数据类
 */
data class LegendItem(
    val label: String,
    val color: Color
)

/**
 * 生长曲线颜色配置
 */
object GrowthChartColors {
    /**
     * WHO P3 标准线颜色（下界）
     */
    val WhoP3 = Color(0xFF88C999)
    
    /**
     * WHO P50 标准线颜色（中位线）
     */
    val WhoP50 = Color(0xFF88C999)
    
    /**
     * WHO P97 标准线颜色（上界）
     */
    val WhoP97 = Color(0xFF88C999)
    
    /**
     * 中国 P3 标准线颜色（下界）
     */
    val ChinaP3 = Color(0xFF73A6FF)
    
    /**
     * 中国 P50 标准线颜色（中位线）
     */
    val ChinaP50 = Color(0xFF73A6FF)
    
    /**
     * 中国 P97 标准线颜色（上界）
     */
    val ChinaP97 = Color(0xFF73A6FF)
    
    /**
     * 宝宝数据线颜色
     */
    val BabyData = Color(0xFFFF9F69)
    
    /**
     * 宝宝数据线填充颜色（20% 透明度）
     */
    val BabyDataFill = Color(0x33FF9F69)
}

/**
 * 获取标准图例项
 * @param includeBabyData 是否包含宝宝数据项
 * @param standardType 标准类型（WHO或中国）
 */
@Composable
fun getStandardLegendItems(
    includeBabyData: Boolean = false,
    standardType: StandardType = StandardType.WHO
): List<LegendItem> {
    val items = mutableListOf<LegendItem>()
    
    when (standardType) {
        StandardType.WHO -> {
            items.add(LegendItem("WHO P3", GrowthChartColors.WhoP3))
            items.add(LegendItem("WHO P50", GrowthChartColors.WhoP50))
            items.add(LegendItem("WHO P97", GrowthChartColors.WhoP97))
        }
        StandardType.CHINA -> {
            items.add(LegendItem("中国 P3", GrowthChartColors.ChinaP3))
            items.add(LegendItem("中国 P50", GrowthChartColors.ChinaP50))
            items.add(LegendItem("中国 P97", GrowthChartColors.ChinaP97))
        }
    }
    
    if (includeBabyData) {
        items.add(LegendItem("宝宝", GrowthChartColors.BabyData))
    }
    
    return items
}

/**
 * 标准类型枚举
 */
enum class StandardType {
    WHO,
    CHINA
}

/**
 * 图表类型枚举
 */
enum class ChartType {
    WEIGHT,
    HEIGHT,
    HEAD_CIRCUMFERENCE
}