package com.example.kkobakkobak.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.kkobakkobak.alarm.AlarmScheduler

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
                // DAO의 getReminderById 호출 (DAO 업데이트로 오류 해결)
                val reminder = db.medicationIntakeDao().getReminderById(reminderId)

                // isActive 및 copy() 사용 (MedicationReminder 모델이 data class이므로 사용 가능)
                if (reminder != null && reminder.isActive) {
                    showNotification(context, reminderId, category, medName)

                    val alarmScheduler = AlarmScheduler(context)
                    // 다음 날 같은 시간으로 다시 스케줄링
                    alarmScheduler.schedule(reminder.copy(id = reminderId))
                }
            }
        }
    }

    private fun showNotification(context: Context, reminderId: Int, category: String, medName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val title = "${category} 복약 시간입니다!"
        val message = "복용 약물: ${medName}"

        // 1. 메인 액티비티로 이동하는 Intent
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. '복용 완료' 버튼을 눌렀을 때 처리하는 Intent (MedicationTakenReceiver로 보냄)
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