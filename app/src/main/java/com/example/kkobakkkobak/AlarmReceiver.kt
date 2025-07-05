package com.example.kkobakkkobak

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

// AlarmReceiver 클래스 시작
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val category = intent.getStringExtra("category") ?: return
        sendNotification(context, category)
        rescheduleAlarm(context, category)
    }

    private fun sendNotification(context: Context, category: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_alarm_channel"
        val notificationId = getRequestCode(category)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "투약 알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val takenIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
            putExtra("notificationId", notificationId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = getNotificationContent(context, category)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, "먹었어요 ✅", takenPendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun rescheduleAlarm(context: Context, category: String) {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val hour = prefs.getInt("${category}_hour", -1)
        val minute = prefs.getInt("${category}_minute", -1)
        if (hour == -1 || minute == -1) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).putExtra("category", category)
        val pendingIntent = PendingIntent.getBroadcast(
            context, getRequestCode(category), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 1)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: SecurityException) { /* MainActivity에서 권한 처리 */ }
    }

    private fun getRequestCode(category: String): Int = when (category) {
        "morning" -> 101; "lunch" -> 102; "dinner" -> 103; "bedtime" -> 104; else -> 0
    }

    private fun getNotificationContent(context: Context, category: String): Pair<String, String> {
        val title = when (category) {
            "morning" -> "아침 약 ☀️"; "lunch" -> "점심 약 🍚"
            "dinner" -> "저녁 약 🌙"; "bedtime" -> "취침 전 약 🛏️"
            else -> "투약 시간"
        }
        val messages = context.resources.getStringArray(R.array.encouragement_messages)
        val randomMessage = messages.random()
        return title to randomMessage
    }
} // AlarmReceiver 클래스 끝