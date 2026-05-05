package com.androidforge.habitforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Long = 1, // Enforce singleton settings
    val areRemindersEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)