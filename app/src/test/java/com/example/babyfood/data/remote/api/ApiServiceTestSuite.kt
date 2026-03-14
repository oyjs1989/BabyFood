package com.example.babyfood.data.remote.api

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * 所有 API 服务集成测试的测试套件
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    GrowthRecordsApiServiceTest::class,
    HealthRecordsApiServiceTest::class,
    InventoryApiServiceTest::class,
    NutritionApiServiceTest::class,
    IngredientTrialsApiServiceTest::class,
    ImageAnalysisApiServiceTest::class
)
class ApiServiceTestSuite
