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

    fun schedule(reminder: MedicationReminder) {
        val requestCode = reminder.id.toInt() // 💡 Long을 Int로 변환

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("CATEGORY", reminder.category)
            putExtra("MEDICATION_NAME", reminder.medicineName) // 💡 medicineName으로 수정
            putExtra("REMINDER_ID", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 💡 time 문자열(HH:mm)을 시, 분으로 파싱
        val timeParts = reminder.time.split(":")
        val hour = timeParts.getOrNull(0)?.toInt() ?: 0
        val minute = timeParts.getOrNull(1)?.toInt() ?: 0

        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
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
            Log.d("AlarmScheduler", "Scheduled alarm for ${reminder.medicineName} at $hour:$minute")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Security error: ${e.message}")
        }
    }

    fun scheduleSnooze(reminder: MedicationReminder, delayMinutes: Int) {
        val requestCode = reminder.id.toInt() + 1000 // 💡 Int 변환

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("CATEGORY", reminder.category)
            putExtra("MEDICATION_NAME", reminder.medicineName) // 💡 medicineName으로 수정
            putExtra("REMINDER_ID", reminder.id)
            putExtra("IS_SNOOZE", true)
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

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTime.timeInMillis,
            pendingIntent
        )
    }

    fun cancel(reminder: MedicationReminder) {
        val requestCode = reminder.id.toInt() // 💡 Int 변환
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}