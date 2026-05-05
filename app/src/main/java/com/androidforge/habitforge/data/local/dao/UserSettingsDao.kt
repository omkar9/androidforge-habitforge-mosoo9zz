package com.androidforge.habitforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidforge.habitforge.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettingsEntity)

    @Update
    suspend fun updateUserSettings(settings: UserSettingsEntity)

    // Helper to ensure default settings exist
    @Query("SELECT COUNT(*) FROM user_settings")
    suspend fun countSettings(): Int
}