package com.androidforge.habitforge.presentation.habitlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.habitforge.R
import com.androidforge.habitforge.core.util.Constants
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.presentation.components.AdBannerComposable
import com.androidforge.habitforge.presentation.components.AppToolbar
import com.androidforge.habitforge.presentation.components.ErrorDialog
import com.androidforge.habitforge.presentation.components.HabitCard
import com.androidforge.habitforge.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel = hiltViewModel(),
    onNavigateToAddEditHabit: (Long) -> Unit,
    onNavigateToHabitDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(uiState) {
        if (uiState is HabitListUiState.Error) {
            val errorMessage = (uiState as HabitListUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppToolbar(
                title = stringResource(R.string.habit_list_title),
                showBackIcon = false,
                onSettingsClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEditHabit(Constants.INVALID_HABIT_ID) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_add_new_habit)
                )
            }
        }
    ) {\ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when (uiState) {
                is HabitListUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.loading_habits)
                    )
                }
                is HabitListUiState.Success -> {
                    val habits = (uiState as HabitListUiState.Success).habits
                    HabitListContent(
                        habits = habits,
                        onHabitClick = onNavigateToHabitDetail,
                        onMarkComplete = viewModel::markHabitCompleted,
                        onMarkSkipped = viewModel::markHabitSkipped,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HabitListUiState.Empty -> {
                    EmptyState(
                        message = stringResource(R.string.empty_habits_message),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HabitListUiState.Error -> {
                    ErrorState(
                        message = (uiState as HabitListUiState.Error).message,
                        onRetry = viewModel::loadHabits,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HabitListUiState.Offline -> {
                    OfflineState(
                        message = stringResource(R.string.offline_message),
                        onRetry = viewModel::loadHabits,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            LaunchedEffect(pullToRefreshState.isRefreshing) {
                if (pullToRefreshState.isRefreshing) {
                    viewModel.loadHabits() // Trigger data reload on pull-to-refresh
                }
            }

            LaunchedEffect(uiState) {
                if (uiState !is HabitListUiState.Loading && pullToRefreshState.isRefreshing) {
                    pullToRefreshState.endRefresh()
                }
            }

            // Ad banner at the bottom
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                AdBannerComposable(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitListContent(
    habits: List<Habit>,
    onHabitClick: (Long) -> Unit,
    onMarkComplete: (Habit) -> Unit,
    onMarkSkipped: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(habits, key = { it.id }) {\ habit ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(animationSpec = tween(300)),
                modifier = Modifier.animateItemPlacement(tween(durationMillis = 500))
            ) {
                HabitCard(
                    habit = habit,
                    onHabitClick = { onHabitClick(habit.id) },
                    onMarkComplete = { onMarkComplete(habit) },
                    onMarkSkipped = { onMarkSkipped(habit) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(60.dp)) // Space for the Ad banner
        }
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_state),
            contentDescription = stringResource(R.string.cd_empty_state_illustration),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB and ad
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error_state),
            contentDescription = stringResource(R.string.cd_error_state_illustration),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppToolbar.AppButton(onClick = onRetry) {
            Text(stringResource(R.string.button_retry))
        }
        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB and ad
    }
}

@Composable
private fun OfflineState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_offline_state),
            contentDescription = stringResource(R.string.cd_offline_state_illustration),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.offline_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppToolbar.AppButton(onClick = onRetry) {
            Text(stringResource(R.string.button_retry))
        }
        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB and ad
    }
}