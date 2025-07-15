package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager // NotificationManager 추가

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId) // 알림 제거
        }
        // 여기에 약을 먹었다는 기록을 남기는 로직을 추가할 수 있습니다.
        // 예: 데이터베이스에 기록하거나, 스트릭을 업데이트하는 브로드캐스트를 보낼 수 있습니다.
        // LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("com.example.kkobakkobak.ACTION_UPDATE_STREAK"))
    }
}
