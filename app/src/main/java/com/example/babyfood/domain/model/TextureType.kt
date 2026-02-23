package com.example.babyfood.domain.model

import android.util.Log

/**
 * 质地类型枚举
 *
 * 根据GB 10770-2025国标和WHO婴幼儿喂养指南定义的4个质地等级
 *
 * @property displayName 显示名称
 * @property description 质地描述
 * @property particleSize 颗粒尺寸（毫米）
 * @property chewingAbility 咀嚼能力要求
 * @property example 示例食物
 * @property applicableAgeRange 适用月龄范围
 */
enum class TextureType(
    val displayName: String,
    val description: String,
    val particleSize: Float?,
    val chewingAbility: String,
    val example: String,
    val applicableAgeRange: IntRange
) {
    /**
     * 泥糊状（6-8个月）
     * 可用舌头压碎，质地如软豆腐
     */
    PUREE(
        displayName = "泥糊状",
        description = "可用舌头压碎，质地如软豆腐",
        particleSize = 5f,
        chewingAbility = "舌头压碎",
        example = "南瓜米糊、胡萝卜土豆泥",
        applicableAgeRange = 6..8
    ),

    /**
     * 稠泥/末状（9-11个月）
     * 可用牙床压碎，质地如香蕉
     */
    THICK_PUREE(
        displayName = "稠泥/末状",
        description = "可用牙床压碎，质地如香蕉",
        particleSize = 8f,
        chewingAbility = "牙床压碎",
        example = "鸡肉粥、牛肉土豆泥",
        applicableAgeRange = 9..11
    ),

    /**
     * 软固体/块状（12-18个月）
     * 可用牙床咀嚼，质地如肉丸子
     */
    SOFT_SOLID(
        displayName = "软固体/块状",
        description = "可用牙床咀嚼，质地如肉丸子",
        particleSize = 10f,
        chewingAbility = "牙床咀嚼",
        example = "蔬菜肉丸、小馄饨",
        applicableAgeRange = 12..18
    ),

    /**
     * 家庭膳食（18个月以上）
     * 可适当调整的家庭常规菜肴
     */
    FAMILY_MEAL(
        displayName = "家庭膳食",
        description = "可适当调整的家庭常规菜肴",
        particleSize = null,
        chewingAbility = "成人咀嚼",
        example = "家庭常规菜肴",
        applicableAgeRange = 18..24
    );

    companion object {
        private const val TAG = "TextureType"

        /**
         * 根据月龄获取推荐的质地类型
         *
         * @param ageInMonths 月龄
         * @return 推荐的质地类型
         */
        fun getByAge(ageInMonths: Int): TextureType? {
            Log.d(TAG, "========== 获取推荐质地 ==========")
            Log.d(TAG, "月龄: $ageInMonths 个月")

            val result = when {
                ageInMonths < 6 -> {
                    Log.d(TAG, "⚠️ 月龄小于6个月，不推荐辅食")
                    null
                }
                ageInMonths <= 8 -> PUREE
                ageInMonths <= 11 -> THICK_PUREE
                ageInMonths <= 18 -> SOFT_SOLID
                ageInMonths <= 24 -> FAMILY_MEAL
                else -> {
                    Log.d(TAG, "⚠️ 月龄超过24个月，推荐家庭膳食")
                    FAMILY_MEAL
                }
            }

            Log.d(TAG, "推荐质地: ${result?.displayName}")
            Log.d(TAG, "========== 获取推荐质地完成 ==========")
            return result
        }

        /**
         * 检查质地类型是否适合指定月龄
         *
         * @param textureType 质地类型
         * @param ageInMonths 月龄
         * @return 是否适合
         */
        fun isSuitableForAge(textureType: TextureType, ageInMonths: Int): Boolean {
            Log.d(TAG, "========== 检查质地适用性 ==========")
            Log.d(TAG, "质地类型: ${textureType.displayName}")
            Log.d(TAG, "月龄: $ageInMonths 个月")

            val result = ageInMonths in textureType.applicableAgeRange

            if (result) {
                Log.d(TAG, "✓ 质地适合当前月龄")
            } else {
                Log.d(TAG, "❌ 质地不适合当前月龄")
            }

            Log.d(TAG, "========== 检查质地适用性完成 ==========")
            return result
        }

        /**
         * 获取所有质地类型列表（按月龄排序）
         *
         * @return 质地类型列表
         */
        fun getAllTypes(): List<TextureType> {
            Log.d(TAG, "========== 获取所有质地类型 ==========")
            val result = entries.toList()
            Log.d(TAG, "✓ 获取到 ${result.size} 种质地类型")
            Log.d(TAG, "========== 获取所有质地类型完成 ==========")
            return result
        }

        /**
         * 根据显示名称获取质地类型
         *
         * @param displayName 显示名称
         * @return 质地类型，如果未找到则返回null
         */
        fun getByDisplayName(displayName: String): TextureType? {
            Log.d(TAG, "========== 根据名称获取质地类型 ==========")
            Log.d(TAG, "名称: $displayName")

            val result = entries.find { it.displayName == displayName }

            if (result != null) {
                Log.d(TAG, "✓ 找到质地类型: ${result.displayName}")
            } else {
                Log.d(TAG, "⚠️ 未找到质地类型: $displayName")
            }

            Log.d(TAG, "========== 根据名称获取质地类型完成 ==========")
            return result
        }

        /**
         * 获取质地适配建议
         *
         * @param currentTexture 当前质地
         * @param ageInMonths 月龄
         * @return 适配建议
         */
        fun getTextureAdvice(currentTexture: TextureType, ageInMonths: Int): TextureAdvice {
            Log.d(TAG, "========== 获取质地适配建议 ==========")
            Log.d(TAG, "当前质地: ${currentTexture.displayName}")
            Log.d(TAG, "月龄: $ageInMonths 个月")

            val recommendedTexture = getByAge(ageInMonths)

            val advice = when {
                recommendedTexture == null -> {
                    TextureAdvice(
                        currentTexture = currentTexture,
                        recommendedTexture = null,
                        status = TextureStatus.TOO_YOUNG,
                        message = "宝宝月龄小于6个月，还不适合添加辅食"
                    )
                }
                currentTexture == recommendedTexture -> {
                    TextureAdvice(
                        currentTexture = currentTexture,
                        recommendedTexture = recommendedTexture,
                        status = TextureStatus.SUITABLE,
                        message = "当前质地适合宝宝月龄，可以放心食用"
                    )
                }
                currentTexture.applicableAgeRange.last < ageInMonths -> {
                    // 当前质地过于简单
                    TextureAdvice(
                        currentTexture = currentTexture,
                        recommendedTexture = recommendedTexture,
                        status = TextureStatus.TOO_SIMPLE,
                        message = "当前质地过于简单，建议尝试更丰富的质地以锻炼咀嚼能力"
                    )
                }
                currentTexture.applicableAgeRange.first > ageInMonths -> {
                    // 当前质地过于复杂
                    TextureAdvice(
                        currentTexture = currentTexture,
                        recommendedTexture = recommendedTexture,
                        status = TextureStatus.TOO_COMPLEX,
                        message = "当前质地过于复杂，可能增加宝宝吞咽困难风险，建议选择更简单的质地"
                    )
                }
                else -> {
                    TextureAdvice(
                        currentTexture = currentTexture,
                        recommendedTexture = recommendedTexture,
                        status = TextureStatus.SUITABLE,
                        message = "当前质地适合宝宝月龄"
                    )
                }
            }

            Log.d(TAG, "建议状态: ${advice.status}")
            Log.d(TAG, "建议消息: ${advice.message}")
            Log.d(TAG, "========== 获取质地适配建议完成 ==========")

            return advice
        }
    }
}

/**
 * 质地适配状态
 */
enum class TextureStatus {
    /** 适合 */
    SUITABLE,

    /** 过于简单 */
    TOO_SIMPLE,

    /** 过于复杂 */
    TOO_COMPLEX,

    /** 月龄过小 */
    TOO_YOUNG
}

/**
 * 质地适配建议
 *
 * @property currentTexture 当前质地
 * @property recommendedTexture 推荐质地
 * @property status 适配状态
 * @property message 建议消息
 */
data class TextureAdvice(
    val currentTexture: TextureType,
    val recommendedTexture: TextureType?,
    val status: TextureStatus,
    val message: String
)