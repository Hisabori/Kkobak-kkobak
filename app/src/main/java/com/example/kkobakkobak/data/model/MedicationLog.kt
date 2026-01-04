package com.example.kkobakkobak.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "medication_logs")
data class MedicationLog(

    //서버 id(크로스 디바이스)
    @ColumnInfo(name = "server_id")
    var serverId: String? = null,

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val mood: Int, // 1: 좋음, 2: 보통, 3: 나쁨
    val memo: String





)