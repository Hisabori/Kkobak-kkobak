package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_intake")
data class MedicationIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val isTaken: Boolean = false,
    val date: String // 💡 날짜가 String으로 잘 되어있는지 확인!
)