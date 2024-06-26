package com.app.body_manage.ui.top

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.common.CustomButton
import com.app.body_manage.common.toKcal
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toCentiMeter
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.toWeight
import com.app.body_manage.extension.withPercent
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.common.ColumTextWithLabelAndIcon

@Composable
fun TopScreen(
    userPreference: UserPreference?,
    lastMeasure: BodyMeasure?,
    todayMeasure: TodayMeasure,
    bottomSheetDataList: List<BottomSheetData>,
    onClickSeeTrainingMenu: () -> Unit = {},
    onClickCalendar: () -> Unit = {},
    onClickToday: () -> Unit = {},
    onClickAddMeasure: () -> Unit = {},
    onClickAddMeal: () -> Unit = {},
    onClickAddTraining: () -> Unit = {},
    onClickSetGoat: () -> Unit = {},
    onClickSetting: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            Column {
                BottomSheet(bottomSheetDataList)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .background(background)
                .padding(it)
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            item {
                Row(verticalAlignment = Alignment.Bottom) {
                    if (lastMeasure?.weight != null) {
                        Text(
                            text = lastMeasure.weight.toString(),
                            fontSize = 32.sp,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(id = R.string.unit_kg),
                            fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.label_reserve),
                            fontSize = 18.sp,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    lastMeasure?.time?.toLocalDate()?.toMMDDEE()?.let { mmdd ->
                        val label = stringResource(id = R.string.label_registered_date)
                        Text(
                            text = "$label: $mmdd",
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    (lastMeasure?.tall)?.let { tall ->
                        Text(
                            text = tall.toCentiMeter(),
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onClickSetting()
                        }
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (userPreference?.goalWeight == null && lastMeasure != null) {
                item {
                    RequireGoal(onClickSetGoat)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            } else if (lastMeasure != null && userPreference != null) {
                item {
                    Goal(
                        bodyMeasure = lastMeasure,
                        userPreference = userPreference,
                        meal = todayMeasure.meals,
                        onClickSetGoat
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
            item {
                TodaySummary(
                    todayMeasure = todayMeasure,
                    onClickToday = onClickToday,
                    onClickAddMeal = onClickAddMeal,
                    onClickAddMeasure = onClickAddMeasure,
                    onClickAddTraining = onClickAddTraining,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (lastMeasure != null && userPreference != null) {
                item {
                    Statistics(
                        bodyMeasure = lastMeasure,
                        userPreference = userPreference,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.CalendarMonth,
                        modifier = Modifier.padding(vertical = 5.dp),
                        onClick = { onClickCalendar() },
                        text = stringResource(id = R.string.label_see_by_calendar),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn {
                    IconAndText(
                        icon = Icons.Default.EmojiPeople,
                        modifier = Modifier.padding(vertical = 5.dp),
                        onClick = { onClickSeeTrainingMenu() },
                        text = stringResource(id = R.string.label_see_by_training_menu),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun Goal(
    bodyMeasure: BodyMeasure,
    userPreference: UserPreference,
    meal: List<Meal>,
    onClickSetGoat: () -> Unit,
) {
    PanelColumn {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.label_target_weight) + " ${userPreference.goalWeight} kg"
            )
            Spacer(Modifier.weight(1F))
            Text(text = userPreference.progressWeightText(bodyMeasure.weight))
        }
        Spacer(modifier = Modifier.size(10.dp))
        LinearProgressIndicator(
            progress = userPreference.progressWeight(bodyMeasure.weight),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.label_target_kcal) + " ${userPreference.goalKcal} kcal"
            )
            Spacer(Modifier.weight(1F))
            Text(text = userPreference.progressKcalText(meal.sumOf { it.totalKcal }))
        }
        Spacer(modifier = Modifier.size(10.dp))
        LinearProgressIndicator(
            progress = userPreference.progressKcal(meal.sumOf { it.totalKcal }),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            CustomButton(
                modifier = Modifier.height(35.dp),
                onClick = { onClickSetGoat() },
                valueResourceId = R.string.label_update_object,
                backgroundColor = theme
            )
        }
    }
}

@Composable
private fun RequireGoal(
    onClickSetGoat: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .height(120.dp)
            .padding(5.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            TextWithUnderLine(R.string.label_set_object)
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = stringResource(id = R.string.message_set_object),
                fontSize = 12.sp,
            )
        }
        CustomButton(
            modifier = Modifier.height(35.dp),
            onClick = { onClickSetGoat() },
            valueResourceId = R.string.label_set_object,
            backgroundColor = theme
        )
    }
}

@Composable
private fun TodaySummary(
    todayMeasure: TodayMeasure,
    onClickToday: () -> Unit,
    onClickAddMeal: () -> Unit,
    onClickAddMeasure: () -> Unit,
    onClickAddTraining: () -> Unit,
) {
    PanelColumn(
        modifier = Modifier.clickable { onClickToday() }
    ) {
        TextWithUnderLine(R.string.label_today_you)
        Spacer(modifier = Modifier.size(12.dp))
        if (todayMeasure.bodyMeasures.isEmpty() && todayMeasure.meals.isEmpty() && todayMeasure.didTraining.not()) {
            Text(text = stringResource(id = R.string.message_not_registered_today))
        }
        if (todayMeasure.meals.isNotEmpty()) {
            LabelAndText(
                stringResource(id = R.string.label_today_total_kcal),
                todayMeasure.meals.sumOf { it.totalKcal }.toKcal()
            )
            Spacer(modifier = Modifier.size(12.dp))
        }
        if (todayMeasure.bodyMeasures.isNotEmpty()) {
            LabelAndText(
                stringResource(id = R.string.label_today_min_weight),
                todayMeasure.minWeight
            )
            Spacer(modifier = Modifier.size(12.dp))
        }
        if (todayMeasure.didTraining) {
            Text(text = stringResource(id = R.string.label_did_training))
            Spacer(modifier = Modifier.size(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CustomButton(
                backgroundColor = theme,
                onClick = { onClickAddMeasure() },
                valueResourceId = R.string.label_add_measure,
                fontSize = 11.sp,
            )
            CustomButton(
                backgroundColor = theme,
                onClick = { onClickAddMeal() },
                valueResourceId = R.string.label_add_meal,
                fontSize = 11.sp,
            )
            CustomButton(
                backgroundColor = theme,
                onClick = { onClickAddTraining() },
                valueResourceId = R.string.label_add_training,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
fun Statistics(
    bodyMeasure: BodyMeasure,
    userPreference: UserPreference,
) {
    Panel(content = {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextWithUnderLine(stringResource(id = R.string.label_current_you, userPreference.name))
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.weight),
                    value = bodyMeasure.weight.toWeight(),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.tall),
                    value = bodyMeasure.tall.toCentiMeter(),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.age),
                    value = userPreference.birth.age().toString(),
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_bmi),
                    value = userPreference.bim(bodyMeasure.tall, bodyMeasure.weight),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_kcal) + "※",
                    value = userPreference.basicConsumeEnergy(bodyMeasure.tall, bodyMeasure.weight),
                )
                ColumTextWithLabelAndIcon(
                    title = stringResource(id = R.string.label_fat),
                    value = userPreference.calcFat(bodyMeasure.tall, bodyMeasure.weight)
                        .withPercent(),
                )
            }
        }
    }) {
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            stringResource(id = R.string.message_this_is_estimated_value),
            fontSize = 11.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun TextWithUnderLine(
    @StringRes stringResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = stringResourceId),
        modifier = modifier.drawBehind {
            drawLine(
                Color.Black,
                Offset(-10F, size.height),
                Offset(size.width + 10F, size.height),
                strokeWidth = 1F
            )
        },
        fontSize = 16.sp,
    )
}

@Composable
fun TextWithUnderLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .heightIn(min = 30.dp)
            .drawBehind {
                drawLine(
                    Color.Black,
                    Offset(-10F, size.height),
                    Offset(size.width + 10F, size.height),
                    strokeWidth = 1F
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = modifier,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LabelAndText(
    label: String,
    text: String
) {
    Row {
        Text(text = label, modifier = Modifier.width(130.dp))
        Text(text = text)
    }
}

@Composable
fun BottomButtons(
    onClickAddMeasure: () -> Unit,
    onClickAddMeal: () -> Unit,
    onClickAddTraining: () -> Unit,
) {
    Row(
        modifier = Modifier
            .shadow(2.dp)
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        CustomButton(
            onClick = onClickAddMeal,
            valueResourceId = R.string.label_add_meal,
            backgroundColor = theme,
        )
        CustomButton(
            onClick = onClickAddMeasure,
            valueResourceId = R.string.label_add_measure,
            backgroundColor = theme,
        )
        CustomButton(
            onClick = onClickAddTraining,
            valueResourceId = R.string.label_add_training,
            backgroundColor = theme,
        )
    }
}

@Composable
fun VerticalLine() {
    Box(
        modifier = Modifier
            .width(width = 1.dp)
            .height(height = 50.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(1.dp))
    )
}

@Composable
fun HorizontalLine(
    verticalPadding: Dp = 20.dp
) {
    Box(
        modifier = Modifier
            .padding(vertical = verticalPadding)
            .fillMaxWidth()
            .height(height = 1.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(1.dp))
    )
}

@Composable
fun IconAndText(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    withArrow: Boolean = true,
    message: String? = null,
    subTitle: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(text = text)
            subTitle?.let {
                Text(
                    text = subTitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1F))
        if (withArrow) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
        if (message != null) {
            Text(
                text = message,
            )
        }
    }
}

@Composable
fun PanelColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
fun Panel(
    content: @Composable () -> Unit,
    bottom: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        bottom()
    }
}
