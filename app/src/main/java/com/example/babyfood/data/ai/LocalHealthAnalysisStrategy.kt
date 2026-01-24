package com.example.babyfood.data.ai

import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 本地健康分析策略
 * 基于规则的简单分析器，用于在本地分析体检数据
 * 参考标准：中国营养学会婴幼儿营养标准
 */
@Singleton
class LocalHealthAnalysisStrategy @Inject constructor() : HealthAnalysisService {

    /**
     * 分析体检记录
     * 基于预定义的规则判断是否存在异常
     */
    override suspend fun analyze(record: HealthRecord, baby: Baby): String? {
        val conclusions = mutableListOf<String>()

        // 1. 血红蛋白分析（缺铁性贫血）
        record.hemoglobin?.let { hemoglobin ->
            when {
                hemoglobin < 110 -> {
                    conclusions.add("血红蛋白偏低（${hemoglobin} g/L），可能存在缺铁性贫血风险，建议增加富含铁的食物摄入")
                }
                hemoglobin > 160 -> {
                    conclusions.add("血红蛋白偏高（${hemoglobin} g/L），建议复查确认")
                }
            }
        }

        // 2. 铁含量分析
        record.ironLevel?.let { ironLevel ->
            when {
                ironLevel < 7.0 -> {
                    conclusions.add("铁含量偏低（${ironLevel} mg/L），可能存在缺铁风险，建议增加红肉、肝脏等富含铁的食物")
                }
                ironLevel > 25.0 -> {
                    conclusions.add("铁含量偏高（${ironLevel} mg/L），建议咨询医生确认")
                }
            }
        }

        // 3. 钙含量分析
        record.calciumLevel?.let { calciumLevel ->
            when {
                calciumLevel < 2.0 -> {
                    conclusions.add("钙含量偏低（${calciumLevel} mg/L），可能存在缺钙风险，建议增加奶制品、豆制品等富含钙的食物")
                }
                calciumLevel > 3.0 -> {
                    conclusions.add("钙含量偏高（${calciumLevel} mg/L），建议咨询医生确认")
                }
            }
        }

        // 4. 体重分析（基于月龄的简单判断）
        record.weight?.let { weight ->
            val ageInMonths = baby.ageInMonths
            when {
                weight < 5.0 -> {
                    conclusions.add("体重偏轻（${weight} kg），建议增加营养摄入，关注生长发育情况")
                }
                weight > 15.0 && ageInMonths < 24 -> {
                    conclusions.add("体重偏重（${weight} kg），建议控制饮食，避免过度喂养")
                }
            }
        }

        // 5. 身高分析
        record.height?.let { height ->
            val ageInMonths = baby.ageInMonths
            when {
                height < 50.0 && ageInMonths > 6 -> {
                    conclusions.add("身高偏矮（${height} cm），建议关注生长发育情况，必要时咨询医生")
                }
            }
        }

        // 6. 头围分析
        record.headCircumference?.let { headCircumference ->
            val ageInMonths = baby.ageInMonths
            when {
                headCircumference < 35.0 && ageInMonths > 6 -> {
                    conclusions.add("头围偏小（${headCircumference} cm），建议咨询医生确认")
                }
                headCircumference > 50.0 && ageInMonths < 12 -> {
                    conclusions.add("头围偏大（${headCircumference} cm），建议咨询医生确认")
                }
            }
        }

        // 7. 综合判断
        return when {
            conclusions.isEmpty() -> null
            conclusions.size == 1 -> conclusions[0]
            else -> conclusions.joinToString("\n")
        }
    }

    /**
     * 获取正常血红蛋白范围（根据月龄）
     */
    private fun getNormalHemoglobinRange(ageInMonths: Int): Pair<Float, Float> {
        return when {
            ageInMonths < 6 -> 110f to 160f
            ageInMonths < 12 -> 110f to 145f
            ageInMonths < 24 -> 110f to 140f
            else -> 120f to 160f
        }
    }

    /**
     * 获取正常铁含量范围（根据月龄）
     */
    private fun getNormalIronRange(ageInMonths: Int): Pair<Float, Float> {
        return when {
            ageInMonths < 6 -> 7.0f to 18.0f
            ageInMonths < 12 -> 7.0f to 20.0f
            else -> 7.0f to 25.0f
        }
    }

    /**
     * 获取正常钙含量范围（根据月龄）
     */
    private fun getNormalCalciumRange(ageInMonths: Int): Pair<Float, Float> {
        return when {
            ageInMonths < 6 -> 2.0f to 2.7f
            ageInMonths < 12 -> 2.1f to 2.7f
            else -> 2.2f to 2.7f
        }
    }
}