package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager // NotificationManager 추가
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationIntake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId) // 알림 제거
        }
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: return
        val db = AppDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            db.medicationIntakeDao().insert(
                MedicationIntake(medName = medName, timestamp = System.currentTimeMillis())
            )
        }
    }
}
