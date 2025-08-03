package com.example.kkobakkobak.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.dao.MedicationIntakeDao
import com.example.kkobakkobak.data.dao.MedicationLogDao

@Database(
    entities = [MedicationIntake::class, MedicationLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationIntakeDao(): MedicationIntakeDao
    abstract fun medicationLogDao(): MedicationLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medication_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
