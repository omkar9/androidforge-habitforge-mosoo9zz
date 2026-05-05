package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(id: Long): Result<Habit> {
        return repository.getHabitById(id)
    }
}