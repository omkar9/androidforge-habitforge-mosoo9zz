package com.androidforge.habitforge.core.util

object Constants {
    const val DATABASE_NAME = "habit_forge_db"
    const val HABIT_TABLE_NAME = "habits"
    const val HABIT_COMPLETION_TABLE_NAME = "habit_completions"
    const val USER_SETTINGS_TABLE_NAME = "user_settings"

    // AdMob Test Unit IDs (from Rule 8)
    const val AD_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val AD_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Notification Channel
    const val NOTIFICATION_CHANNEL_ID = "habit_reminders_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Habit Reminders"
    const val NOTIFICATION_ID = 1001

    // WorkManager
    const val HABIT_REMINDER_WORK_TAG = "habit_reminder_work"

    // Navigation arguments
    const val NAV_ARG_HABIT_ID = "habitId"

    // Default values
    const val INVALID_HABIT_ID = -1L

    // DataStore (not explicitly used for settings, but good to keep if needed for other prefs)
    const val USER_PREFERENCES_NAME = "user_preferences"
}