package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.DateUtils
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(private val repository: HabitRepository) {
    operator fun invoke(): Flow<Result<List<Habit>>> {
        return repository.getAllHabits().map { result ->
            when (result) {
                is Result.Success -> {
                    val today = DateUtils.today()
                    val habitsWithStatus = result.data.map { habit ->
                        val completionResult = repository.getHabitCompletionForDate(habit.id, today)
                        val isCompletedToday = (completionResult is Result.Success && completionResult.data?.type == CompletionType.COMPLETED)
                        val isSkippedToday = (completionResult is Result.Success && completionResult.data?.type == CompletionType.SKIPPED)

                        val streakResult = CalculateStreakUseCase(repository).invoke(habit.id)
                        val currentStreak = if (streakResult is Result.Success) streakResult.data.currentStreak else 0
                        val longestStreak = if (streakResult is Result.Success) streakResult.data.longestStreak else 0

                        habit.copy(
                            isCompletedToday = isCompletedToday,
                            isSkippedToday = isSkippedToday,
                            currentStreak = currentStreak,
                            longestStreak = longestStreak
                        )
                    }
                    Result.Success(habitsWithStatus)
                }
                else -> result
            }
        }
    }
}