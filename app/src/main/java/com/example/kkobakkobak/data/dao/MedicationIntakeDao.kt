package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kkobakkobak.data.model.MedicationIntake

@Dao
interface MedicationIntakeDao {
    @Insert
    suspend fun insert(intake: MedicationIntake)

    @Query("SELECT * FROM medication_intakes ORDER BY timestamp DESC")
    suspend fun getAllIntakes(): List<MedicationIntake>
}
