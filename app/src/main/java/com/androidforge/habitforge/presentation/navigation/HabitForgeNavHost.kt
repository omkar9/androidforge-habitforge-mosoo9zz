package com.androidforge.habitforge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androidforge.habitforge.core.util.Constants
import com.androidforge.habitforge.presentation.add_edit_habit.AddEditHabitScreen
import com.androidforge.habitforge.presentation.habitdetail.HabitDetailScreen
import com.androidforge.habitforge.presentation.habitlist.HabitListScreen
import com.androidforge.habitforge.presentation.settings.SettingsScreen

@Composable
fun HabitForgeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HabitList.route,
        modifier = modifier
    ) {
        composable(Screen.HabitList.route) {
            HabitListScreen(
                onNavigateToAddEditHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.createRoute(habitId))
                },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(navArgument(Constants.NAV_ARG_HABIT_ID) { type = NavType.LongType; defaultValue = Constants.INVALID_HABIT_ID })
        ) {
            AddEditHabitScreen(
                onBackClick = { navController.popBackStack() },
                onHabitSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument(Constants.NAV_ARG_HABIT_ID) { type = NavType.LongType })
        ) {
            HabitDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToEditHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.createRoute(habitId))
                },
                onHabitDeleted = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}