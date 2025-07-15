package com.example.kkobakkobak.data.model

data class MedicationReminder(
    val category: String, // e.g., "morning", "lunch", "dinner", "bedtime"
    var hour: Int = -1,
    var minute: Int = -1,
    var medicationName: String = "미설정",
    var isActive: Boolean = false
)