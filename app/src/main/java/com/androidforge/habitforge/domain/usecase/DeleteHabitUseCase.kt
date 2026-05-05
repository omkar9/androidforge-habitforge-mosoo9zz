package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<Unit> {
        return repository.deleteHabit(habitId)
    }
}