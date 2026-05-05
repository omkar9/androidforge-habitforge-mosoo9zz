package com.androidforge.habitforge.data.repository

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.core.util.toAppResult
import com.androidforge.habitforge.data.local.dao.HabitCompletionDao
import com.androidforge.habitforge.data.local.dao.HabitDao
import com.androidforge.habitforge.data.local.dao.UserSettingsDao
import com.androidforge.habitforge.data.local.entity.HabitCompletionEntity
import com.androidforge.habitforge.data.local.entity.HabitEntity
import com.androidforge.habitforge.data.local.entity.UserSettingsEntity
import com.androidforge.habitforge.data.local.mapper.toDomain
import com.androidforge.habitforge.data.local.mapper.toEntity
import com.androidforge.habitforge.domain.model.CompletionType
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.model.HabitCompletion
import com.androidforge.habitforge.domain.model.UserSettings
import com.androidforge.habitforge.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val userSettingsDao: UserSettingsDao
) : HabitRepository {

    override fun getAllHabits(): Flow<Result<List<Habit>>> = flow {
        habitDao.getAllHabits().collect { entities ->
            emit(Result.Success(entities.map { it.toDomain() }))
        }
    }.catch { e ->
        Timber.e(e, "Error getting all habits from DB")
        emit(Result.Error(e, "Failed to load habits"))
    }

    override suspend fun getHabitById(id: Long): Result<Habit> = kotlin.runCatching {
        habitDao.getHabitById(id)?.toDomain() ?: throw Exception("Habit not found")
    }.toAppResult()

    override suspend fun addHabit(habit: Habit): Result<Long> = kotlin.runCatching {
        habitDao.insertHabit(habit.toEntity())
    }.toAppResult()

    override suspend fun updateHabit(habit: Habit): Result<Unit> = kotlin.runCatching {
        habitDao.updateHabit(habit.toEntity())
    }.toAppResult()

    override suspend fun deleteHabit(id: Long): Result<Unit> = kotlin.runCatching {
        habitDao.deleteHabitById(id)
    }.toAppResult()

    override suspend fun markHabitCompletion(habitId: Long, date: LocalDate, type: CompletionType): Result<Unit> = kotlin.runCatching {
        val existingCompletion = habitCompletionDao.getHabitCompletionForDate(habitId, date)
        if (existingCompletion != null) {
            // If existing, update if type is different, or delete if setting to same type (toggle)
            if (existingCompletion.type == type) {
                // If already marked as this type, unmark it (delete)
                habitCompletionDao.deleteHabitCompletionForDate(habitId, date)
            } else {
                // If marked as other type, update to new type
                habitCompletionDao.insertHabitCompletion(existingCompletion.copy(type = type))
            }
        } else {
            // No existing completion, insert new one
            habitCompletionDao.insertHabitCompletion(HabitCompletionEntity(habitId = habitId, completionDate = date, type = type))
        }
    }.toAppResult()

    override suspend fun getHabitCompletionsForHabit(habitId: Long): Result<List<HabitCompletion>> = kotlin.runCatching {
        habitCompletionDao.getHabitCompletionsForHabit(habitId).map { it.toDomain() }
    }.toAppResult()

    override suspend fun getHabitCompletionForDate(habitId: Long, date: LocalDate): Result<HabitCompletion?> = kotlin.runCatching {
        habitCompletionDao.getHabitCompletionForDate(habitId, date)?.toDomain()
    }.toAppResult()

    override suspend fun deleteHabitCompletion(habitId: Long, date: LocalDate): Result<Unit> = kotlin.runCatching {
        habitCompletionDao.deleteHabitCompletionForDate(habitId, date)
    }.toAppResult()

    override fun getUserSettings(): Flow<Result<UserSettings>> = flow {
        if (userSettingsDao.countSettings() == 0) {
            userSettingsDao.insertUserSettings(UserSettingsEntity(id = 1, areRemindersEnabled = false, reminderHour = 8, reminderMinute = 0))
        }
        userSettingsDao.getUserSettings().collect { entity ->
            emit(Result.Success(entity?.toDomain() ?: UserSettings())) // Provide default if somehow null
        }
    }.catch { e ->
        Timber.e(e, "Error getting user settings from DB")
        emit(Result.Error(e, "Failed to load settings"))
    }

    override suspend fun updateUserSettings(settings: UserSettings): Result<Unit> = kotlin.runCatching {
        userSettingsDao.updateUserSettings(settings.toEntity())
    }.toAppResult()
}