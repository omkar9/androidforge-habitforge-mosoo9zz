package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.DateUtils
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.model.StreakInfo
import com.androidforge.habitforge.domain.repository.HabitRepository
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<StreakInfo> {
        return when (val completionsResult = repository.getHabitCompletionsForHabit(habitId)) {
            is Result.Success -> {
                val allCompletions = completionsResult.data
                val today = DateUtils.today()

                // Group completions by date for easier lookup
                val completionMap = allCompletions.associateBy { it.completionDate }

                // Calculate current streak
                var currentStreak = 0
                var dayToCheck = today
                
                // Iterate backwards from today to find current streak
                while (true) {
                    val completion = completionMap[dayToCheck]
                    if (completion?.type == CompletionType.COMPLETED) {
                        currentStreak++
                    } else {
                        // If today is not completed (missed or skipped), current streak is 0.
                        // If a previous day was not completed (missed or skipped), streak breaks.
                        break
                    }
                    dayToCheck = dayToCheck.minusDays(1)
                    if (dayToCheck.isBefore(LocalDate.ofEpochDay(0))) break // Safety break
                }

                // Calculate longest streak
                var longestStreak = 0
                var tempStreak = 0
                val sortedCompletedDates = allCompletions
                    .filter { it.type == CompletionType.COMPLETED }
                    .map { it.completionDate }
                    .sorted()

                if (sortedCompletedDates.isNotEmpty()) {
                    tempStreak = 1
                    longestStreak = 1 // Initialize longest streak to 1 if there's at least one completion

                    for (i in 1 until sortedCompletedDates.size) {
                        val daysBetween = DateUtils.getDaysBetween(sortedCompletedDates[i - 1], sortedCompletedDates[i])
                        if (daysBetween == 1L) {
                            tempStreak++
                        } else if (daysBetween > 1L) {
                            longestStreak = maxOf(longestStreak, tempStreak)
                            tempStreak = 1
                        }
                        // If daysBetween is 0 (duplicate entry for same day), ignore and continue streak, tempStreak remains same
                    }
                    longestStreak = maxOf(longestStreak, tempStreak) // Final check after loop
                }

                Result.Success(StreakInfo(currentStreak, longestStreak))
            }
            is Result.Error -> completionsResult
            is Result.Offline -> completionsResult
            else -> Result.Error(message = "Could not retrieve habit completions for streak calculation")
        }
    }
}