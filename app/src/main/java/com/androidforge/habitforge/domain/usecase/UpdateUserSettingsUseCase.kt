package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.UserSettings
import com.androidforge.habitforge.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateUserSettingsUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(settings: UserSettings): Result<Unit> {
        return repository.updateUserSettings(settings)
    }
}