package com.example.babyfood.presentation.ui.growth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.domain.model.GrowthRecord
import com.example.babyfood.domain.model.GrowthStandard
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

@Composable
fun GrowthChart(
    growthRecords: List<GrowthRecord>,
    isBoy: Boolean,
    chartType: ChartType,
    birthDate: LocalDate?, // 宝宝的出生日期，用于计算月龄
    modifier: Modifier = Modifier
) {
    val sortedRecords = remember(growthRecords) {
        growthRecords.sortedBy { it.recordDate }
    }
    
    val modelProducer = remember { CartesianChartModelProducer.build() }
    
    LaunchedEffect(sortedRecords, isBoy, chartType, birthDate) {
        modelProducer.tryRunTransaction {
            lineSeries {
                // 计算月龄范围
                val (startMonths, endMonths) = if (sortedRecords.isNotEmpty() && birthDate != null) {
                    // 有用户数据时，根据记录日期计算月龄范围
                    val firstAgeInMonths = calculateAgeInMonths(birthDate, sortedRecords.first().recordDate)
                    val lastAgeInMonths = calculateAgeInMonths(birthDate, sortedRecords.last().recordDate)
                    firstAgeInMonths to lastAgeInMonths
                } else {
                    // 没有用户数据时，使用默认月龄范围（0-36个月）
                    0 to 36
                }
                
                // 生成标准曲线数据
                val standardCurve = generateStandardCurve(
                    isBoy = isBoy,
                    startMonths = startMonths,
                    endMonths = endMonths,
                    chartType = chartType
                )
                
                // P3 标准线（下界）
                series(
                    x = standardCurve.map { it.first },
                    y = standardCurve.map { it.second },
                )
                
                // P50 标准线（中位线）
                series(
                    x = standardCurve.map { it.first },
                    y = standardCurve.map { it.third },
                )
                
                // P97 标准线（上界）
                series(
                    x = standardCurve.map { it.first },
                    y = standardCurve.map { it.fourth },
                )
                
                // 实际测量数据（如果有且知道出生日期）
                if (sortedRecords.isNotEmpty() && birthDate != null) {
                    series(
                        x = sortedRecords.map { calculateAgeInMonths(birthDate, it.recordDate).toFloat() },
                        y = sortedRecords.map { 
                            when (chartType) {
                                ChartType.WEIGHT -> it.weight
                                ChartType.HEIGHT -> it.height
                                ChartType.HEAD_CIRCUMFERENCE -> it.headCircumference ?: 0f
                            }
                        },
                    )
                }
            }
        }
    }
    
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = rememberStartAxis(
                title = when (chartType) {
                    ChartType.WEIGHT -> "体重 (kg)"
                    ChartType.HEIGHT -> "身高 (cm)"
                    ChartType.HEAD_CIRCUMFERENCE -> "头围 (cm)"
                },
                valueFormatter = { value, _, _ ->
                    // 格式化Y轴标签，保留1位小数
                    String.format("%.1f", value)
                }
            ),
            bottomAxis = rememberBottomAxis(
                title = "月龄",
                valueFormatter = { value, _, _ ->
                    // 格式化X轴标签为月龄
                    String.format("%.0f", value)
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

private fun generateStandardCurve(
    isBoy: Boolean,
    startMonths: Int?,
    endMonths: Int?,
    chartType: ChartType
): List<Quadruple<Float, Float, Float, Float>> {
    val actualStartMonths = startMonths ?: 0
    val actualEndMonths = endMonths ?: 36
    
    val smoothCurve = GrowthStandard.generateSmoothCurve(isBoy, actualStartMonths, actualEndMonths)
    
    return smoothCurve.map { standard ->
        val (p3, p50, p97) = when (chartType) {
            ChartType.WEIGHT -> Triple(standard.weightP3, standard.weightP50, standard.weightP97)
            ChartType.HEIGHT -> Triple(standard.heightP3, standard.heightP50, standard.heightP97)
            ChartType.HEAD_CIRCUMFERENCE -> Triple(
                standard.headCircumferenceP3 ?: 0f,
                standard.headCircumferenceP50 ?: 0f,
                standard.headCircumferenceP97 ?: 0f
            )
        }
        Quadruple(standard.ageInMonths.toFloat(), p3, p50, p97)
    }
}

// 计算月龄的辅助函数
private fun calculateAgeInMonths(birthDate: LocalDate, recordDate: LocalDate): Int {
    val yearsDiff = recordDate.year - birthDate.year
    val monthsDiff = recordDate.monthNumber - birthDate.monthNumber
    val totalMonths = yearsDiff * 12 + monthsDiff
    
    // 如果记录日期的日小于出生日期的日，减去一个月
    return if (recordDate.dayOfMonth < birthDate.dayOfMonth) {
        totalMonths - 1
    } else {
        totalMonths
    }
}

enum class ChartType {
    WEIGHT, HEIGHT, HEAD_CIRCUMFERENCE
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
