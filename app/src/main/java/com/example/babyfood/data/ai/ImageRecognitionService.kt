package com.example.babyfood.data.ai

import com.example.babyfood.domain.model.ImageRecognitionRequest
import com.example.babyfood.domain.model.ImageRecognitionResponse

/**
 * 图像识别服务接口
 * 用于通过 AI 识别食材图片并提取食材信息
 */
interface ImageRecognitionService {
    /**
     * 识别食材图片
     *
     * @param request 图像识别请求，包含 Base64 编码的图片数据
     * @return 图像识别响应，包含识别的食材信息
     * @throws ImageRecognitionException 识别失败时抛出
     */
    suspend fun recognizeFood(request: ImageRecognitionRequest): ImageRecognitionResponse
}

/**
 * 图像识别异常
 */
class ImageRecognitionException(message: String, cause: Throwable? = null) : Exception(message, cause)