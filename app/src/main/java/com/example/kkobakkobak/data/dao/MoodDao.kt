package com.example.kkobakkobak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kkobakkobak.data.model.MoodLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMoodLog(moodLog: MoodLog)

    @Query("SELECT * FROM mood_log ORDER BY date DESC")
    fun getAllMoodLogs(): Flow<List<MoodLog>>
}
