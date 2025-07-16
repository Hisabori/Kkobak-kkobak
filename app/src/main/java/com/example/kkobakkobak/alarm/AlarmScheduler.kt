package com.example.kkobakkobak.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kkobakkobak.receiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(timeInMillis: Long, category: String, medName: String) {
        val notificationId = category.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MEDICATION_NAME", medName)
            putExtra("NOTIFICATION_ID", notificationId)
        }

        // PendingIntent의 RequestCode는 알람마다 고유해야 합니다.
        // 여기서는 medName의 해시코드나 미리 정의된 고유한 코드를 사용할 수 있습니다.
        val requestCode = notificationId // 간단한 고유 코드 생성

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Scheduled alarm for $medName at ${java.text.SimpleDateFormat("HH:mm").format(timeInMillis)}, requestCode: $requestCode")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Failed to schedule alarm due to security exception: ${e.message}")
        }
    }

    fun cancelAlarm(category: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = category.hashCode() // 스케줄링할 때 사용한 동일한 requestCode 사용

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Cancelled alarm for category: $category, requestCode: $requestCode")
    }
}
