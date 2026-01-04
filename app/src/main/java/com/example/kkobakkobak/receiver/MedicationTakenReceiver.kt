package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationIntake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", -1L)
        val medicineName = intent.getStringExtra("MEDICATION_NAME") ?: "알 수 없는 약"
        val time = intent.getStringExtra("TIME") ?: ""

        if (reminderId != -1L) {
            val db = AppDatabase.getDatabase(context)
            CoroutineScope(Dispatchers.IO).launch {
                val intake = MedicationIntake(
                    medicineName = medicineName,
                    dosage = "1정",
                    time = time,
                    date = LocalDate.now().toString()
                )
                db.medicationIntakeDao().insertIntake(intake)
            }
        }
    }
}