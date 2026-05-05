package com.androidforge.habitforge.presentation.add_edit_habit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.habitforge.R
import com.androidforge.habitforge.presentation.components.AppToolbar
import com.androidforge.habitforge.presentation.components.ErrorDialog
import com.androidforge.habitforge.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    viewModel: AddEditHabitViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onHabitSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddEditHabitUiState.Saved -> onHabitSaved()
            is AddEditHabitUiState.Error -> {
                val message = (uiState as AddEditHabitUiState.Error).message
                snackbarHostState.showSnackbar(message)
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppToolbar(
                title = if (viewModel.isEditing) stringResource(R.string.edit_habit_title) else stringResource(R.string.add_habit_title),
                showBackIcon = true,
                onBackClick = onBackClick,
                actions = {},
                onSettingsClick = null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is AddEditHabitUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.loading_habit_details)
                    )
                }
                is AddEditHabitUiState.Success, is AddEditHabitUiState.Saving, is AddEditHabitUiState.Saved -> {
                    val currentHabitState = uiState as? AddEditHabitUiState.HabitInputState
                        ?: (uiState as? AddEditHabitUiState.Saving)?.habitInputState
                        ?: (uiState as? AddEditHabitUiState.Saved)?.habitInputState
                        ?: AddEditHabitUiState.Success(viewModel.currentHabitName, viewModel.currentHabitDescription)

                    OutlinedTextField(
                        value = currentHabitState.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text(stringResource(R.string.habit_name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = currentHabitState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text(stringResource(R.string.habit_description_label)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = uiState is AddEditHabitUiState.Saving,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    AppToolbar.AppButton(
                        onClick = viewModel::saveHabit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is AddEditHabitUiState.Saving && currentHabitState.name.isNotBlank()
                    ) {
                        Text(stringResource(R.string.button_save_habit))
                    }
                }
                is AddEditHabitUiState.Error -> {
                    ErrorDialog(
                        message = (uiState as AddEditHabitUiState.Error).message,
                        onDismiss = onBackClick,
                        onRetry = viewModel::loadHabit
                    )
                }
                is AddEditHabitUiState.Offline -> {
                    ErrorDialog(
                        message = stringResource(R.string.offline_message),
                        onDismiss = onBackClick,
                        onRetry = viewModel::loadHabit
                    )
                }
            }
        }
    }
}