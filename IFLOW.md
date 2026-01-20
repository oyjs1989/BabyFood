# BabyFood 项目文档

## 项目概述

BabyFood 是一个使用 Kotlin 开发的 Android 应用程序。项目目前处于初始开发阶段，使用现代化的 Android 开发技术栈。

- **项目名称**：BabyFood
- **项目类型**：Android 应用
- **包名**：com.example.babyfood
- **当前版本**：1.0 (versionCode: 1)
- **开发语言**：Kotlin

## 技术栈

### 核心技术
- **语言**：Kotlin 2.0.21
- **构建工具**：Gradle 9.3.0 + Kotlin DSL
- **Android Gradle Plugin**：8.9.1

### Android SDK 版本
- **minSdk**：24 (Android 7.0)
- **targetSdk**：34 (Android 14)
- **compileSdk**：34

### 主要依赖库
- `androidx.core:core-ktx:1.12.0` - AndroidX Core KTX 扩展
- `androidx.appcompat:appcompat:1.6.1` - AppCompat 兼容库
- `com.google.android.material:material:1.11.0` - Material Design 组件
- `androidx.constraintlayout:constraintlayout:2.1.4` - ConstraintLayout 布局

### 测试框架
- `junit:junit:4.13.2` - 单元测试
- `androidx.test.ext:junit:1.1.5` - Android JUnit 扩展
- `androidx.test.espresso:espresso-core:3.5.1` - UI 测试

## 项目结构

```
BabyFood/
├── app/                           # 应用模块
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/babyfood/
│   │       │   └── MainActivity.kt # 主 Activity
│   │       ├── res/                # 资源文件
│   │       │   ├── drawable/       # 图片资源
│   │       │   ├── layout/         # 布局文件
│   │       │   │   └── activity_main.xml
│   │       │   ├── mipmap-*/       # 应用图标
│   │       │   ├── values/         # 值资源
│   │       │   │   └── strings.xml
│   │       │   └── xml/            # XML 配置
│   │       └── AndroidManifest.xml # 应用清单
│   ├── build.gradle.kts            # 应用模块构建配置
│   └── build/                      # 构建输出目录
├── gradle/                         # Gradle 包装器
│   └── wrapper/
├── build.gradle.kts                # 项目级构建配置
├── settings.gradle.kts             # Gradle 设置
├── gradle.properties               # Gradle 属性配置
├── gradlew                         # Unix/Linux/Mac 构建脚本
├── gradlew.bat                     # Windows 构建脚本
└── IFLOW.md                        # 项目文档

```

## 构建和运行

### 前置要求
- JDK 17 或更高版本
- Android SDK (API 34)
- Android Studio (推荐) 或命令行工具

### 构建命令

#### Windows 系统
```bash
# 清理构建产物
gradlew.bat clean

# 构建项目（Debug 版本）
gradlew.bat assembleDebug

# 构建项目（Release 版本）
gradlew.bat assembleRelease

# 运行单元测试
gradlew.bat test

# 运行 Android 设备测试
gradlew.bat connectedAndroidTest

# 安装 Debug 版本到连接的设备
gradlew.bat installDebug

# 卸载应用
gradlew.bat uninstallDebug
```

#### Unix/Linux/macOS 系统
```bash
# 清理构建产物
./gradlew clean

# 构建项目（Debug 版本）
./gradlew assembleDebug

# 构建项目（Release 版本）
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行 Android 设备测试
./gradlew connectedAndroidTest

# 安装 Debug 版本到连接的设备
./gradlew installDebug

# 卸载应用
./gradlew uninstallDebug
```

### 构建输出
- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`
- Release APK：`app/build/outputs/apk/release/app-release.apk`

## 开发约定

### 代码风格
- 使用 Kotlin 官方代码风格（`kotlin.code.style=official`）
- 遵循 Android 官方代码规范
- 使用有意义的变量和方法命名

### 架构约定
- 项目采用单模块结构
- 使用 AndroidX 库替代旧的 Support 库
- 使用 Material Design 组件构建 UI

### 依赖管理
- 使用阿里云 Maven 镜像加速依赖下载
- 优先使用稳定版本的依赖库
- 定期更新依赖版本

### 配置说明
- **AndroidX**：已启用（`android.useAndroidX=true`）
- **非传递性 R 类**：已启用（`android.nonTransitiveRClass=true`）
- **JVM 目标**：Java 17
- **Gradle 配置缓存**：已禁用（`org.gradle.configuration-cache=false`）

## 当前项目状态

项目处于初始开发阶段，目前包含：
- 一个主 Activity (`MainActivity.kt`)
- 一个主布局文件 (`activity_main.xml`)
- 基础的应用图标和资源
- 完整的构建配置

主 Activity 显示 "Hello Baby Food!" 文本作为项目的起点。

## 开发环境配置

### Maven 仓库镜像
项目使用阿里云 Maven 镜像加速依赖下载：
- `https://maven.aliyun.com/repository/google`
- `https://maven.aliyun.com/repository/public`
- `https://maven.aliyun.com/repository/gradle-plugin`

### Gradle 配置
- JVM 参数：`-Xmx2048m -Dfile.encoding=UTF-8`
- 并行构建：未启用（可根据需要启用）

## 常见问题

### 构建失败
- 确保 JDK 17 已安装并配置正确
- 检查网络连接，确保能访问 Maven 仓库
- 尝试清理构建：`gradlew.bat clean`

### 依赖下载缓慢
- 项目已配置阿里云镜像，如果仍然缓慢，可检查网络代理设置

### 设备连接问题
- 确保 USB 调试已开启
- 检查 Android 设备是否正确连接
- 使用 `adb devices` 命令验证设备连接

## 下一步开发

建议的开发方向：
1. 扩展应用功能，添加更多 Activity 和 Fragment
2. 实现数据持久化（Room 数据库或 SharedPreferences）
3. 添加网络请求功能（Retrofit + OkHttp）
4. 实现依赖注入（Hilt）
5. 添加单元测试和 UI 测试
6. 实现导航组件（Jetpack Navigation）
7. 添加 ViewModel 和 LiveData 实现 MVVM 架构

## 许可证

请根据项目实际情况添加许可证信息。