package com.example.babyfood.util

import android.util.Log

/**
 * 统一的日志工具类
 * 根据项目规范，所有日志必须使用类名作为 TAG
 * 日志格式：========== 方法名 开始/结束 ==========
 */
object Logger {

    /**
     * 打印调试日志
     */
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    /**
     * 打印信息日志
     */
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    /**
     * 打印警告日志
     */
    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    /**
     * 打印警告日志（带异常）
     */
    fun w(tag: String, message: String, throwable: Throwable) {
        Log.w(tag, message, throwable)
    }

    /**
     * 打印错误日志
     */
    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    /**
     * 打印错误日志（带异常）
     */
    fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    /**
     * 打印方法开始日志
     * 格式：========== 方法名 开始 ==========
     */
    fun logMethodStart(tag: String, methodName: String) {
        Log.d(tag, "========== $methodName 开始 ==========")
    }

    /**
     * 打印方法结束日志
     * 格式：========== 方法名 完成 ==========
     */
    fun logMethodEnd(tag: String, methodName: String) {
        Log.d(tag, "========== $methodName 完成 ==========")
    }

    /**
     * 打印操作成功日志
     * 格式：✓ 操作描述
     */
    fun logSuccess(tag: String, message: String) {
        Log.d(tag, "✓ $message")
    }

    /**
     * 打印操作失败日志
     * 格式：❌ 操作描述: 错误信息
     */
    fun logError(tag: String, message: String, error: Throwable? = null) {
        Log.e(tag, "❌ $message", error)
    }

    /**
     * 打印警告信息日志
     * 格式：⚠️ 警告信息
     */
    fun logWarning(tag: String, message: String) {
        Log.w(tag, "⚠️ $message")
    }

    /**
     * 打印带分隔符的日志块
     * 用于包裹一段逻辑的开始和结束
     */
    inline fun <T> logBlock(tag: String, blockName: String, block: () -> T): T {
        logMethodStart(tag, blockName)
        return try {
            val result = block()
            logMethodEnd(tag, blockName)
            result
        } catch (e: Exception) {
            logError(tag, "$blockName 失败", e)
            throw e
        }
    }

    /**
     * 打印带分隔符的挂起函数日志块
     */
    suspend inline fun <T> logBlockSuspend(tag: String, blockName: String, crossinline block: suspend () -> T): T {
        logMethodStart(tag, blockName)
        return try {
            val result = block()
            logMethodEnd(tag, blockName)
            result
        } catch (e: Exception) {
            logError(tag, "$blockName 失败", e)
            throw e
        }
    }
}

/**
 * 为类提供日志 TAG 的接口
 * 实现类只需提供 TAG 常量即可使用扩展函数
 */
interface Loggable {
    val logTag: String

    fun logD(message: String) = Logger.d(logTag, message)
    fun logI(message: String) = Logger.i(logTag, message)
    fun logW(message: String) = Logger.w(logTag, message)
    fun logE(message: String, throwable: Throwable? = null) = Logger.e(logTag, message, throwable)

    fun logMethodStart(methodName: String) = Logger.logMethodStart(logTag, methodName)
    fun logMethodEnd(methodName: String) = Logger.logMethodEnd(logTag, methodName)
    fun logSuccess(message: String) = Logger.logSuccess(logTag, message)
    fun logError(message: String, error: Throwable? = null) = Logger.logError(logTag, message, error)
}
