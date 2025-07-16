package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_intakes")
data class MedicationIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medName: String,
    val timestamp: Long
)
