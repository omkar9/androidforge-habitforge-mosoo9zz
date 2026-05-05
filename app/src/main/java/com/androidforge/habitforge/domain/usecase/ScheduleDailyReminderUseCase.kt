package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.NotificationScheduler
import com.androidforge.habitforge.core.util.Result
import javax.inject.Inject

class ScheduleDailyReminderUseCase @Inject constructor(private val notificationScheduler: NotificationScheduler) {
    operator fun invoke(hour: Int, minute: Int): Result<Unit> {
        notificationScheduler.scheduleDailyReminder(hour, minute)
        return Result.Success(Unit)
    }
}