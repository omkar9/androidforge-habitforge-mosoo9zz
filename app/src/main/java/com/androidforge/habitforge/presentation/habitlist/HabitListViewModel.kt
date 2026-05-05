package com.androidforge.habitforge.presentation.habitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.habitforge.R
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.usecase.GetHabitsUseCase
import com.androidforge.habitforge.domain.usecase.MarkHabitCompletedUseCase
import com.androidforge.habitforge.domain.usecase.MarkHabitSkippedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val markHabitCompletedUseCase: MarkHabitCompletedUseCase,
    private val markHabitSkippedUseCase: MarkHabitSkippedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitListUiState>(HabitListUiState.Loading)
    val uiState: StateFlow<HabitListUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().onEach { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = HabitListUiState.Loading
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            _uiState.value = HabitListUiState.Empty
                        } else {
                            _uiState.value = HabitListUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> _uiState.value = HabitListUiState.Error(result.message ?: "An unexpected error occurred")
                    is Result.Empty -> _uiState.value = HabitListUiState.Empty // Should be handled by Success empty check
                    is Result.Offline -> _uiState.value = HabitListUiState.Offline
                }
            }.launchIn(viewModelScope)
        }
    }

    fun markHabitCompleted(habit: Habit) {
        viewModelScope.launch {
            markHabitCompletedUseCase(habit.id)
            // No need to reload, the flow from getHabitsUseCase should update automatically
        }
    }

    fun markHabitSkipped(habit: Habit) {
        viewModelScope.launch {
            markHabitSkippedUseCase(habit.id)
            // No need to reload, the flow from getHabitsUseCase should update automatically
        }
    }
}