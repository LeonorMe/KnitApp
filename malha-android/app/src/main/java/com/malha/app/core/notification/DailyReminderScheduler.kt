package com.malha.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object DailyReminderScheduler {
    private const val REQUEST_CODE = 4101

    fun setReminder(
        context: Context,
        enabled: Boolean,
        regularity: com.malha.app.core.preferences.ReminderRegularity,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = reminderPendingIntent(context)

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val triggerAtMillis = nextTriggerMillis(regularity, daysOfWeek, hour, minute)
        // Use setExactAndAllowWhileIdle if permission granted, otherwise setAndAllowWhileIdle
        // For simplicity and Android 12+ Exact Alarm permission restrictions, we'll use setWindow or basic set
        // to avoid crashing if exact alarm permission isn't granted.
        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }

    private fun reminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(
        regularity: com.malha.app.core.preferences.ReminderRegularity,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int
    ): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()

        if (regularity == com.malha.app.core.preferences.ReminderRegularity.DAILY) {
            if (calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // Weekly
            // If the time has already passed today, we consider it "next day" for calculation purposes
            if (calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            // Find the next available day from daysOfWeek
            val selectedDays = if (daysOfWeek.isEmpty()) setOf(Calendar.SUNDAY) else daysOfWeek
            while (!selectedDays.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return calendar.timeInMillis
    }
}
