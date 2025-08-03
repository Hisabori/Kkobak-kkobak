package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_intake")
data class MedicationIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val medicationName: String,
    val taken: Boolean
)
