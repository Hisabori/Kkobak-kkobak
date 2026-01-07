package com.example.kkobakkobak.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kkobakkobak.R // 네 프로젝트의 R 패키지 확인

class MedicationNowBarService : Service() {

    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "MedicationNowBarChannel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()

        // Android 14 (API 34) 대응:
        // 5초 이내에 startForeground를 호출해야 하며, 타입을 명시해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE // 용도에 맞게 선택
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
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

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("꼬박꼬박 알림")
            .setContentText("약 복용 시간을 확인하세요.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘 있는지 확인!
            .setOngoing(true) // 사용자가 못 지우게 설정
            .build()
    }
}