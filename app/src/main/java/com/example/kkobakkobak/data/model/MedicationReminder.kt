package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_reminder") // @Entity 주석 추가
data class MedicationReminder(
    @PrimaryKey(autoGenerate = true) // @PrimaryKey 주석 추가
    val id: Int = 0,

    // 복용 시간대 (morning, lunch, dinner, bedtime)
    val category: String,

    // 시간 정보 (미설정 시 -1)
    var hour: Int = -1,
    var minute: Int = -1,

    // 복용할 약물 이름 (미설정 시 "미설정")
    var medicationName: String = "미설정",

    // 알림 활성화 여부
    var isActive: Boolean = false
)