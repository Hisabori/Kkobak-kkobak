package com.example.kkobakkobak.data.model

import java.time.LocalDate

data class InpatientEntry(
    val date: LocalDate,
    val weekday: String,
    val count: Int
)
