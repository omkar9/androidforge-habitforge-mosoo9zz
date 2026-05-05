package com.androidforge.habitflow.presentation.habits

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.androidforge.habitflow.R
import com.androidforge.habitflow.core.util.DateUtils.toFormattedDate
import com.androidforge.habitflow.presentation.components.BannerAdView
import com.androidforge.habitflow.presentation.components.CustomTopAppBar
import com.androidforge.habitflow.presentation.components.EmptyState
import com.androidforge.habitflow.presentation.components.ErrorState
import com.androidforge.habitflow.presentation.components.HabitItem
import com.androidforge.habitflow.presentation.components.OfflineState
import com.androidforge.habitflow.presentation.components.ShimmerLoadingScreen
import com.androidforge.habitflow.presentation.util.AdmobManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitListScreen(
    onNavigateToAddEditHabit: (String?) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    admobManager: AdmobManager,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    // Observe Lifecycle for AdmobManager
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            admobManager.loadInterstitialAd(context)
        }
    }

    // Handle interstitial ad display
    LaunchedEffect(uiState) {
        if (uiState is HabitListUiState.Success) {
            val successState = uiState as HabitListUiState.Success
            if (successState.showInterstitialAd) {
                admobManager.showInterstitialAd(activity) {
                    viewModel.onInterstitialAdShown()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.app_name),
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        contentDescription = stringResource(R.string.settings_icon_description)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_icon_description),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEditHabit(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_habit_icon_description)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                HabitListUiState.Loading -> {
                    ShimmerLoadingScreen()
                }
                is HabitListUiState.Success -> {
                    if (state.habits.isEmpty()) {
                        EmptyState(
                            title = stringResource(R.string.empty_habits_title),
                            message = stringResource(R.string.empty_habits_description),
                            buttonText = stringResource(R.string.add_your_first_habit),
                            onButtonClick = { onNavigateToAddEditHabit(null) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = state.habits, key = { it.habit.id }) { habitWithStatus ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            scope.launch {
                                                viewModel.deleteHabit(habitWithStatus.habit)
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.habit_deleted)
                                                )
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                    positionalThreshold = { it * 0.5f }
                                )

                                // Reset dismiss state if habit ID changes (e.g., after undo)
                                LaunchedEffect(habitWithStatus.habit.id) {
                                    dismissState.reset()
                                }

                                SwipeToDismissBox(
                                    state = dismissState,
                                    modifier = Modifier.animateItemPlacement(tween(durationMillis = 200)),
                                    directions = setOf(SwipeToDismissBoxValue.EndToStart),
                                    background = {
                                        val color = when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceVariant
                                            DismissValue.DismissedToEnd -> Color.Transparent // Not used
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(color, MaterialTheme.shapes.medium)
                                                .padding(horizontal = 20.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete_habit_description),
                                                tint = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        HabitItem(
                                            habitWithStatus = habitWithStatus,
                                            onToggleCompletion = { habitId, isCompleted ->
                                                viewModel.markHabitCompleted(habitId, isCompleted)
                                            },
                                            onClick = { habitId ->
                                                onNavigateToDetail(habitId)
                                            },
                                            onLongClick = { habitId ->
                                                onNavigateToAddEditHabit(habitId)
                                            }
                                        )
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(60.dp)) // Space for FAB
                            }
                        }
                    }
                }
                is HabitListUiState.Error -> {
                    ErrorState(
                        title = stringResource(R.string.error_loading_habits_title),
                        message = state.message,
                        onRetryClick = viewModel::loadHabits
                    )
                }
                HabitListUiState.Offline -> {
                    OfflineState(onRetryClick = viewModel::loadHabits)
                }
            }
            // AdMob Banner Ad at the bottom
            BannerAdView(modifier = Modifier.fillMaxWidth())
        }
    }
}
