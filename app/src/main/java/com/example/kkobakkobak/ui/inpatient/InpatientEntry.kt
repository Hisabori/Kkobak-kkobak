package com.example.kkobakkobak.ui.inpatient

data class InpatientEntry(
    val date: String,
    val count: Int,
    val weekday: String     // 요일 텍스트 (예: "월", "화")
)
