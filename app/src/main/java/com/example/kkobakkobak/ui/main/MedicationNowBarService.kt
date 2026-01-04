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

class MedicationNowBarService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(36)
    private fun startLiveUpdate(content: String) {
        val channelId = "live_update_channel"
        val manager = getSystemService(NotificationManager::class.java)

        /*
        // 💡 Android 16 (One UI 8) Now Bar 스타일 직접 참조
        val progressStyle = Notification.ProgressStyle().apply {
            setStyledByProgress(false)
            setProgress(100)
            setProgressTrackerIcon(Icon.createWithResource(this@MedicationNowBarService, com.example.kkobakkobak.R.drawable.ic_check))
            setProgressSegments(listOf(Notification.ProgressStyle.Segment(100).apply {
                setColor(Color.parseColor("#3D50E7"))
            }))
        }

         */

        val notification = Notification.Builder(this, channelId)
            .setSmallIcon(com.example.kkobakkobak.R.drawable.ic_medication)
            //.setStyle(progressStyle)
            .setContentTitle("오늘의 투약 현황")
            .setContentText(content)
            .setOngoing(true)
            .build()

        startForeground(2002, notification)
    }
    // ... 나머지 startForegroundLegacy 등은 유지
}