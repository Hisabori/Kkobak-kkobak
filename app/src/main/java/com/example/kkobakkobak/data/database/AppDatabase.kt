package com.example.kkobakkobak.data.database;

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kkobakkobak.data.dao.MedicationLogDao
import com.example.kkobakkobak.data.dao.MedicationIntakeDao
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.model.MedicationIntake

@Database(
    entities = [MedicationLog::class, MedicationIntake::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationLogDao(): MedicationLogDao
    abstract fun medicationIntakeDao(): MedicationIntakeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}