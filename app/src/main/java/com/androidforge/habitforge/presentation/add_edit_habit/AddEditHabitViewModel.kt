package com.androidforge.habitforge.presentation.add_edit_habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.habitforge.core.util.Constants
import com.androidforge.habitforge.core.util.Result
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.domain.usecase.AddHabitUseCase
import com.androidforge.habitforge.domain.usecase.GetHabitByIdUseCase
import com.androidforge.habitforge.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val getHabitByIdUseCase: GetHabitByIdUseCase
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>(Constants.NAV_ARG_HABIT_ID) ?: Constants.INVALID_HABIT_ID
    val isEditing: Boolean = habitId != Constants.INVALID_HABIT_ID

    private val _uiState = MutableStateFlow<AddEditHabitUiState>(AddEditHabitUiState.Loading)
    val uiState: StateFlow<AddEditHabitUiState> = _uiState.asStateFlow()

    var currentHabitName: String = "" private set
    var currentHabitDescription: String = "" private set

    private var originalHabit: Habit? = null

    init {
        if (isEditing) {
            loadHabit()
        } else {
            _uiState.value = AddEditHabitUiState.Success("", "")
        }
    }

    fun loadHabit() {
        viewModelScope.launch {
            _uiState.value = AddEditHabitUiState.Loading
            when (val result = getHabitByIdUseCase(habitId)) {
                is Result.Success -> {
                    originalHabit = result.data
                    currentHabitName = result.data.name
                    currentHabitDescription = result.data.description
                    _uiState.value = AddEditHabitUiState.Success(currentHabitName, currentHabitDescription)
                }
                is Result.Error -> _uiState.value = AddEditHabitUiState.Error(result.message ?: "Failed to load habit")
                is Result.Offline -> _uiState.value = AddEditHabitUiState.Offline
                else -> _uiState.value = AddEditHabitUiState.Error("Habit not found")
            }
        }
    }

    fun onNameChange(name: String) {
        currentHabitName = name
        _uiState.value = AddEditHabitUiState.Success(currentHabitName, currentHabitDescription)
    }

    fun onDescriptionChange(description: String) {
        currentHabitDescription = description
        _uiState.value = AddEditHabitUiState.Success(currentHabitName, currentHabitDescription)
    }

    fun saveHabit() {
        if (currentHabitName.isBlank()) {
            _uiState.value = AddEditHabitUiState.Error("Habit name cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddEditHabitUiState.Saving
            val habitToSave = originalHabit?.copy(
                name = currentHabitName,
                description = currentHabitDescription
            ) ?: Habit(
                name = currentHabitName,
                description = currentHabitDescription,
                createdAt = System.currentTimeMillis()
            )

            val result = if (isEditing) {
                updateHabitUseCase(habitToSave)
            } else {
                addHabitUseCase(habitToSave)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.value = AddEditHabitUiState.Saved(result.data, AddEditHabitUiState.HabitInputState(currentHabitName, currentHabitDescription))
                }
                is Result.Error -> _uiState.value = AddEditHabitUiState.Error(result.message ?: "Failed to save habit")
                is Result.Offline -> _uiState.value = AddEditHabitUiState.Offline
                else -> _uiState.value = AddEditHabitUiState.Error("Unknown error saving habit")
            }
        }
    }
}