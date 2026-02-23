package com.example.babyfood.domain.model

/**
 * 数据同步状态枚举
 * 用于跟踪本地数据与云端数据的同步状态
 */
enum class SyncStatus {
    /**
     * 已同步：本地数据与云端数据一致
     */
    SYNCED,

    /**
     * 待上传：本地数据已更新，等待上传到云端
     */
    PENDING_UPLOAD,

    /**
     * 待下载：云端数据已更新，等待下载到本地
     */
    PENDING_DOWNLOAD,

    /**
     * 同步失败：同步过程中发生错误
     */
    ERROR,

    /**
     * 仅本地：数据仅存储在本地，不上传到云端
     * 用于敏感数据（如健康记录、个人身份信息）
     */
    LOCAL_ONLY
}