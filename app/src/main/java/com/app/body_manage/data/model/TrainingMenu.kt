package com.app.body_manage.data.model

import androidx.annotation.StringRes
import com.app.body_manage.R
import com.app.body_manage.common.toCount
import com.app.body_manage.common.toKg

data class TrainingMenu(
    val id: Id,
    val name: String,
    val part: Part,
    val memo: String,
    val sets: List<Set>, // MEMO：最新のセット数。履歴管理する。
    val type: Type,
) {
    data class Id(val value: Long)

    sealed interface Set {
        val index: Int
        val targetNumber: Int
        val actualNumber: Int
    }

    // MEMO: 例えば60kgを10回上げるような感じ
    // マシン・フリーウェイト用のSet
    data class WeightSet(
        override val index: Int,
        override val targetNumber: Int, // 目標回数
        override val actualNumber: Int, // 実際の回数
        val targetWeight: Int, // 目標重量
        val actualWeight: Int, // 実際の重量
    ) : Set {
        val targetText = "${targetWeight.toKg()} ${targetNumber.toCount()}"
    }

    // マシン・フリーウェイト用のSet
    data class OwnWeightSet(
        override val index: Int,
        override val targetNumber: Int, // 目標回数
        override val actualNumber: Int, // 実際の回数
        val additionalWeight: Int, // 加重
    ) : Set

    enum class Type(@StringRes val nameStringRes: Int) {
        MACHINE(R.string.label_machine), FREE(R.string.label_free), OWN_WEIGHT(R.string.label_own_weight)
    }

    enum class Part(
        @StringRes val nameStringResourceId: Int,
    ) {
        // 胸
        CHEST(R.string.label_type_chest),

        // 肩
        SHOULDER(R.string.label_type_shoulder),

        // 背中
        BACK(R.string.label_type_back),

        // 腕
        ARM(R.string.label_type_arm),

        // 腹部
        ABDOMINAL(R.string.label_type_abdominal),

        // 尻
        HIP(R.string.label_type_hip),

        // 脚
        LEG(R.string.label_type_leg),

        // その他
        ELSE(R.string.label_type_else),
    }
}

fun createSampleTrainingMenu(): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "ダンベルベンチプレス",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.WeightSet(
                index = index + 1,
                targetNumber = 10,
                actualNumber = 10,
                targetWeight = 50,
                actualWeight = 55,
            )
        },
        type = TrainingMenu.Type.MACHINE,
    )
}

fun createSampleOwnWeightTrainingMenu(): TrainingMenu {
    return TrainingMenu(
        id = TrainingMenu.Id(0),
        name = "腕立て伏せ",
        part = TrainingMenu.Part.ARM,
        memo = "メモメモ".repeat(4),
        sets = List(5) { index ->
            TrainingMenu.OwnWeightSet(
                index = index + 1,
                targetNumber = 10 + index,
                actualNumber = 10 + index,
                additionalWeight = 0 + index,
            )
        },
        type = TrainingMenu.Type.OWN_WEIGHT,
    )
}
