package com.example.babyfood.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Generic base repository providing common CRUD operations
 *
 * Note: Due to Room's limitations, DAOs cannot extend the BaseDao interface.
 * Concrete repositories should implement their own CRUD operations by calling
 * their DAO methods directly. This base class provides helper methods and
 * utilities to reduce code duplication.
 *
 * @param T Domain model type
 * @param E Entity type (Room database entity)
 * @param ID ID type (typically Long)
 */
abstract class BaseRepository<T, E, ID> {

    /**
     * Convert Entity to Domain model
     */
    protected abstract fun E.toDomainModel(): T

    /**
     * Convert Domain model to Entity
     */
    protected abstract fun T.toEntity(): E

    // ============ Flow Mapping Helpers ============

    /**
     * Extension function to convert Flow<List<Entity>> to Flow<List<DomainModel>>
     * This eliminates the repetitive .map { entities -> entities.map { it.toDomainModel() } } pattern
     */
    protected fun Flow<List<E>>.toDomainModels(): Flow<List<T>> =
        map { entities -> entities.map { it.toDomainModel() } }
}

/**
 * Base DAO interface that all DAOs should implement
 * Defines the common CRUD operations that BaseRepository expects
 *
 * Note: Due to Room's limitations, DAOs cannot actually extend this interface.
 * This interface serves as documentation of the expected CRUD method signatures.
 * All DAOs should implement these methods with consistent naming.
 *
 * @param E Entity type (Room database entity)
 * @param ID ID type (typically Long)
 */
interface BaseDao<E, ID> {
    /**
     * Get a single entity by its ID
     * @param id The entity's ID
     * @return The entity, or null if not found
     */
    suspend fun getById(id: ID): E?

    /**
     * Insert a new entity into the database
     * @param item The entity to insert
     * @return The ID of the inserted entity
     */
    suspend fun insert(item: E): ID

    /**
     * Update an existing entity in the database
     * @param item The entity to update
     */
    suspend fun update(item: E)

    /**
     * Delete an entity from the database
     * @param item The entity to delete
     */
    suspend fun delete(item: E)
}