package com.example.kkobakkobak.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InpatientDao {
    @Query("SELECT * FROM inpatient ORDER BY date ASC")
    fun observeAll(): Flow<List<InpatientEntity>>

    @Query("SELECT COUNT(*) FROM inpatient")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(list: List<InpatientEntity>)

    @Query("DELETE FROM inpatient")
    suspend fun clear()

    @Query("SELECT * FROM inpatient ORDER BY count ASC LIMIT 1")
    suspend fun getMin(): InpatientEntity?

    @Query("SELECT * FROM inpatient ORDER BY count DESC LIMIT 1")
    suspend fun getMax(): InpatientEntity?
}