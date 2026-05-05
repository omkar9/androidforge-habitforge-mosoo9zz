package com.androidforge.habitforge.domain.repository

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.model.HabitCompletion
import com.androidforge.habitforge.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    fun getAllHabits(): Flow<Result<List<Habit>>>
    suspend fun getHabitById(id: Long): Result<Habit>
    suspend fun addHabit(habit: Habit): Result<Long>
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun deleteHabit(id: Long): Result<Unit>

    suspend fun markHabitCompletion(habitId: Long, date: LocalDate, type: CompletionType): Result<Unit>
    suspend fun getHabitCompletionsForHabit(habitId: Long): Result<List<HabitCompletion>>
    suspend fun getHabitCompletionForDate(habitId: Long, date: LocalDate): Result<HabitCompletion?>
    suspend fun deleteHabitCompletion(habitId: Long, date: LocalDate): Result<Unit>

    fun getUserSettings(): Flow<Result<UserSettings>>
    suspend fun updateUserSettings(settings: UserSettings): Result<Unit>
}