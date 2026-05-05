package com.androidforge.habitforge.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.UserSettings
import com.androidforge.habitforge.domain.usecase.CancelDailyReminderUseCase
import com.androidforge.habitforge.domain.usecase.GetUserSettingsUseCase
import com.androidforge.habitforge.domain.usecase.ScheduleDailyReminderUseCase
import com.androidforge.habitforge.domain.usecase.UpdateUserSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val scheduleDailyReminderUseCase: ScheduleDailyReminderUseCase,
    private val cancelDailyReminderUseCase: CancelDailyReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    var reminderHour: Int = 8 private set
    var reminderMinute: Int = 0 private set

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            getUserSettingsUseCase().onEach { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = SettingsUiState.Loading
                    is Result.Success -> {
                        val settings = result.data
                        reminderHour = settings.reminderHour
                        reminderMinute = settings.reminderMinute
                        _uiState.value = SettingsUiState.Success(settings)
                    }
                    is Result.Error -> _uiState.value = SettingsUiState.Error(result.message ?: "Failed to load settings")
                    is Result.Offline -> _uiState.value = SettingsUiState.Offline
                    else -> _uiState.value = SettingsUiState.Error("Unknown error loading settings")
                }
            }.launchIn(viewModelScope)
        }
    }

    fun toggleReminders(enable: Boolean) {
        viewModelScope.launch {
            val currentSettings = (_uiState.value as? SettingsUiState.Success)?.settings
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(areRemindersEnabled = enable)
                when (updateUserSettingsUseCase(updatedSettings)) {
                    is Result.Success -> {
                        if (enable) {
                            scheduleDailyReminderUseCase(updatedSettings.reminderHour, updatedSettings.reminderMinute)
                        } else {
                            cancelDailyReminderUseCase()
                        }
                        _uiState.value = SettingsUiState.Success(updatedSettings)
                    }
                    is Result.Error -> _uiState.value = SettingsUiState.Error("Failed to update reminder setting")
                    is Result.Offline -> _uiState.value = SettingsUiState.Offline
                    else -> _uiState.value = SettingsUiState.Error("Unknown error updating reminder setting")
                }
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val currentSettings = (_uiState.value as? SettingsUiState.Success)?.settings
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(reminderHour = hour, reminderMinute = minute)
                when (updateUserSettingsUseCase(updatedSettings)) {
                    is Result.Success -> {
                        if (updatedSettings.areRemindersEnabled) {
                            scheduleDailyReminderUseCase(hour, minute)
                        }
                        _uiState.value = SettingsUiState.Success(updatedSettings)
                    }
                    is Result.Error -> _uiState.value = SettingsUiState.Error("Failed to update reminder time")
                    is Result.Offline -> _uiState.value = SettingsUiState.Offline
                    else -> _uiState.value = SettingsUiState.Error("Unknown error updating reminder time")
                }
            }
        }
    }
}