package com.androidforge.habitforge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidforge.habitforge.data.local.converter.Converters
import com.androidforge.habitforge.data.local.dao.HabitCompletionDao
import com.androidforge.habitforge.data.local.dao.HabitDao
import com.androidforge.habitforge.data.local.dao.UserSettingsDao
import com.androidforge.habitforge.data.local.entity.HabitCompletionEntity
import com.androidforge.habitforge.data.local.entity.HabitEntity
import com.androidforge.habitforge.data.local.entity.UserSettingsEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun userSettingsDao(): UserSettingsDao
}