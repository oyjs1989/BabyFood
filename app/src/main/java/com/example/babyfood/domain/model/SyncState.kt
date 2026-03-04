package com.example.babyfood.domain.model

/**
 * 同步状态枚举
 */
enum class SyncState {
    /**
     * 已同步
     */
    SYNCED,

    /**
     * 待上传
     */
    PENDING_UPLOAD,

    /**
     * 待下载
     */
    PENDING_DOWNLOAD,

    /**
     * 同步中
     */
    SYNCING,

    /**
     * 同步错误
     */
    ERROR,

    /**
     * 离线
     */
    OFFLINE
}
