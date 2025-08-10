package com.example.kkobakkobak.data.db

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverters {
    @TypeConverter
    fun fromString(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun toString(value: LocalDate?): String? = value?.toString()
}
