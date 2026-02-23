package com.example.babyfood.data.ai.ruleengine

/**
 * 年龄禁忌规则
 * 基于中国营养学会《婴幼儿喂养指南》和 WHO 婴幼儿喂养建议
 */
object AgeRestrictionRules {

    /**
     * 年龄禁忌食材列表
     * key: 年龄段（月），value: 禁忌食材列表
     */
    private val restrictions = mapOf(
        // 0-6个月：纯母乳或配方奶，不添加辅食
        0 to listOf(
            "所有固体食物", "蜂蜜", "牛奶", "果汁", "盐", "糖"
        ),

        // 6-12个月
        6 to listOf(
            "蜂蜜",          // 肉毒杆菌风险
            "整颗坚果",      // 窒息风险
            "牛奶",          // 不推荐作为主饮品，仅限医生指导下少量引入
            "果汁",          // 高糖，影响牙齿和食欲
            "盐",            // 肾脏负担
            "糖",            // 偏好养成
            "整颗葡萄",      // 窒息风险
            "整颗樱桃",      // 窒息风险
            "爆米花",        // 窒息风险
            "硬糖",          // 窒息风险
            "生鱼片",        // 寄生虫风险
            "生鸡蛋",        // 沙门氏菌风险
            "含咖啡因饮料",  // 影响神经系统
            "茶",            // 影响铁吸收
            "含酒精食物"     // 损伤神经系统
        ),

        // 12-24个月
        12 to listOf(
            "蜂蜜",          // 仍需避免
            "整颗坚果",      // 窒息风险
            "含咖啡因饮料",  // 影响神经系统
            "茶",            // 影响铁吸收
            "含酒精食物",    // 损伤神经系统
            "爆米花",        // 窒息风险
            "硬糖",          // 窒息风险
            "生鱼片"         // 寄生虫风险
        ),

        // 24-36个月
        24 to listOf(
            "含咖啡因饮料",  // 影响神经系统
            "茶",            // 影响铁吸收
            "含酒精食物"     // 损伤神经系统
        )
    )

    /**
     * 获取指定年龄段的禁忌食材
     */
    fun getRestrictions(ageInMonths: Int): List<String> {
        return when {
            ageInMonths < 6 -> restrictions[0] ?: emptyList()
            ageInMonths < 12 -> restrictions[6] ?: emptyList()
            ageInMonths < 24 -> restrictions[12] ?: emptyList()
            else -> restrictions[24] ?: emptyList()
        }
    }

    /**
     * 检查食材是否在禁忌列表中
     */
    fun isRestricted(ingredient: String, ageInMonths: Int): Boolean {
        val restrictions = getRestrictions(ageInMonths)
        return restrictions.any { restriction ->
            ingredient.contains(restriction, ignoreCase = true) ||
            restriction.contains(ingredient, ignoreCase = true)
        }
    }

    /**
     * 检查食材列表中是否包含禁忌食材
     */
    fun containsRestrictedIngredient(ingredients: List<String>, ageInMonths: Int): Pair<Boolean, List<String>> {
        val restrictions = getRestrictions(ageInMonths)
        val foundRestrictions = ingredients.filter { ingredient ->
            restrictions.any { restriction ->
                ingredient.contains(restriction, ignoreCase = true) ||
                restriction.contains(ingredient, ignoreCase = true)
            }
        }
        return Pair(foundRestrictions.isNotEmpty(), foundRestrictions)
    }

    /**
     * 获取年龄段推荐说明
     */
    fun getRecommendationNotes(ageInMonths: Int): List<String> {
        return when {
            ageInMonths < 6 -> listOf(
                "6个月以下仅需母乳或配方奶",
                "不建议添加任何辅食"
            )
            ageInMonths < 12 -> listOf(
                "食物需做成泥糊状",
                "每次只添加一种新食物",
                "观察3-5天确认无过敏反应",
                "避免高盐、高糖食物",
                "牛奶不推荐作为主饮品"
            )
            ageInMonths < 24 -> listOf(
                "食物可逐渐过渡到小块状",
                "培养自主进食能力",
                "保证每日奶量500ml左右",
                "避免整颗坚果等窒息风险食物"
            )
            else -> listOf(
                "食物可接近成人饮食",
                "继续保证奶量300-500ml",
                "培养良好饮食习惯",
                "避免含咖啡因和酒精的食品"
            )
        }
    }
}