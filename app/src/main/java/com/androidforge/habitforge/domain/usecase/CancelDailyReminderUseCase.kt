package com.androidforge.habitforge.domain.usecase

import com.androidforge.habitforge.core.util.NotificationScheduler
import com.androidforge.habitforge.core.util.Result
import javax.inject.Inject

class CancelDailyReminderUseCase @Inject constructor(private val notificationScheduler: NotificationScheduler) {
    operator fun invoke(): Result<Unit> {
        notificationScheduler.cancelDailyReminder()
        return Result.Success(Unit)
    }
}