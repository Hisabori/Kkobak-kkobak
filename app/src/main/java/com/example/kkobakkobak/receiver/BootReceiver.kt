package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val db = AppDatabase.getDatabase(context)
            val scheduler = AlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                // 저장된 모든 알람을 가져와서 재등록
                val reminders = db.medicationIntakeDao().getAllReminders().first()
                reminders.forEach { reminder ->
                    if (reminder.isActive) {
                        scheduler.schedule(reminder)
                    }
                }
            }
        }
    }
}