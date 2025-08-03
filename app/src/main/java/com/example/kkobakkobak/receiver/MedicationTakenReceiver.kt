package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        val db = AppDatabase.getDatabase(context)

        CoroutineScope(Dispatchers.IO).launch {
            val currentTime = System.currentTimeMillis()
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime)

            db.medicationIntakeDao().insertLog(
                MedicationLog(
                    date = dateString,
                    mood = 2,  // 1=좋음, 2=보통, 3=나쁨
                    memo = "알림으로 자동 복용 기록됨"
                )
            )
        }
    }
}
