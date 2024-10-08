package com.app.body_manage.ui.top

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.body_manage.BuildConfig
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.common.CustomButton
import com.app.body_manage.data.local.UserPreference
import com.app.body_manage.data.model.BodyMeasure
import com.app.body_manage.data.model.Meal
import com.app.body_manage.extension.age
import com.app.body_manage.extension.toCentiMeter
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.toWeight
import com.app.body_manage.extension.withPercent
import com.app.body_manage.style.Colors.Companion.accentColor
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.disable
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.common.ColumTextWithLabelAndIcon
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize.FULL_BANNER
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun TopScreen(
    userPreference: UserPreference?,
    lastMeasure: BodyMeasure?,
    initialMeasure: BodyMeasure?,
    todayMeasure: TodayMeasure,
    enableUpdate: Boolean,
    bottomSheetDataList: List<BottomSheetData>,
    onClickSeeTrainingMenu: () -> Unit = {},
    onClickMeal: () -> Unit = {},
    onClickCompare: () -> Unit = {},
    onClickPhotos: () -> Unit = {},
    onClickToday: () -> Unit = {},
    onClickAddMeasure: () -> Unit = {},
    onClickAddMeal: () -> Unit = {},
    onClickAddTraining: () -> Unit = {},
    onClickSetGoal: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickStore: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var openMultiFb by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.background(if (openMultiFb) disable else background),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.size(300.dp)
            ) {
                AnimatedVisibility(
                    visible = openMultiFb,
                    enter = slideInVertically(),
                    exit = slideOutVertically(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if (userPreference?.optionFeature?.training == true) {
                            FabItem(textRes = R.string.label_add_training) {
                                onClickAddTraining()
                                openMultiFb = openMultiFb.not()
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        if (userPreference?.optionFeature?.meal == true) {
                            FabItem(textRes = R.string.label_add_meal) {
                                onClickAddMeal()
                                openMultiFb = openMultiFb.not()
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        FabItem(textRes = R.string.label_add_measure) {
                            onClickAddMeasure()
                            openMultiFb = openMultiFb.not()
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                FloatingActionButton(
                    onClick = {
                        // オプション機能を全て切っている場合は直接体型登録を開く
                        if (
                            userPreference?.optionFeature?.meal != true &&
                            userPreference?.optionFeature?.training != true
                        ) {
                            onClickAddMeasure()
                        } else {
                            openMultiFb = openMultiFb.not()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (openMultiFb.not()) {
                            Icons.Default.Add
                        } else {
                            Icons.Default.Clear
                        },
                        contentDescription = null,
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .navigationBarsPadding()
            ) {
                AndroidView(factory = { context ->
                    val adView = AdView(context).apply {
                        adUnitId = if (BuildConfig.DEBUG) {
                            "ca-app-pub-3940256099942544/9214589741"
                        } else {
                            "ca-app-pub-2002859886618281/9421408761"
                        }
                        setAdSize(FULL_BANNER)
                    }
                    scope.launch {
                        AdRequest.Builder().build().let { adView.loadAd(it) }
                    }
                    adView
                })
                BottomSheet(bottomSheetDataList)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    if (openMultiFb) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.2F),
                            topLeft = Offset(0F, 0F),
                        )
                    }
                }
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                )
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
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
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        (lastMeasure?.tall)?.let { tall ->
                            Text(
                                text = tall.toCentiMeter(),
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                        lastMeasure?.time?.toLocalDate()?.toMMDDEE()?.let { mmdd ->
                            val label = stringResource(id = R.string.label_registered_date)
                            Text(
                                text = "$label: $mmdd",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    UpdateIcon(
                        enableUpdate = enableUpdate,
                        onClickStore = onClickStore,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                onClickSetting()
                            }
                            .size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (userPreference?.goalWeight == null && lastMeasure != null) {
                item {
                    RequireGoal(onClickSetGoal)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            } else if (lastMeasure != null && userPreference != null) {
                item {
                    Goal(
                        initialMeasure = initialMeasure,
                        lastMeasure = lastMeasure,
                        userPreference = userPreference,
                        meal = todayMeasure.meals,
                        onClickSetGoal
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
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
                PanelColumn(
                    modifier = Modifier,
                    onClick = if (openMultiFb.not()) onClickToday else null,
                ) {
                    IconAndText(
                        icon = Icons.Default.Today,
                        modifier = Modifier.padding(vertical = 5.dp),
                        text = stringResource(id = R.string.label_see_by_today),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn(
                    onClick = if (openMultiFb.not()) onClickCompare else null,
                ) {
                    IconAndText(
                        icon = Icons.Default.Compare,
                        modifier = Modifier.padding(vertical = 5.dp),
                        text = stringResource(id = R.string.label_compare),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                PanelColumn(
                    onClick = if (openMultiFb.not()) onClickPhotos else null,
                ) {
                    IconAndText(
                        icon = Icons.Default.Photo,
                        modifier = Modifier.padding(vertical = 5.dp),
                        text = stringResource(id = R.string.label_photos),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            if (userPreference?.optionFeature?.training == true) {
                item {
                    PanelColumn(
                        onClick = if (openMultiFb.not()) onClickSeeTrainingMenu else null
                    ) {
                        IconAndText(
                            icon = Icons.Default.EmojiPeople,
                            modifier = Modifier.padding(vertical = 5.dp),
                            text = stringResource(id = R.string.label_see_by_training_menu),
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
            if (userPreference?.optionFeature?.meal == true) {
                item {
                    PanelColumn(
                        onClick = if (openMultiFb.not()) onClickMeal else null
                    ) {
                        IconAndText(
                            icon = Icons.Default.SetMeal,
                            modifier = Modifier.padding(vertical = 5.dp),
                            text = stringResource(id = R.string.label_see_meal),
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun UpdateIcon(
    enableUpdate: Boolean,
    onClickStore: () -> Unit,
) {
    if (enableUpdate) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClickStore()
                }
                .background(accentColor, RoundedCornerShape(5.dp))
                .border(1.dp, Color.Blue, RoundedCornerShape(5.dp))
                .padding(vertical = 3.dp, horizontal = 6.dp),
        ) {
            Text(
                text = stringResource(id = R.string.enable_update),
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(3.dp))
            Icon(
                painter = painterResource(id = R.drawable.icons8_google_play_store),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )
        }
    } else {
        Icon(
            painter = painterResource(id = R.drawable.icons8_google_play_store),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onClickStore()
                }
                .size(20.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun FabItem(
    @StringRes textRes: Int,
    onClick: () -> Unit
) {
    Text(
        text = stringResource(id = textRes),
        modifier = Modifier
            .clickable { onClick() }
            .background(theme, RoundedCornerShape(15.dp))
            .padding(15.dp)
    )
}

@Composable
private fun Goal(
    initialMeasure: BodyMeasure?,
    lastMeasure: BodyMeasure,
    userPreference: UserPreference,
    meal: List<Meal>,
    onClickSetGoat: () -> Unit,
) {
    val startWeight = userPreference.startWeight ?: initialMeasure?.weight
    val goalWeight = checkNotNull(userPreference.goalWeight)
    PanelColumn(
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Black),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.label_start_weight),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.current_weight),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = stringResource(id = R.string.label_target_weight),
                        textAlign = TextAlign.Center,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Black),
                ) {
                    val displayStartWeight = if (startWeight != null) {
                        "$startWeight kg"
                    } else {
                        "未設定"
                    }
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = displayStartWeight,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = "${lastMeasure.weight} kg",
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .border(0.5.dp, Color.Black),
                        text = "$goalWeight kg",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        startWeight?.let {
            Spacer(modifier = Modifier.size(10.dp))
            Diff(
                label = stringResource(id = R.string.label_from_start),
                standard = lastMeasure.weight,
                current = it,
                isFromStart = true,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Diff(
                label = stringResource(id = R.string.label_until_object),
                standard = lastMeasure.weight,
                current = goalWeight,
                isFromStart = false,
            )
        }

        if (userPreference.optionFeature.meal == true && userPreference.goalKcal != null) {
            Spacer(modifier = Modifier.size(10.dp))
            HorizontalLine()
            val totalKcal = meal.sumOf { it.totalKcal }

            Text(
                text = stringResource(id = R.string.kcal_today) + " $totalKcal / ${userPreference.goalKcal} kcal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(12.dp))
            LinearProgressIndicator(
                progress = userPreference.progressKcal(meal.sumOf { it.totalKcal }),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = Color(0xFF00CC00),
                backgroundColor = Color.LightGray,
                strokeCap = StrokeCap.Round
            )
        }
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
fun Dp.toPx(): Float {
    val metrics = LocalContext.current.resources.displayMetrics
    return this.value * (metrics.densityDpi / 160f)
}

@Composable
private fun Diff(
    label: String,
    standard: Float,
    current: Float,
    isFromStart: Boolean,
) {
    val diff = ((standard - current) * 100).toInt() / 100F
    val color = if (diff == 0F) {
        Color.Black
    } else if (diff > 0) {
        Color.Red
    } else {
        Color.Blue
    }

    val plusMinus = if (diff == 0F) {
        "±"
    } else if (diff > 0) {
        "+"
    } else {
        "-"
    }
    val yOffsetDp = 2.dp
    val yOffsetPx = yOffsetDp.toPx()

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = label,
            modifier = Modifier
                .offset(y = yOffsetDp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = plusMinus,
            color = color,
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "${diff.absoluteValue} kg",
            color = color,
            fontSize = 16.sp,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = color,
                    start = Offset(-30F, size.height + yOffsetPx),
                    end = Offset(size.width + 10F, size.height + yOffsetPx),
                    strokeWidth = 2F
                )
            }
        )
        if (isFromStart) {
            Spacer(modifier = Modifier.size(10.dp))
            val text = if (diff.toInt() == 0) {
                ""
            } else if (diff > 0) {
                stringResource(id = R.string.increase)
            } else {
                stringResource(id = R.string.decrease)
            }
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                modifier = Modifier.offset(y = yOffsetDp),
            )
        }
        if (isFromStart.not() && diff <= 0F) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "\uD83C\uDF89")
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
    withArrow: Boolean = true,
    message: String? = null,
    subTitle: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
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
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onClick?.invoke() }
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = horizontalAlignment
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
