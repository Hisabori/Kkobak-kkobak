package com.example.kkobakkkobak

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            listOf("morning", "lunch", "dinner", "bedtime").forEach { category ->
                val hour = prefs.getInt("${category}_hour", -1)
                val minute = prefs.getInt("${category}_minute", -1)
                if (hour != -1 && minute != -1) {
                    rescheduleAlarm(context, category, hour, minute)
                }
            }
        }
    }

    private fun rescheduleAlarm(context: Context, category: String, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: SecurityException) { /* 권한은 앱 실행 시 확보 */ }
    }

    private fun getRequestCode(category: String): Int = when (category) {
        "morning" -> 101; "lunch" -> 102; "dinner" -> 103; "bedtime" -> 104; else -> 0
    }
}