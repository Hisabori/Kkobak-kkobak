package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.util.Log
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationIntake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        // val category = intent.getStringExtra("CATEGORY") ?: "복약" // MedicationIntake에 category 필드 없음
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: "약물"

        if (notificationId != -1) {
            // 알림 닫기
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)

            // DB에 복용 기록 저장
            val db = AppDatabase.getDatabase(context)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                // MedicationIntake 모델 (timestamp, medicationName, taken)에 맞게 수정
                val newIntake = MedicationIntake(
                    medicationName = medName,
                    timestamp = System.currentTimeMillis(), // 현재 시간으로 기록
                    taken = true // 복용 완료이므로 true 설정
                    // category/intakeTime 필드는 MedicationIntake 모델에 없으므로 삭제
                )

                // DAO 함수 호출을 insertIntake로 변경
                db.medicationIntakeDao().insertIntake(newIntake)

                Log.d("MedTakenReceiver", "Medication intake recorded: $medName")
            }
        }
    }
}