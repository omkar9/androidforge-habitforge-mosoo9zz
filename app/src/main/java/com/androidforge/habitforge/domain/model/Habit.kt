package com.androidforge.habitforge.domain.model

import java.time.LocalDate

data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isCompletedToday: Boolean = false,
    val isSkippedToday: Boolean = false
)