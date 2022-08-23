package com.app.body_manage.data.entity

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "bodyMeasures"
)
data class BodyMeasureEntity(
    @PrimaryKey(autoGenerate = true) var ui: Int,
    @ColumnInfo(name = "calendar_date") val calendarDate: LocalDate,
    @ColumnInfo(name = "capture_date") val capturedDate: LocalDate,
    @ColumnInfo(name = "capture_time") val capturedTime: LocalDateTime,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "fat") val fatRate: Float,
    @ColumnInfo(name = "photo_uri") var photoUri: String?,
    @ColumnInfo(name = "tall") val tall: Float?,
) : Serializable

data class BodyMeasureModel(
    val ui: Id,
    val capturedLocalDateTime: LocalDateTime,
    val weight: Float,
    val fat: Float,
    val photoUri: Uri?
) {
    @JvmInline
    value class Id(val ui: Int)
}

fun BodyMeasureEntity.toModel(): BodyMeasureModel =
    BodyMeasureModel(
        ui = BodyMeasureModel.Id(this.ui),
        capturedLocalDateTime = this.capturedTime,
        weight = this.weight,
        fat = this.fatRate,
        photoUri = this.photoUri?.toUri()
    )