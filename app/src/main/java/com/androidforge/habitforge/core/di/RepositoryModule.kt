package com.androidforge.habitforge.core.di

import com.androidforge.habitforge.data.repository.HabitRepositoryImpl
import com.androidforge.habitforge.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(habitRepositoryImpl: HabitRepositoryImpl): HabitRepository
}