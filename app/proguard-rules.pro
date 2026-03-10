# 项目包名下的模型和类
-keep class com.example.babyfood.domain.model.** { *; }
-keep class com.example.babyfood.data.remote.dto.** { *; }

# Jetpack Compose
-keepclassmembers class  * extends androidx.compose.runtime.Composer { *; }
-keep class androidx.compose.** { *; }

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class * { @dagger.hilt.android.lifecycle.HiltViewModel *; }

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * { @androidx.room.* *; }

# Kotlin Serialization
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, InnerClasses

# Coil
-keep class coil.** { *; }
