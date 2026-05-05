package com.androidforge.habitforge.data.local.mapper

import com.androidforge.habitforge.data.local.entity.UserSettingsEntity
import com.androidforge.habitforge.domain.model.UserSettings

fun UserSettingsEntity.toDomain(): UserSettings {
    return UserSettings(
        id = id,
        areRemindersEnabled = areRemindersEnabled,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}

fun UserSettings.toEntity(): UserSettingsEntity {
    return UserSettingsEntity(
        id = id,
        areRemindersEnabled = areRemindersEnabled,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}