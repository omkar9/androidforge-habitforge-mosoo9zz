package com.androidforge.habitforge.presentation.navigation

import com.androidforge.habitforge.core.util.Constants

sealed class Screen(val route: String) {
    object HabitList : Screen("habit_list")
    object AddEditHabit : Screen("add_edit_habit/{${Constants.NAV_ARG_HABIT_ID}}") {
        fun createRoute(habitId: Long) = "add_edit_habit/$habitId"
    }
    object HabitDetail : Screen("habit_detail/{${Constants.NAV_ARG_HABIT_ID}}") {
        fun createRoute(habitId: Long) = "habit_detail/$habitId"
    }
    object Settings : Screen("settings")
}