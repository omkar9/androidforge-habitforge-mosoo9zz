package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.UserSettings
import com.androidforge.habitforge.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(private val repository: HabitRepository) {
    operator fun invoke(): Flow<Result<UserSettings>> {
        return repository.getUserSettings()
    }
}