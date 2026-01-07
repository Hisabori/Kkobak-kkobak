package com.example.kkobakkobak.data.model

data class SurveyItem(
    val id: Int,
    val question: String,
    var selectedScore: Int = -1
)