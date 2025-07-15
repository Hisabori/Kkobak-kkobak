package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kkobakkobak.data.model.MedicationLog

@Dao
interface MedicationLogDao {
    @Insert
    suspend fun insert(log: MedicationLog)

    @Query("SELECT * FROM medication_logs ORDER BY date DESC")
    suspend fun getAllLogs(): List<MedicationLog>
}