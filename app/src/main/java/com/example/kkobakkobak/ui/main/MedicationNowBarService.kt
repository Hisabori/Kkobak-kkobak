package com.example.kkobakkobak.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kkobakkobak.R
import com.example.kkobakkobak.receiver.MedicationTakenReceiver

class MedicationNowBarService : Service() {

    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "MedicationNowBarChannel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val status = intent?.getStringExtra("status") ?: "약 복용 시간을 확인하세요."
        val notification = createNotification(status)

        // Android 14 (API 34) 대응:
        // 5초 이내에 startForeground를 호출해야 하며, 타입을 명시해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // 이미 실행 중인 경우 알림만 업데이트
        if (intent?.action == "UPDATE_STATUS") {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    private fun createNotification(content: String): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "약 복용 알림",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "현재 복용해야 할 약 정보를 보여줍니다."
            }
            manager.createNotificationChannel(channel)
        }

        // 앱 실행 인텐트 (삼성 Ongoing Activity 클릭 시 대응)
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 즉시 복용 버튼 인텐트
        val takeIntent = Intent(this, MedicationTakenReceiver::class.java).apply {
            action = "ACTION_TAKE_MEDICATION"
        }
        val takePendingIntent = android.app.PendingIntent.getReceiver(
            this, 1, takeIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("꼬박꼬박 알림")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_check, "💊 지금 복용", takePendingIntent)
            .apply {
                // 삼성 Ongoing Activity를 위한 스타일 설정
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                }
            }
            .build()
    }
}