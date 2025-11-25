package com.example.kkobakkobak.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.ui.main.MainActivity
import com.example.kkobakkobak.worker.IconChangeWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.kkobakkobak.alarm.AlarmScheduler
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "MEDICATION_REMINDER_CHANNEL"

    override fun onReceive(context: Context, intent: Intent) {
        val category = intent.getStringExtra("CATEGORY") ?: "복약"
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: "약물"
        val reminderId = intent.getIntExtra("REMINDER_ID", -1)

        if (reminderId != -1) {
            val db = AppDatabase.getDatabase(context)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val reminder = db.medicationIntakeDao().getReminderById(reminderId)

                if (reminder != null && reminder.isActive) {
                    showNotification(context, reminderId, category, medName)

                    // Schedule icon changes only for morning, lunch, dinner
                    if (category.lowercase() in listOf("morning", "lunch", "dinner")) {
                        scheduleIconChange(context, ".ui.main.MainActivitySad", 1)
                        scheduleIconChange(context, ".ui.main.MainActivityAngry", 10)
                    }

                    val alarmScheduler = AlarmScheduler(context)
                    alarmScheduler.schedule(reminder.copy(id = reminderId))
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
            .addTag("ICON_CHANGE_WORK") // Add a tag to the work request
            .build()

        workManager.enqueue(workRequest)
    }

    private fun showNotification(context: Context, reminderId: Int, category: String, medName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val title = "${category} 복약 시간입니다!"
        val message = "복용 약물: ${medName}"

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val takenIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
            putExtra("NOTIFICATION_ID", reminderId)
            putExtra("CATEGORY", category)
            putExtra("MEDICATION_NAME", medName)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, "복용 완료", takenPendingIntent)
            .build()

        notificationManager.notify(reminderId, notification)
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
