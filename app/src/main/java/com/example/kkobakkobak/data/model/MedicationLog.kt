package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_logs")
data class MedicationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val mood: Int, // 1: 좋음, 2: 보통, 3: 나쁨
    val memo: String
)   