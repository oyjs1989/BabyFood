package com.example.babyfood.util

import java.util.regex.Pattern

/**
 * 验证工具类
 * 统一处理项目中所有输入验证逻辑
 */
object ValidationUtils {

    // ==================== 常量定义 ====================

    /** 手机号正则表达式（中国大陆） */
    private const val PHONE_REGEX = "^1[3-9]\\d{9}$"

    /** 邮箱正则表达式 */
    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

    /** 密码最小长度 */
    const val MIN_PASSWORD_LENGTH = 6

    /** 密码最大字节数（ bcrypt 限制） */
    const val MAX_PASSWORD_BYTES = 72

    /** 验证码长度 */
    const val VERIFICATION_CODE_LENGTH = 6

    /** 手机号长度 */
    const val PHONE_LENGTH = 11

    /** 最小用户名长度 */
    const val MIN_NICKNAME_LENGTH = 2

    /** 最大用户名长度 */
    const val MAX_NICKNAME_LENGTH = 20

    /** 宝宝最小月龄 */
    const val MIN_BABY_AGE_MONTHS = 0

    /** 宝宝最大月龄 */
    const val MAX_BABY_AGE_MONTHS = 36

    // ==================== 验证结果 ====================

    /**
     * 验证结果密封类
     */
    sealed class ValidationResult {
        /** 验证成功 */
        data object Success : ValidationResult()

        /** 验证失败 */
        data class Error(val message: String) : ValidationResult()

        /** 判断是否成功 */
        fun isSuccess(): Boolean = this is Success

        /** 判断是否失败 */
        fun isError(): Boolean = this is Error

        /** 获取错误信息，成功时返回 null */
        fun errorMessage(): String? = (this as? Error)?.message
    }

    // ==================== 手机号验证 ====================

    /**
     * 验证手机号格式
     */
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult.Error("手机号不能为空")
            phone.length != PHONE_LENGTH -> ValidationResult.Error("手机号长度应为 $PHONE_LENGTH 位")
            !PHONE_REGEX.toRegex().matches(phone) -> ValidationResult.Error("手机号格式不正确")
            else -> ValidationResult.Success
        }
    }

    /**
     * 检查是否为有效的手机号格式（仅格式检查，不返回错误信息）
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.length == PHONE_LENGTH && PHONE_REGEX.toRegex().matches(phone)
    }

    // ==================== 邮箱验证 ====================

    /**
     * 验证邮箱格式
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("邮箱不能为空")
            email.length > 254 -> ValidationResult.Error("邮箱长度不能超过 254 个字符")
            !EMAIL_REGEX.toRegex().matches(email) -> ValidationResult.Error("邮箱格式不正确")
            else -> ValidationResult.Success
        }
    }

    /**
     * 检查是否为有效的邮箱格式
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() &&
               email.length <= 254 &&
               EMAIL_REGEX.toRegex().matches(email)
    }

    // ==================== 密码验证 ====================

    /**
     * 验证密码强度
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("密码不能为空")
            password.length < MIN_PASSWORD_LENGTH ->
                ValidationResult.Error("密码长度不能少于 $MIN_PASSWORD_LENGTH 位")
            password.toByteArray().size > MAX_PASSWORD_BYTES ->
                ValidationResult.Error("密码过长")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证密码是否匹配
     */
    fun validatePasswordMatch(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult.Error("请确认密码")
            password != confirmPassword -> ValidationResult.Error("两次输入的密码不一致")
            else -> ValidationResult.Success
        }
    }

    // ==================== 验证码验证 ====================

    /**
     * 验证验证码格式
     */
    fun validateVerificationCode(code: String): ValidationResult {
        return when {
            code.isBlank() -> ValidationResult.Error("验证码不能为空")
            code.length != VERIFICATION_CODE_LENGTH ->
                ValidationResult.Error("验证码应为 $VERIFICATION_CODE_LENGTH 位数字")
            !code.all { it.isDigit() } -> ValidationResult.Error("验证码只能包含数字")
            else -> ValidationResult.Success
        }
    }

    // ==================== 用户名/昵称验证 ====================

    /**
     * 验证昵称格式
     */
    fun validateNickname(nickname: String): ValidationResult {
        return when {
            nickname.isBlank() -> ValidationResult.Error("昵称不能为空")
            nickname.length < MIN_NICKNAME_LENGTH ->
                ValidationResult.Error("昵称长度不能少于 $MIN_NICKNAME_LENGTH 个字符")
            nickname.length > MAX_NICKNAME_LENGTH ->
                ValidationResult.Error("昵称长度不能超过 $MAX_NICKNAME_LENGTH 个字符")
            else -> ValidationResult.Success
        }
    }

    // ==================== 宝宝信息验证 ====================

    /**
     * 验证宝宝姓名
     */
    fun validateBabyName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("宝宝姓名不能为空")
            name.length < 1 -> ValidationResult.Error("宝宝姓名太短")
            name.length > 20 -> ValidationResult.Error("宝宝姓名不能超过 20 个字符")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证宝宝月龄
     */
    fun validateBabyAge(ageInMonths: Int): ValidationResult {
        return when {
            ageInMonths < MIN_BABY_AGE_MONTHS ->
                ValidationResult.Error("月龄不能为负数")
            ageInMonths > MAX_BABY_AGE_MONTHS ->
                ValidationResult.Error("月龄不能超过 $MAX_BABY_AGE_MONTHS 个月")
            else -> ValidationResult.Success
        }
    }

    // ==================== 通用验证 ====================

    /**
     * 验证非空
     */
    fun validateNotEmpty(value: String, fieldName: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Error("$fieldName 不能为空")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * 验证长度范围
     */
    fun validateLength(
        value: String,
        fieldName: String,
        minLength: Int,
        maxLength: Int
    ): ValidationResult {
        return when {
            value.length < minLength ->
                ValidationResult.Error("$fieldName 长度不能少于 $minLength 个字符")
            value.length > maxLength ->
                ValidationResult.Error("$fieldName 长度不能超过 $maxLength 个字符")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证数值范围
     */
    fun validateRange(
        value: Number,
        fieldName: String,
        min: Number,
        max: Number
    ): ValidationResult {
        val doubleValue = value.toDouble()
        return when {
            doubleValue < min.toDouble() ->
                ValidationResult.Error("$fieldName 不能小于 $min")
            doubleValue > max.toDouble() ->
                ValidationResult.Error("$fieldName 不能大于 $max")
            else -> ValidationResult.Success
        }
    }

    // ==================== 组合验证 ====================

    /**
     * 验证登录输入（手机号/邮箱 + 密码）
     */
    fun validateLoginInput(account: String, password: String): ValidationResult {
        // 判断是手机号还是邮箱
        val accountValidation = if (account.contains("@")) {
            validateEmail(account)
        } else {
            validatePhone(account)
        }

        if (accountValidation.isError()) {
            return accountValidation
        }

        return validatePassword(password)
    }

    /**
     * 验证注册输入
     */
    fun validateRegisterInput(
        account: String,
        password: String,
        confirmPassword: String,
        verificationCode: String
    ): ValidationResult {
        // 验证账号
        val accountValidation = if (account.contains("@")) {
            validateEmail(account)
        } else {
            validatePhone(account)
        }
        if (accountValidation.isError()) return accountValidation

        // 验证密码
        val passwordValidation = validatePassword(password)
        if (passwordValidation.isError()) return passwordValidation

        // 验证密码匹配
        val matchValidation = validatePasswordMatch(password, confirmPassword)
        if (matchValidation.isError()) return matchValidation

        // 验证验证码
        return validateVerificationCode(verificationCode)
    }
}

// ==================== 扩展函数 ====================

/**
 * 字符串验证扩展：验证是否为有效手机号
 */
fun String.isValidPhone(): Boolean = ValidationUtils.isValidPhone(this)

/**
 * 字符串验证扩展：验证是否为有效邮箱
 */
fun String.isValidEmail(): Boolean = ValidationUtils.isValidEmail(this)

/**
 * 字符串验证扩展：验证手机号并返回结果
 */
fun String.validateAsPhone(): ValidationUtils.ValidationResult = ValidationUtils.validatePhone(this)

/**
 * 字符串验证扩展：验证邮箱并返回结果
 */
fun String.validateAsEmail(): ValidationUtils.ValidationResult = ValidationUtils.validateEmail(this)

/**
 * 字符串验证扩展：验证密码并返回结果
 */
fun String.validateAsPassword(): ValidationUtils.ValidationResult = ValidationUtils.validatePassword(this)
