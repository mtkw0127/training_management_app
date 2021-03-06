package com.app.body_manage.data.dao.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateConverter {
    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): String = localDate.toString()

    @TypeConverter
    fun toLocalDate(stringDate: String): LocalDate = LocalDate.parse(stringDate)

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): String = localDateTime.toString()

    @TypeConverter
    fun toLocalDateTime(stringLocalDateTime: String): LocalDateTime =
        LocalDateTime.parse(stringLocalDateTime)
}