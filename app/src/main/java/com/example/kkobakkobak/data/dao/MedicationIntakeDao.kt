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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: MedicationIntake)

    // 💡 timestamp 에러 방지를 위해 date 필드로 직접 비교
    @Query("SELECT * FROM medication_intake ORDER BY date DESC")
    suspend fun getAllIntakes(): List<MedicationIntake>

    // 💡 SQLite 함수 대신 인자로 오늘 날짜를 넘겨받는 게 에러가 안 나!
    @Query("SELECT * FROM medication_intake WHERE date = :todayDate")
    suspend fun getTodayIntakeList(todayDate: String): List<MedicationIntake>

    @Insert
    suspend fun insertLog(log: MedicationLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MedicationReminder)

    @Update
    suspend fun updateReminder(reminder: MedicationReminder)

    @Query("SELECT * FROM medication_reminder ORDER BY time ASC")
    fun getAllReminders(): Flow<List<MedicationReminder>>

    @Query("SELECT * FROM medication_reminder WHERE id = :id")
    suspend fun getReminderById(id: Int): MedicationReminder?

    @Query("SELECT * FROM medication_reminder WHERE category = :category LIMIT 1")
    suspend fun getReminderByCategory(category: String): MedicationReminder?
}