package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 图像识别请求模型
 * 用于 AI 图像识别服务的请求数据
 */
@Serializable
data class ImageRecognitionRequest(
    /**
     * Base64 编码的图片数据
     * 图片格式应与 imageFormat 一致
     */
    val imageBase64: String,

    /**
     * 图片格式
     * 支持：jpeg, png, webp
     * 默认：jpeg
     */
    val imageFormat: String = "jpeg"
)