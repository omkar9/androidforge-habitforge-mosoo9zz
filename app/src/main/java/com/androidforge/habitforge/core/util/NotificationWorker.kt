package com.androidforge.habitforge.core.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidforge.habitforge.R
import com.androidforge.habitforge.domain.usecase.GetHabitsUseCase
import com.androidforge.habitforge.domain.usecase.GetHabitCompletionStatusForDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.time.LocalDate

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getHabitsUseCase: GetHabitsUseCase,
    private val getHabitCompletionStatusForDateUseCase: GetHabitCompletionStatusForDateUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("NotificationWorker: doWork started.")
        return try {
            val allHabitsResult = getHabitsUseCase().firstOrNull()

            if (allHabitsResult is com.androidforge.habitforge.core.util.Result.Success) {
                val allHabits = allHabitsResult.data
                val today = DateUtils.today()

                val incompleteHabits = allHabits.filter { habit ->
                    val completionStatusResult = getHabitCompletionStatusForDateUseCase(habit.id, today)
                    !(completionStatusResult is com.androidforge.habitforge.core.util.Result.Success && completionStatusResult.data)
                }

                if (incompleteHabits.isNotEmpty()) {
                    val title = appContext.getString(R.string.notification_title_reminder)
                    val message = if (incompleteHabits.size == 1) {
                        appContext.getString(R.string.notification_message_single_habit, incompleteHabits.first().name)
                    } else {
                        appContext.getString(R.string.notification_message_multiple_habits, incompleteHabits.size)
                    }
                    notificationHelper.showDailyReminderNotification(title, message)
                    Timber.d("NotificationWorker: Notification shown for ${incompleteHabits.size} habits.")
                } else {
                    Timber.d("NotificationWorker: No incomplete habits found for reminder.")
                }
            } else if (allHabitsResult is com.androidforge.habitforge.core.util.Result.Error) {
                Timber.e(allHabitsResult.exception, "NotificationWorker: Error getting habits for notification: ${allHabitsResult.message}")
            } else {
                Timber.d("NotificationWorker: No habits found or other state for notification.")
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "NotificationWorker: Error showing notification.")
            Result.failure()
        }
    }
}