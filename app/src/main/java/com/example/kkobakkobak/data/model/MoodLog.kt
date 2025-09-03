package com.example.kkobakkobak.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 기분 기록 데이터를 저장하기 위한 테이블(Entity)입니다.
 * tableName은 데이터베이스에서 사용될 테이블의 이름입니다.
 */
@Entity(tableName = "mood_log")
data class MoodLog(
        // PrimaryKey는 각 데이터를 구분하는 고유 ID입니다.
        // autoGenerate = true는 ID를 자동으로 1씩 증가시켜 부여합니다.
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,

        // 기분 상태를 숫자로 저장합니다. (예: 1:아주 좋음, 2:좋음 등)
        val mood: Int,

        // 사용자가 입력한 기분에 대한 구체적인 내용입니다.
        val content: String,

        // 기록이 저장된 시간을 나타냅니다. Long 타입으로 저장하는 것이 효율적입니다.
        val date: Long
)

