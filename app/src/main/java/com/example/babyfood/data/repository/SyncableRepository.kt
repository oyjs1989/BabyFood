package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.entity.SyncableEntity

/**
 * Base repository for entities that support cloud synchronization
 *
 * This repository extends BaseRepository and provides utilities for:
 * - Sync metadata (cloudId, syncStatus, lastSyncTime, version, isDeleted)
 * - Entity preparation for insert/update operations
 *
 * Note: Due to Room's limitations, concrete repositories must implement
 * their own CRUD operations by calling their DAO methods directly.
 * This base class provides helper methods for entity preparation.
 *
 * @param T Domain model type
 * @param E Entity type (must implement SyncableEntity)
 * @param ID ID type (typically Long)
 */
abstract class SyncableRepository<T, E : SyncableEntity, ID> : BaseRepository<T, E, ID>() {

    /**
     * Get the ID from a domain model
     * Each concrete repository must implement this to extract the ID
     */
    protected abstract fun getItemId(item: T): ID

    // Note: CRUD operations (insert, update, delete) should be implemented
    // in concrete repositories using their specific DAO methods.
    // The prepareForInsert and prepareForUpdate helper functions below
    // can be used to prepare entities with sync metadata.
}

/**
 * Enhanced DAO interface for syncable entities
 * Extends BaseDao with batch operations
 *
 * Note: Due to Room's limitations, DAOs cannot actually extend this interface.
 * This interface serves as documentation of the expected method signatures.
 *
 * @param E Entity type (must implement SyncableEntity)
 * @param ID ID type (typically Long)
 */
interface SyncableDao<E : SyncableEntity, ID> : BaseDao<E, ID> {
    /**
     * Insert multiple entities into the database
     * @param items List of entities to insert
     * @return List of IDs of the inserted entities
     */
    suspend fun insertAll(items: List<E>): List<ID>

    /**
     * Update multiple entities in the database
     * @param items List of entities to update
     */
    suspend fun updateAll(items: List<E>)
}