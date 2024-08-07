package com.app.body_manage.data.model

import android.net.Uri
import com.app.body_manage.data.entity.BodyMeasureEntity
import java.time.LocalDateTime

data class BodyMeasure(
    val id: Id,
    override val time: LocalDateTime,
    val weight: Float,
    val fat: Float,
    val memo: String,
    val photoUri: Uri?,
    val tall: Float,
) : Measure {
    @JvmInline
    value class Id(val value: Int)

    companion object {
        val INITIAL = BodyMeasure(
            id = Id(0),
            time = LocalDateTime.now(), // その日付の現在時刻
            weight = 50F,
            fat = 20F,
            photoUri = null,
            tall = 150F,
            memo = "",
        )
    }
}

fun BodyMeasure.toEntity() = BodyMeasureEntity(
    ui = id.value,
    calendarDate = time.toLocalDate(),
    capturedTime = time,
    weight = weight,
    fat = fat,
    memo = memo,
    photoUri = photoUri?.path,
    tall = tall,
)
