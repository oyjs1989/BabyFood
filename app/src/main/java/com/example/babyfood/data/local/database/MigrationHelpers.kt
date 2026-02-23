package com.example.babyfood.data.local.database

import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 数据库迁移辅助工具类
 * 提供简化的数据库迁移操作方法
 */
object MigrationHelpers {

    /**
     * 列定义数据类
     */
    data class ColumnDefinition(
        val name: String,
        val type: String,
        val nullable: Boolean = true,
        val defaultValue: String? = null
    )

    /**
     * 添加列到表
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列类型
     * @param nullable 是否可为空
     * @param defaultValue 默认值
     */
    fun SupportSQLiteDatabase.addColumn(
        tableName: String,
        columnName: String,
        columnType: String,
        nullable: Boolean = true,
        defaultValue: String? = null
    ) {
        val nullableClause = if (nullable) "" else " NOT NULL"
        val defaultClause = if (defaultValue != null) " DEFAULT $defaultValue" else ""
        execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnType$nullableClause$defaultClause")
    }

    /**
     * 批量添加列到表
     */
    fun SupportSQLiteDatabase.addColumns(
        tableName: String,
        columns: List<ColumnDefinition>
    ) {
        columns.forEach { column ->
            addColumn(
                tableName = tableName,
                columnName = column.name,
                columnType = column.type,
                nullable = column.nullable,
                defaultValue = column.defaultValue
            )
        }
    }

    /**
     * 创建索引
     * @param indexName 索引名
     * @param tableName 表名
     * @param columns 列名列表
     * @param unique 是否唯一索引
     */
    fun SupportSQLiteDatabase.createIndex(
        indexName: String,
        tableName: String,
        columns: List<String>,
        unique: Boolean = false
    ) {
        val uniqueClause = if (unique) "UNIQUE " else ""
        val columnsClause = columns.joinToString(", ")
        execSQL("CREATE $uniqueClause INDEX IF NOT EXISTS $indexName ON $tableName($columnsClause)")
    }

    /**
     * 创建表
     * @param tableName 表名
     * @param columns 列定义列表
     * @param primaryKey 主键列名（如果为 null，则使用 id）
     * @param autoIncrement 是否自增主键
     */
    fun SupportSQLiteDatabase.createTable(
        tableName: String,
        columns: List<ColumnDefinition>,
        primaryKey: String? = null,
        autoIncrement: Boolean = true
    ) {
        val pkColumn = primaryKey ?: "id"
        val autoIncrementClause = if (autoIncrement) " AUTOINCREMENT" else ""

        val columnDefs = columns.map { column ->
            val nullableClause = if (column.nullable) "" else " NOT NULL"
            val defaultClause = if (column.defaultValue != null) " DEFAULT ${column.defaultValue}" else ""
            val primaryKeyClause = if (column.name == pkColumn) " PRIMARY KEY$autoIncrementClause" else ""
            "${column.name} ${column.type}$nullableClause$defaultClause$primaryKeyClause"
        }.joinToString(",\n    ")

        execSQL("""
            CREATE TABLE IF NOT EXISTS $tableName (
                $columnDefs
            )
        """.trimIndent())
    }

    /**
     * 重建表（用于修改列约束）
     * @param oldTableName 旧表名
     * @param newTableName 新表名（临时）
     * @param columns 新表的列定义
     * @param selectColumns 要迁移的列名（如果不指定，则迁移所有列）
     * @param additionalSelectSQL 额外的 SELECT 子句（如 COALESCE）
     */
    fun SupportSQLiteDatabase.rebuildTable(
        oldTableName: String,
        newTableName: String,
        columns: List<ColumnDefinition>,
        selectColumns: List<String>? = null,
        additionalSelectSQL: Map<String, String> = emptyMap()
    ) {
        // 创建新表
        createTable(newTableName, columns)

        // 迁移数据
        val columnsToSelect = selectColumns ?: columns.map { it.name }
        val selectClause = columnsToSelect.map { columnName ->
            val additionalSQL = additionalSelectSQL[columnName]
            if (additionalSQL != null) {
                "$additionalSQL AS $columnName"
            } else {
                columnName
            }
        }.joinToString(", ")

        execSQL("""
            INSERT INTO $newTableName (${columnsToSelect.joinToString(", ")})
            SELECT $selectClause
            FROM $oldTableName
        """.trimIndent())

        // 删除旧表
        execSQL("DROP TABLE $oldTableName")

        // 重命名新表
        execSQL("ALTER TABLE $newTableName RENAME TO $oldTableName")
    }

    /**
     * 执行安全的 SQL 语句（自动处理字符串转义）
     * @param sql SQL 语句
     * @param args 参数列表
     */
    fun SupportSQLiteDatabase.executeSafeSQL(
        sql: String,
        vararg args: Any
    ) {
        execSQL(sql, args)
    }

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 是否存在
     */
    fun SupportSQLiteDatabase.tableExists(tableName: String): Boolean {
        val cursor = query("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(tableName))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * 检查列是否存在
     * @param tableName 表名
     * @param columnName 列名
     * @return 是否存在
     */
    fun SupportSQLiteDatabase.columnExists(tableName: String, columnName: String): Boolean {
        val cursor = query("PRAGMA table_info($tableName)", emptyArray())
        var exists = false
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1) == columnName) {
                    exists = true
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return exists
    }
}
