package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habit: Habit): Result<Long> {
        return repository.addHabit(habit)
    }
}