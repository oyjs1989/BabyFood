package com.example.babyfood.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 图片工具类
 * 用于图片压缩、Base64 编码和临时文件创建
 */
object ImageUtils {

    private const val TAG = "ImageUtils"
    
    // 默认图片压缩参数
    private const val DEFAULT_MAX_WIDTH = 1024
    private const val DEFAULT_MAX_HEIGHT = 1024
    private const val DEFAULT_QUALITY = 85
    private val DEFAULT_FORMAT = Bitmap.CompressFormat.JPEG

    /**
     * 压缩图片并转换为 Base64 编码
     *
     * @param context 上下文
     * @param uri 图片 URI
     * @param maxWidth 最大宽度（像素）
     * @param maxHeight 最大高度（像素）
     * @param quality 压缩质量（0-100）
     * @return Base64 编码的图片字符串（不含 MIME 类型前缀）
     */
    suspend fun compressAndEncodeToBase64(
        context: Context,
        uri: Uri,
        maxWidth: Int = DEFAULT_MAX_WIDTH,
        maxHeight: Int = DEFAULT_MAX_HEIGHT,
        quality: Int = DEFAULT_QUALITY
    ): String = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d(TAG, "========== 开始压缩和编码图片 ==========")
            android.util.Log.d(TAG, "URI: $uri")
            android.util.Log.d(TAG, "最大尺寸: ${maxWidth}x${maxHeight}")
            android.util.Log.d(TAG, "质量: $quality")

            // 从 URI 加载图片
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("无法打开图片流")

            // 解码图片
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // 计算采样率
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight
            android.util.Log.d(TAG, "原始尺寸: ${imageWidth}x${imageHeight}")

            val sampleSize = calculateSampleSize(imageWidth, imageHeight, maxWidth, maxHeight)
            android.util.Log.d(TAG, "采样率: $sampleSize")

            // 使用采样率重新解码
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val decodeInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(decodeInputStream, null, decodeOptions)
            decodeInputStream?.close()

            if (bitmap == null) {
                throw IllegalArgumentException("无法解码图片")
            }

            android.util.Log.d(TAG, "解码后尺寸: ${bitmap.width}x${bitmap.height}")

            // 如果尺寸仍然超过限制，进行缩放
            val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                android.util.Log.d(TAG, "需要进一步缩放")
                scaleBitmap(bitmap, maxWidth, maxHeight)
            } else {
                bitmap
            }

            android.util.Log.d(TAG, "最终尺寸: ${scaledBitmap.width}x${scaledBitmap.height}")

            // 压缩为字节数组
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(DEFAULT_FORMAT, quality, outputStream)
            val byteArray = outputStream.toByteArray()

            // 回收位图
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            // Base64 编码
            val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            
            android.util.Log.d(TAG, "Base64 编码完成: ${base64.length} 字符")
            android.util.Log.d(TAG, "图片大小: ${byteArray.size} 字节 (${byteArray.size / 1024} KB)")
            android.util.Log.d(TAG, "========== 压缩和编码完成 ==========")

            base64

        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 压缩和编码失败: ${e.message}")
            throw e
        }
    }

    /**
     * 计算采样率
     */
    private fun calculateSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * 缩放位图
     */
    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 计算缩放比例
        val widthRatio = maxWidth.toFloat() / width
        val heightRatio = maxHeight.toFloat() / height
        val ratio = minOf(widthRatio, heightRatio)

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * 创建临时图片文件用于拍照
     *
     * @param context 上下文
     * @return 临时文件的 content:// URI
     */
    fun createTempImageFile(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        val storageDir = context.getExternalFilesDir("temp_images")
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }

        val tempFile = File(storageDir, fileName)

        android.util.Log.d(TAG, "创建临时文件: ${tempFile.absolutePath}")

        // 使用 FileProvider 生成 content:// URI，避免 FileUriExposedException
        return FileProvider.getUriForFile(
            context,
            "com.example.babyfood.fileprovider",
            tempFile
        )
    }

    /**
     * 删除临时文件
     *
     * @param context 上下文
     * @param uri 文件 URI
     */
    fun deleteTempFile(context: Context, uri: Uri) {
        try {
            val path = uri.path
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    android.util.Log.d(TAG, "删除临时文件: $path")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "删除临时文件失败: ${e.message}")
        }
    }

    /**
     * 清理所有临时文件
     *
     * @param context 上下文
     */
    fun cleanTempFiles(context: Context) {
        try {
            val storageDir = context.getExternalFilesDir("temp_images")
            if (storageDir != null && storageDir.exists()) {
                val files = storageDir.listFiles()
                if (files != null) {
                    for (file in files) {
                        file.delete()
                        android.util.Log.d(TAG, "删除临时文件: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "清理临时文件失败: ${e.message}")
        }
    }
}