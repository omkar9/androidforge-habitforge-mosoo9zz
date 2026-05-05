package com.androidforge.habitforge.data.local.mapper

import com.androidforge.habitforge.data.local.entity.HabitEntity
import com.androidforge.habitforge.domain.model.Habit

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        // Streak and completion status are calculated in domain layer, not stored directly in entity
        currentStreak = 0,
        longestStreak = 0,
        isCompletedToday = false,
        isSkippedToday = false
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt
    )
}