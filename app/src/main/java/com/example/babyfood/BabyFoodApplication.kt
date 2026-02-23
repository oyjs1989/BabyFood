package com.example.babyfood

import android.app.Application
import com.example.babyfood.data.init.DataMigration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BabyFoodApplication : Application() {
    
    @Inject
    lateinit var dataMigration: DataMigration
    
    override fun onCreate() {
        super.onCreate()

        // 执行数据迁移（将历史体检记录中的生长数据同步到生长记录表）
        dataMigration.migrateHealthRecordsToGrowthRecords { result ->
            when (result) {
                is com.example.babyfood.data.init.MigrationResult.Success -> {
                    android.util.Log.i("DataMigration", "成功迁移 ${result.count} 条生长记录")
                }
                is com.example.babyfood.data.init.MigrationResult.AlreadyMigrated -> {
                    android.util.Log.i("DataMigration", "生长记录已存在，跳过迁移")
                }
                is com.example.babyfood.data.init.MigrationResult.Error -> {
                    android.util.Log.e("DataMigration", "迁移失败: ${result.message}", result.cause)
                }
            }
        }
    }
}