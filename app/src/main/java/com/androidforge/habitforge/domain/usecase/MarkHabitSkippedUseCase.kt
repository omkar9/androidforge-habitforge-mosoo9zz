package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.DateUtils
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class MarkHabitSkippedUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<Unit> {
        val today = DateUtils.today()
        return repository.markHabitCompletion(habitId, today, CompletionType.SKIPPED)
    }
}