package com.androidforge.habitforge.domain.model

data class UserSettings(
    val id: Long = 1, // Singleton settings, Room will enforce this with primary key
    val areRemindersEnabled: Boolean = false,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0
)