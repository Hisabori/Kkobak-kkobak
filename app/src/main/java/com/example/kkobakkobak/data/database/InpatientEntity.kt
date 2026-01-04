package com.example.kkobakkobak.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inpatient")
data class InpatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val count: Int
)