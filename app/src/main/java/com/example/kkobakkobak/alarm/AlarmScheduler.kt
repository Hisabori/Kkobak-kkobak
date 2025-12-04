package com.example.kkobakkobak.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kkobakkobak.receiver.AlarmReceiver
import com.example.kkobakkobak.data.model.MedicationReminder
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // MedicationReminder 객체를 받아 알람 스케줄링 (기존 로직)
    fun schedule(reminder: MedicationReminder) {
        val requestCode = reminder.id

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("CATEGORY", reminder.category)
            putExtra("MEDICATION_NAME", reminder.medicationName)
            putExtra("REMINDER_ID", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime.timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Scheduled alarm for ${reminder.category} at ${reminder.hour}:${reminder.minute}, ID: $requestCode")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Failed to schedule alarm due to security exception: ${e.message}")
        }
    }

    // 🔔 [추가] 스누즈 알람 스케줄링 함수
    fun scheduleSnooze(reminder: MedicationReminder, delayMinutes: Int) {
        val requestCode = reminder.id + 1000

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("CATEGORY", reminder.category)
            putExtra("MEDICATION_NAME", reminder.medicationName)
            putExtra("REMINDER_ID", reminder.id)
            putExtra("IS_SNOOZE", true) // 스누즈 알람임을 표시
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, delayMinutes)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                snoozeTime.timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Scheduled snooze alarm for ${reminder.category} in ${delayMinutes} mins, Snooze ID: $requestCode")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Failed to schedule snooze alarm due to security exception: ${e.message}")
        }
    }


    // MedicationReminder 객체를 받아 알람 취소 (기존 로직)
    fun cancel(reminder: MedicationReminder) {
        val requestCode = reminder.id
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Cancelled alarm for category: ${reminder.category}, ID: $requestCode")
    }
}