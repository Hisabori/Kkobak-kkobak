package com.example.kkobakkobak.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [InpatientEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inpatientDao(): InpatientDao
}
