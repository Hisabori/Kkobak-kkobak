package com.example.kkobakkobak.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.worker.IconChangeWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.ui.alarm.AlarmFullscreenActivity // 👈 신규 Activity import
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "MEDICATION_REMINDER_CHANNEL"

    // 🔔 [추가] 풀스크린 액티비티 실행 함수
    private fun showAlarmFullscreen(context: Context, reminderId: Int, category: String, medName: String) {
        val fullScreenIntent = Intent(context, AlarmFullscreenActivity::class.java).apply {
            putExtra("REMINDER_ID", reminderId)
            putExtra("CATEGORY", category)
            putExtra("MEDICATION_NAME", medName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        context.startActivity(fullScreenIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val category = intent.getStringExtra("CATEGORY") ?: "복약"
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: "약물"
        val reminderId = intent.getIntExtra("REMINDER_ID", -1)
        val isSnooze = intent.getBooleanExtra("IS_SNOOZE", false) // 👈 Snooze 여부 확인

        if (reminderId != -1) {
            val db = AppDatabase.getDatabase(context)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val reminder = db.medicationIntakeDao().getReminderById(reminderId)

                if (reminder != null && reminder.isActive) {

                    // 기존 showNotification 대신 풀스크린 Activity를 띄움
                    showAlarmFullscreen(context, reminderId, category, medName)

                    // Schedule icon changes only for morning, lunch, dinner
                    if (category.lowercase() in listOf("morning", "lunch", "dinner")) {
                        scheduleIconChange(context, ".ui.main.MainActivitySad", 1)
                        scheduleIconChange(context, ".ui.main.MainActivityAngry", 10)
                    }

                    // 🔔 [수정] 스누즈 알람이 아닌 경우에만 다음 날로 재스케줄링
                    if (!isSnooze) {
                        val alarmScheduler = AlarmScheduler(context)
                        // 다음 날 같은 시간으로 알람 재등록
                        alarmScheduler.schedule(reminder.copy(id = reminderId))
                    }
                }
            }
        }
    }

    private fun scheduleIconChange(context: Context, aliasName: String, delayMinutes: Long) {
        val workManager = WorkManager.getInstance(context)
        val data = Data.Builder()
            .putString(IconChangeWorker.ALIAS_NAME_KEY, context.packageName + aliasName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<IconChangeWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(data)
            .addTag("ICON_CHANGE_WORK")
            .build()

        workManager.enqueue(workRequest)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "복약 알림"
            val descriptionText = "꼬박꼬박 복약 알림 채널입니다."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}