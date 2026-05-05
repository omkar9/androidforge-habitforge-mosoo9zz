package com.androidforge.habitforge.core.di

import android.content.Context
import androidx.room.Room
import com.androidforge.habitforge.core.util.Constants
import com.androidforge.habitforge.data.local.AppDatabase
import com.androidforge.habitforge.data.local.dao.HabitCompletionDao
import com.androidforge.habitforge.data.local.dao.HabitDao
import com.androidforge.habitforge.data.local.dao.UserSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(appDatabase: AppDatabase): HabitDao {
        return appDatabase.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitCompletionDao(appDatabase: AppDatabase): HabitCompletionDao {
        return appDatabase.habitCompletionDao()
    }

    @Provides
    @Singleton
    fun provideUserSettingsDao(appDatabase: AppDatabase): UserSettingsDao {
        return appDatabase.userSettingsDao()
    }
}