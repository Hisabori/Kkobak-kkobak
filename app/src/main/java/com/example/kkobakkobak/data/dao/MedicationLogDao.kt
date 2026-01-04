package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kkobakkobak.data.model.MedicationLog
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface MedicationLogDao {
    @Insert
    suspend fun insert(log: MedicationLog)

    //실시간 업데이트
    @Query("SELECT * FROM medication_logs ORDER BY date DESC")
    fun getAll(): Flow<List<MedicationLog>>


    @Query("DELETE FROM medication_logs")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteAllAndInsertAll(logs: List<MedicationLog>) {
        deleteAll()
        logs.forEach {
            insert(it)
        }
    }
}