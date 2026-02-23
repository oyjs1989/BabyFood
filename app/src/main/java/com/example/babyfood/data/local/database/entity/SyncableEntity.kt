package com.example.babyfood.data.local.database.entity

/**
 * Interface for entities that support cloud synchronization.
 * All entities that can be synced with the cloud should implement this interface.
 */
interface SyncableEntity {
    val cloudId: String?
    val syncStatus: String
    val lastSyncTime: Long?
    val version: Int
    val isDeleted: Boolean
}