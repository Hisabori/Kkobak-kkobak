package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kkobakkobak.alarm.AlarmScheduler
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            val alarmScheduler = AlarmScheduler(context)
            val categories = listOf("morning", "lunch", "dinner", "bedtime")
            val now = Calendar.getInstance()

            categories.forEach { category ->
                val hour = prefs.getInt("${category}_hour", -1)
                val minute = prefs.getInt("${category}_minute", -1)
                val active = prefs.getBoolean("${category}_active", false)
                val medName = prefs.getString("${category}_med_name", "미설정") ?: "미설정"

                if (hour != -1 && minute != -1 && active) {
                    val alarmTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        if (before(now)) add(Calendar.DATE, 1)
                    }
                    alarmScheduler.scheduleAlarm(
                        alarmTime.timeInMillis,
                        category,
                        medName
                    )
                }
            }
        }
    }
}
