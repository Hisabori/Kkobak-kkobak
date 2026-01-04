package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kkobakkobak.data.database.AppDatabase // 💡 base -> AppDatabase 수정
import com.example.kkobakkobak.ui.alarm.AlarmFullscreenActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", -1L)

        if (reminderId != -1L) {
            val db = AppDatabase.getDatabase(context)
            CoroutineScope(Dispatchers.IO).launch {
                // 💡 Long을 Int로 변환하여 Dao 호출
                val reminder = db.medicationIntakeDao().getReminderById(reminderId.toInt())

                if (reminder != null && reminder.isActive) {
                    val fullScreenIntent = Intent(context, AlarmFullscreenActivity::class.java).apply {
                        putExtra("REMINDER_ID", reminderId)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(fullScreenIntent)
                }
            }
        }
    }
}