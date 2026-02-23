package com.example.babyfood.data.remote

import com.example.babyfood.data.remote.api.BabyApiService
import com.example.babyfood.data.remote.api.PlanApiService
import com.example.babyfood.data.remote.api.RecipeApiService
import com.example.babyfood.data.remote.api.SyncApiService
import com.example.babyfood.data.remote.dto.CloudBaby
import com.example.babyfood.data.remote.dto.CloudPlan
import com.example.babyfood.data.remote.dto.CloudRecipe
import com.example.babyfood.data.remote.dto.SyncPullResponse
import com.example.babyfood.data.remote.dto.SyncPushRequest
import com.example.babyfood.data.remote.dto.SyncPushResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 远程数据源实现
 * 基于 Retrofit 的 REST API 调用
 */
@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val recipeApiService: RecipeApiService,
    private val planApiService: PlanApiService,
    private val babyApiService: BabyApiService,
    private val syncApiService: SyncApiService
) : RemoteDataSource {

    // ==================== Recipe 操作 ====================

    override suspend fun getAllRecipes(): List<CloudRecipe> {
        return try {
            recipeApiService.getRecipes()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRecipe(cloudId: String): CloudRecipe? {
        return try {
            recipeApiService.getRecipe(cloudId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createRecipe(recipe: CloudRecipe): CloudRecipe? {
        return try {
            recipeApiService.createRecipe(recipe)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateRecipe(cloudId: String, recipe: CloudRecipe): CloudRecipe? {
        return try {
            recipeApiService.updateRecipe(cloudId, recipe)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteRecipe(cloudId: String): Boolean {
        return try {
            recipeApiService.deleteRecipe(cloudId)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==================== Plan 操作 ====================

    override suspend fun getAllPlans(cloudBabyId: String?): List<CloudPlan> {
        return try {
            planApiService.getPlans(cloudBabyId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPlan(cloudId: String): CloudPlan? {
        return try {
            planApiService.getPlan(cloudId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createPlan(plan: CloudPlan): CloudPlan? {
        return try {
            planApiService.createPlan(plan)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updatePlan(cloudId: String, plan: CloudPlan): CloudPlan? {
        return try {
            planApiService.updatePlan(cloudId, plan)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deletePlan(cloudId: String): Boolean {
        return try {
            planApiService.deletePlan(cloudId)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==================== Baby 操作 ====================

    override suspend fun getAllBabies(): List<CloudBaby> {
        return try {
            babyApiService.getBabies()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBaby(cloudId: String): CloudBaby? {
        return try {
            babyApiService.getBaby(cloudId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createBaby(baby: CloudBaby): CloudBaby? {
        return try {
            babyApiService.createBaby(baby)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateBaby(cloudId: String, baby: CloudBaby): CloudBaby? {
        return try {
            babyApiService.updateBaby(cloudId, baby)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteBaby(cloudId: String): Boolean {
        return try {
            babyApiService.deleteBaby(cloudId)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==================== Sync 操作 ====================

    override suspend fun pull(lastSyncTime: Long?): SyncPullResponse? {
        return try {
            syncApiService.pull(lastSyncTime)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun push(request: SyncPushRequest): SyncPushResponse? {
        return try {
            syncApiService.push(request)
        } catch (e: Exception) {
            null
        }
    }
}