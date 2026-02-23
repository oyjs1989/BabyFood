package com.example.babyfood.data.ai

import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord

/**
 * AI 健康分析服务接口
 * 用于分析体检记录数据，生成健康建议
 */
interface HealthAnalysisService {

    /**
     * 分析体检记录
     * @param record 体检记录数据
     * @param baby 宝宝信息（用于获取准确的出生日期计算月龄）
     * @return 分析结论（如果无异常则返回 null）
     */
    suspend fun analyze(record: HealthRecord, baby: Baby): String?
}