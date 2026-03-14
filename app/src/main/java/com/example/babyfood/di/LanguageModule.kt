package com.example.babyfood.di

import com.example.babyfood.data.manager.LanguageManagerImpl
import com.example.babyfood.domain.manager.LanguageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LanguageModule {

    @Binds
    @Singleton
    abstract fun bindLanguageManager(
        languageManagerImpl: LanguageManagerImpl
    ): LanguageManager
}
