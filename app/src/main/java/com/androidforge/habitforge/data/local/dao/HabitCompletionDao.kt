package com.androidforge.habitforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidforge.habitforge.data.local.entity.HabitCompletionEntity
import java.time.LocalDate

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletionEntity)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completionDate = :date")
    suspend fun getHabitCompletionForDate(habitId: Long, date: LocalDate): HabitCompletionEntity?

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completionDate ASC")
    suspend fun getHabitCompletionsForHabit(habitId: Long): List<HabitCompletionEntity>

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND completionDate = :date")
    suspend fun deleteHabitCompletionForDate(habitId: Long, date: LocalDate)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteAllCompletionsForHabit(habitId: Long)
}