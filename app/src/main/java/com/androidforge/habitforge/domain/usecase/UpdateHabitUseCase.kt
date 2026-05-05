package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habit: Habit): Result<Unit> {
        return repository.updateHabit(habit)
    }
}