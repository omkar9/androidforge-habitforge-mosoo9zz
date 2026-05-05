package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHabitCompletionStatusForDateUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Long, date: LocalDate): Result<Boolean> {
        return when (val result = repository.getHabitCompletionForDate(habitId, date)) {
            is Result.Success -> Result.Success(result.data?.type == CompletionType.COMPLETED)
            is Result.Error -> Result.Error(result.exception, result.message)
            is Result.Offline -> Result.Offline
            else -> Result.Success(false)
        }
    }
}