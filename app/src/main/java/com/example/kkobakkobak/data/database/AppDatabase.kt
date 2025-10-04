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
import com.example.kkobakkobak.data.model.MedicationReminder // import 추가
import com.example.kkobakkobak.data.model.MoodLog

@Database(
    // MedicationReminder Entity 추가
    entities = [MedicationIntake::class, MedicationLog::class, MoodLog::class, MedicationReminder::class],
    // 버전 4로 업데이트 (fallbackToDestructiveMigration으로 충돌 해결)
    version = 4,
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
                    // 데이터베이스 마이그레이션 실패 시 재생성 허용
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}