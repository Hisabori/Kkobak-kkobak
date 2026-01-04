package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_reminder")
data class MedicationReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val category: String, // 👈 추가: 아침, 점심, 저녁 등 (에러 해결의 핵심!)
    val isActive: Boolean = true
)