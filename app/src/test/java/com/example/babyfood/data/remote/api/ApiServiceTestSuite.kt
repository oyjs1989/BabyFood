package com.example.babyfood.data.remote.api

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * API 服务测试套件
 * 运行所有 API 服务的集成测试
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    NutritionApiServiceTest::class,
    ImageAnalysisApiServiceTest::class,
    IngredientTrialsApiServiceTest::class,
    HealthRecordsApiServiceTest::class,
    GrowthRecordsApiServiceTest::class,
    InventoryApiServiceTest::class
)
class ApiServiceTestSuite {
    // 此类仅用于组织和运行所有 API 服务测试
    companion object {
        const val TOTAL_TEST_CLASSES = 6
        const val EXPECTED_TESTS_PER_CLASS = 10
    }
}
