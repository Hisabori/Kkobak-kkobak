package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.model.MedicationReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationIntakeDao {

    // --- MedicationIntake (복용 기록) 관련 쿼리 ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: MedicationIntake)

    @Query("SELECT * FROM medication_intake ORDER BY timestamp DESC")
    suspend fun getAllIntakes(): List<MedicationIntake>

    @Query("SELECT * FROM medication_intake WHERE date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayIntakeList(): List<MedicationIntake>

    // --- MedicationLog (일반 로그) 관련 쿼리 ---

    @Insert
    suspend fun insertLog(log: MedicationLog)

    // --- MedicationReminder (복약 알림) 관련 쿼리 ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MedicationReminder)

    @Update
    suspend fun updateReminder(reminder: MedicationReminder)

    @Query("SELECT * FROM medication_reminder ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<MedicationReminder>>

    @Query("SELECT * FROM medication_reminder WHERE id = :id")
    suspend fun getReminderById(id: Int): MedicationReminder?

    @Query("SELECT * FROM medication_reminder WHERE category = :category LIMIT 1")
    suspend fun getReminderByCategory(category: String): MedicationReminder?
}