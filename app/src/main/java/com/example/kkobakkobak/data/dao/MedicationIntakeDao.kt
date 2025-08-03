package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.data.model.MedicationLog

@Dao
interface MedicationIntakeDao {

    @Insert
    suspend fun insertLog(log: MedicationLog)

    @Query("SELECT * FROM medication_intake ORDER BY timestamp DESC")
    suspend fun getAllIntakes(): List<MedicationIntake> // ✅ 이름 맞게 수정

    @Query("SELECT * FROM medication_intake WHERE date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayIntakeList(): List<MedicationIntake> // ✅ 새로 추가
}
