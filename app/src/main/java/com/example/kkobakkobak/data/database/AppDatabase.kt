package com.example.kkobakkobak.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kkobakkobak.data.dao.MedicationIntakeDao
import com.example.kkobakkobak.data.dao.MedicationLogDao
import com.example.kkobakkobak.data.dao.MoodDao
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.model.MoodLog

@Database(
    entities = [MedicationIntake::class, MedicationLog::class, MoodLog::class],
    // ✅ 1. 버전이 2 이상으로 올라가 있는지 확인
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationIntakeDao(): MedicationIntakeDao
    abstract fun medicationLogDao(): MedicationLogDao
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kkobak_database"
                )
                    // ✅ 2. 이 코드가 반드시 포함되어 있는지 확인! (충돌 방지)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

