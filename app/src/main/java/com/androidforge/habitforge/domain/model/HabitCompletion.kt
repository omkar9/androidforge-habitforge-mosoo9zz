package com.androidforge.habitforge.domain.model

import java.time.LocalDate

data class HabitCompletion(
    val id: Long = 0,
    val habitId: Long,
    val completionDate: LocalDate,
    val type: CompletionType // COMPLETED, SKIPPED
)

enum class CompletionType {
    COMPLETED,
    SKIPPED
}