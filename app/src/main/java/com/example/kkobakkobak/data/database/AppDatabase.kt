package com.example.kkobakkobak.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kkobakkobak.data.model.MedicationReminder
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.model.MoodLog // 💡 경로 확인 완료
import com.example.kkobakkobak.data.dao.MedicationIntakeDao

@Database(
    entities = [
        MedicationReminder::class,
        MedicationIntake::class,
        MedicationLog::class,
        MoodLog::class,
        InpatientEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationIntakeDao(): MedicationIntakeDao
    abstract fun inpatientDao(): InpatientDao
    abstract fun moodDao(): com.example.kkobakkobak.data.dao.MoodDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kkobak_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}