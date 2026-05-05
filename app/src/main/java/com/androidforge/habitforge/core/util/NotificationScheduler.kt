package com.androidforge.habitforge.core.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(private val context: Context) {

    fun scheduleDailyReminder(hour: Int, minute: Int) {
        val now = LocalTime.now()
        val targetTime = LocalTime.of(hour, minute)

        // Calculate initial delay
        var initialDelayMinutes = Duration.between(now, targetTime).toMinutes()
        if (initialDelayMinutes < 0) { // If target time is past today, schedule for tomorrow
            initialDelayMinutes += TimeUnit.DAYS.toMinutes(1)
        }

        val dailyReminderRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 1, // Repeat every 1 day
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .addTag(Constants.HABIT_REMINDER_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.HABIT_REMINDER_WORK_TAG,
            ExistingPeriodicWorkPolicy.UPDATE, // Update existing work if reminder time changes
            dailyReminderRequest
        )
        Timber.d("NotificationScheduler: Scheduled daily reminder for $hour:$minute with initial delay of $initialDelayMinutes minutes.")
    }

    fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.HABIT_REMINDER_WORK_TAG)
        Timber.d("NotificationScheduler: Canceled daily reminder.")
    }
}