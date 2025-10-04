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

    // MedicationReminder 객체를 받아 알람 스케줄링
    fun schedule(reminder: MedicationReminder) {
        // ID를 RequestCode로 사용 (고유성 보장)
        val requestCode = reminder.id

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("CATEGORY", reminder.category)
            putExtra("MEDICATION_NAME", reminder.medicationName)
            putExtra("REMINDER_ID", reminder.id) // 알람 발생 시 DB 업데이트를 위해 ID 전달
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

            // 만약 이미 지난 시간이면 다음 날로 설정
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        try {
            // 정확한 시간에 알람 설정
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

    // MedicationReminder 객체를 받아 알람 취소
    fun cancel(reminder: MedicationReminder) {
        val requestCode = reminder.id // 스케줄링할 때 사용한 동일한 requestCode 사용
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