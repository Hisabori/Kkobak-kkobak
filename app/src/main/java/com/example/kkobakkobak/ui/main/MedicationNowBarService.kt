package com.example.kkobakkobak.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kkobakkobak.R

class MedicationNowBarService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val statusText = intent?.getStringExtra("status") ?: "투약 관리 중..."

        // Android 16 (API 36) 이상인지 확인 (One UI 8)
        if (Build.VERSION.SDK_INT >= 36) {
            startLiveUpdate(statusText)
        } else {
            // 하위 버전 호환
            startForegroundLegacy(statusText)
        }

        return START_NOT_STICKY
    }

    // 🚀 [One UI 8 / Android 16] Now Bar 전용 알림
    @RequiresApi(36)
    private fun startLiveUpdate(content: String) {
        val channelId = "live_update_channel"
        val manager = getSystemService(NotificationManager::class.java)

        // 1. 채널 생성 (중요도 MAX)
        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, "실시간 투약 현황", NotificationManager.IMPORTANCE_HIGH)
            channel.setAllowBubbles(true)
            manager.createNotificationChannel(channel)
        }

        // 2. 진행 상태 스타일 (ProgressStyle) 설정 [수정됨]
        // setPointMarker -> setProgressTrackerIcon 으로 변경
        // addSegment -> setProgressSegments 로 변경
        val segment = Notification.ProgressStyle.Segment(100)
            .setColor(Color.parseColor("#3D50E7"))

        val progressStyle = Notification.ProgressStyle()
            .setStyledByProgress(false) // 시스템 기본 색상 대신 커스텀 사용
            .setProgress(100) // 진행률 100%
            .setProgressTrackerIcon(Icon.createWithResource(this, R.drawable.ic_check)) // 👈 수정된 부분
            .setProgressSegments(listOf(segment)) // 👈 수정된 부분

        // 3. 알림 빌드
        val notification = Notification.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("오늘의 투약 현황")
            .setContentText(content)
            .setStyle(progressStyle) // Now Bar 트리거
            .setOngoing(true) // 지워지지 않게 설정
            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        startForeground(2002, notification)
    }

    // 🏚️ [구버전] 일반 알림
    private fun startForegroundLegacy(content: String) {
        val channelId = "now_bar_channel_legacy"
        val manager = getSystemService(NotificationManager::class.java)

        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, "투약 알림", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("꾸박꾸박")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_medication)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1001, notification)
    }
}