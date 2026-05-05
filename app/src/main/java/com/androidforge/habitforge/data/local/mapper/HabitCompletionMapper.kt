package com.androidforge.habitforge.data.local.mapper

import com.androidforge.habitforge.data.local.entity.HabitCompletionEntity
import com.androidforge.habitforge.domain.model.HabitCompletion

fun HabitCompletionEntity.toDomain(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        completionDate = completionDate,
        type = type
    )
}

fun HabitCompletion.toEntity(): HabitCompletionEntity {
    return HabitCompletionEntity(
        id = id,
        habitId = habitId,
        completionDate = completionDate,
        type = type
    )
}