# BabyFood

一个智能的婴儿辅食管理 Android 应用，帮助父母科学管理宝宝的辅食计划、营养摄入和生长记录。

## 项目特性

- **智能辅食推荐**: 基于宝宝年龄、过敏史和营养目标的个性化食谱推荐
- **生长曲线追踪**: 可视化宝宝的生长发育数据（体重、身高）
- **健康记录管理**: 记录宝宝的进食情况、过敏反应等健康信息
- **营养目标计算**: 根据中国营养学会标准自动计算宝宝的营养需求
- **计划管理**: 灵活创建和管理每日辅食计划
- **数据备份**: 本地数据库存储，支持数据迁移

## 技术栈

### 核心技术
- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel)
- **依赖注入**: Hilt
- **数据库**: Room
- **异步处理**: Kotlin Coroutines + Flow
- **导航**: Navigation Compose
- **日期时间**: kotlinx-datetime
- **序列化**: kotlinx-serialization
- **图表**: Vico Charts
- **图片加载**: Coil

### 开发工具
- **构建工具**: Gradle (Kotlin DSL)
- **代码规范**: ktlint + detekt
- **编辑器配置**: EditorConfig
- **最低 SDK**: 24 (Android 7.0)
- **目标 SDK**: 34 (Android 14)

## 项目结构

```
app/src/main/java/com/example/babyfood/
├── BabyFoodApplication.kt          # 应用入口
├── MainActivity.kt                  # 主 Activity
├── data/                            # 数据层
│   ├── init/                       # 数据初始化
│   ├── local/                      # 本地数据
│   │   └── database/
│   │       ├── dao/               # 数据访问对象
│   │       └── entity/            # 实体类
│   └── repository/                # 仓库模式
├── di/                             # 依赖注入
├── domain/                         # 领域层
│   └── model/                      # 领域模型
└── presentation/                   # 表现层
    ├── theme/                      # 主题配置
    └── ui/                         # 用户界面
        ├── home/                   # 首页
        ├── baby/                   # 宝宝管理
        ├── recipes/                # 食谱管理
        ├── plans/                  # 计划管理
        ├── health/                 # 健康记录
        └── growth/                 # 生长曲线
```

## 开始使用

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK API 34

### 构建项目

```bash
# 克隆项目
git clone <repository-url>
cd BabyFood

# 构建项目
./gradlew build

# 安装到设备
./gradlew installDebug
```

### 代码规范检查

```bash
# 运行 ktlint 代码格式检查
./gradlew ktlintCheck

# 自动格式化代码
./gradlew ktlintFormat

# 运行 detekt 代码质量检查
./gradlew detekt
```

## 核心功能模块

### 1. 宝宝管理 (BabyModule)
- 创建和管理多个宝宝档案
- 记录宝宝的基本信息（生日、体重、身高）
- 管理过敏原信息
- 设置饮食偏好

### 2. 食谱管理 (RecipeModule)
- 内置丰富的婴儿辅食食谱
- 支持自定义食谱
- 详细的营养信息（热量、蛋白质、脂肪、碳水化合物等）
- 食材列表和制作步骤

### 3. 辅食计划 (PlanModule)
- 创建每日辅食计划
- 智能推荐适合的食谱
- 灵活的时间段管理
- 计划状态跟踪

### 4. 健康记录 (HealthModule)
- 记录宝宝的进食情况
- 记录过敏反应
- 记录其他健康事件
- 历史记录查询

### 5. 生长曲线 (GrowthModule)
- 记录体重和身高数据
- 可视化生长曲线
- 数据对比分析
- 生长趋势追踪

## 架构设计

### MVVM 架构
项目采用标准的 MVVM 架构，实现了清晰的职责分离：

- **Model**: 负责数据管理和业务逻辑
- **View**: 负责 UI 展示和用户交互
- **ViewModel**: 连接 Model 和 View，处理业务逻辑和状态管理

### 依赖注入
使用 Hilt 进行依赖注入，提高代码的可测试性和可维护性。

### 数据流
采用 Kotlin Flow 实现响应式数据流，确保 UI 与数据的同步更新。

## 数据模型

### 核心实体
- **Baby**: 宝宝信息
- **Recipe**: 食谱信息
- **Plan**: 辅食计划
- **HealthRecord**: 健康记录
- **GrowthRecord**: 生长记录

### 营养计算
基于中国营养学会《中国居民膳食营养素参考摄入量》标准，根据宝宝年龄自动计算营养目标。

## 贡献指南

### 代码规范
- 遵循 Kotlin 官方代码风格指南
- 使用 ktlint 进行代码格式化
- 通过 detekt 代码质量检查
- 编写单元测试和集成测试

### 提交规范
遵循语义化版本控制规范：
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建或工具相关

## 开发计划

- [ ] 添加单元测试
- [ ] 添加 UI 测试
- [ ] 支持多语言
- [ ] 添加云端数据同步
- [ ] 优化性能和用户体验
- [ ] 添加更多食谱数据
- [ ] 社区分享功能

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

---

**注意**: 本项目使用中文进行注释和文档编写，便于团队协作和代码维护。
