package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 图像识别响应模型
 * 用于 AI 图像识别服务的响应数据
 */
@Serializable
data class ImageRecognitionResponse(
    /**
     * 识别是否成功
     */
    val success: Boolean,

    /**
     * 错误信息
     * 如果 success 为 true，此字段为 null
     */
    val errorMessage: String? = null,

    /**
     * 食材名称
     * 如：胡萝卜、牛肉、南瓜
     */
    val foodName: String,

    /**
     * 后端食材 ID
     * 暂时返回 0，由前端通过名称查询
     */
    val foodId: Long = 0L,

    /**
     * 食材图片 URL
     * 从后端获取的食材图片
     */
    val foodImageUrl: String? = null,

    /**
     * 保存方式
     * REFRIGERATOR: 冰箱冷藏
     * FREEZER: 冰箱冷冻
     * ROOM_TEMP: 常温
     */
    val storageMethod: String,

    /**
     * 估计保质期天数
     * 根据食材特性估计
     */
    val estimatedShelfLife: Int,

    /**
     * 默认单位
     * 如：g、kg、个
     */
    val defaultUnit: String,

    /**
     * 数量
     * 默认为 0，由用户手动填写
     */
    val quantity: Float = 0f,

    /**
     * 营养信息
     * 可选字段
     */
    val nutritionInfo: NutritionInfo? = null,

    /**
     * 识别置信度
     * 0-1 之间的小数
     * 图片清晰时为 0.8-1.0
     * 模糊时为 0.5-0.8
     */
    val confidence: Float,

    /**
     * 备注
     * 可选字段
     */
    val notes: String? = null
)

/**
 * 营养信息
 */
@Serializable
data class NutritionInfo(
    /**
     * 每 100g 热量 (kcal)
     */
    val calories: Float,

    /**
     * 每 100g 蛋白质
     */
    val protein: Float,

    /**
     * 每 100g 钙含量
     */
    val calcium: Float,

    /**
     * 每 100g 铁含量
     */
    val iron: Float
)