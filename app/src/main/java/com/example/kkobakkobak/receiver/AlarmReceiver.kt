package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.example.kkobakkobak.R
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.ui.main.MainActivity // 이 줄 추가!
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("MEDICATION_NAME") ?: "약"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingTapIntent = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val takeActionIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
            putExtra("MEDICATION_NAME", medicationName)
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val pendingTakeActionIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1,
            takeActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(context, "medication_alarm_channel")
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("투약 알림: $medicationName")
            .setContentText("$medicationName 복용 시간입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingTapIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, "복용 완료", pendingTakeActionIntent)
            .build()

        notificationManager.notify(notificationId, notification)

        val alarmScheduler = AlarmScheduler(context)
        // 여기에 알람 재설정 로직이 필요하다면 구현해야 해.
    }
}