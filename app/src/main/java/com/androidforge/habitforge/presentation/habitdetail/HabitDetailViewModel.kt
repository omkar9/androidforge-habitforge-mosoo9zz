package com.androidforge.habitforge.presentation.habitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.habitforge.core.util.Constants
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.StreakInfo
import com.androidforge.habitforge.domain.usecase.CalculateStreakUseCase
import com.androidforge.habitforge.domain.usecase.DeleteHabitUseCase
import com.androidforge.habitforge.domain.usecase.GetHabitByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>(Constants.NAV_ARG_HABIT_ID) ?: Constants.INVALID_HABIT_ID

    private val _uiState = MutableStateFlow<HabitDetailUiState>(HabitDetailUiState.Loading)
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    init {
        if (habitId != Constants.INVALID_HABIT_ID) {
            loadHabitDetails()
        } else {
            _uiState.value = HabitDetailUiState.NotFound
        }
    }

    fun loadHabitDetails() {
        viewModelScope.launch {
            _uiState.value = HabitDetailUiState.Loading
            when (val habitResult = getHabitByIdUseCase(habitId)) {
                is Result.Success -> {
                    val habit = habitResult.data
                    when (val streakResult = calculateStreakUseCase(habitId)) {
                        is Result.Success -> {
                            _uiState.value = HabitDetailUiState.Success(habit, streakResult.data)
                        }
                        is Result.Error -> _uiState.value = HabitDetailUiState.Error(streakResult.message ?: "Failed to calculate streak")
                        is Result.Offline -> _uiState.value = HabitDetailUiState.Offline
                        else -> _uiState.value = HabitDetailUiState.Error("Unknown error calculating streak")
                    }
                }
                is Result.Error -> _uiState.value = HabitDetailUiState.Error(habitResult.message ?: "Failed to load habit details")
                is Result.Offline -> _uiState.value = HabitDetailUiState.Offline
                is Result.Empty -> _uiState.value = HabitDetailUiState.NotFound
                else -> _uiState.value = HabitDetailUiState.NotFound
            }
        }
    }

    fun deleteHabit() {
        viewModelScope.launch {
            when (deleteHabitUseCase(habitId)) {
                is Result.Success -> _uiState.value = HabitDetailUiState.HabitDeleted
                is Result.Error -> _uiState.value = HabitDetailUiState.Error("Failed to delete habit")
                is Result.Offline -> _uiState.value = HabitDetailUiState.Offline
                else -> _uiState.value = HabitDetailUiState.Error("Unknown error deleting habit")
            }
        }
    }
}