package com.example.kkobakkobak.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "inpatient")
data class InpatientEntity(
    @PrimaryKey val date: LocalDate, // 2020-01-01
    val weekday: String,             // 수/목/...
    val count: Int                   // 입원환자수
)
