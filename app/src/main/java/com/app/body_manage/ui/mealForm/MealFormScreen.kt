package com.app.body_manage.ui.mealForm

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.common.CustomImage
import com.app.body_manage.common.toKcal
import com.app.body_manage.common.toNumber
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.model.Food.Companion.NEW_ID
import com.app.body_manage.data.model.Meal
import com.app.body_manage.data.model.Photo
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.style.Colors
import com.app.body_manage.ui.top.TextWithUnderLine

@Composable
fun MealFormScreen(
    type: MealFormViewModel.Type,
    mealFoods: Meal,
    foodCandidates: List<Food>,
    onClickMealTiming: (Meal.Timing) -> Unit,
    onClickTime: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onClickSearchedFood: (Food) -> Unit,
    onClickBackPress: () -> Unit,
    onClickSave: () -> Unit,
    onClickDeleteFood: (Food) -> Unit,
    onClickTakePhoto: () -> Unit,
    onClickDeleteForm: () -> Unit,
    onUpdateMealKcal: (Food) -> Unit,
    onUpdateMealNumber: (Food) -> Unit,
    onClickDeletePhoto: (Photo) -> Unit,
    onClickPhotoDetail: (Photo) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .background(Colors.background)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
            ),
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                modifier = Modifier
                    .background(colorResource(id = R.color.app_theme))
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    ),
                backgroundColor = Colors.theme
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                    Text(
                        text = mealFoods.time.toLocalDate().toMMDDEE(),
                        modifier = Modifier.offset(x = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (type == MealFormViewModel.Type.Edit) {
                        Spacer(modifier = Modifier.weight(1F))
                        CustomButton(
                            onClick = onClickDeleteForm,
                            valueResourceId = R.string.delete,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.padding(it)) {
                Column(
                    Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    SelectMealTiming(
                        mealFoods,
                        onClickMealTiming,
                        onClickTime,
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    if (mealFoods.foods.isNotEmpty()) {
                        EatenFoods(
                            mealFoods,
                            onClickDeleteFood,
                            onUpdateMealKcal = onUpdateMealKcal,
                            onUpdateMealNumber = onUpdateMealNumber,
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                    Photos(
                        mealFoods.photos,
                        onClickDeletePhoto = onClickDeletePhoto,
                        onClickPhotoDetail = onClickPhotoDetail,
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
            }
            Column {
                TextWithCandidate(
                    type = type,
                    onValueChange = onSearchTextChange,
                    onClickSearchedFood = onClickSearchedFood,
                    candidates = foodCandidates,
                )
                SaveForm(
                    onClickSave = onClickSave,
                    onClickTakePhoto = onClickTakePhoto,
                    enable = mealFoods.foods.isNotEmpty(),
                )
            }
        }
    }
}

@Composable
private fun Photos(
    photos: List<Photo>,
    onClickPhotoDetail: (Photo) -> Unit,
    onClickDeletePhoto: (Photo) -> Unit,
) {
    if (photos.isNotEmpty()) {
        Column(
            modifier = Modifier
                .shadow(2.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(5.dp)
                )
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(text = "写真")
            Spacer(modifier = Modifier.size(5.dp))
            LazyRow {
                itemsIndexed(photos) { _, photo ->
                    CustomImage(
                        photo = photo,
                        onClickPhotoDetail = onClickPhotoDetail,
                        onClickDeletePhoto = onClickDeletePhoto,
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }
    }
}

@Composable
private fun SelectMealTiming(
    meal: Meal,
    onClickMealTiming: (Meal.Timing) -> Unit,
    onClickTime: () -> Unit
) {
    var mealTimingMenuExpanded by remember { mutableStateOf(false) }
    val mealTimingMenuItems = Meal.Timing.entries.toList()

    Row(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconAndText(
            text = stringResource(meal.timing.textResourceId),
            onClick = {
                mealTimingMenuExpanded = true
            },
        )
        Spacer(modifier = Modifier.size(20.dp))
        DropdownMenu(
            expanded = mealTimingMenuExpanded,
            onDismissRequest = { mealTimingMenuExpanded = false }
        ) {
            mealTimingMenuItems.forEach { timing ->
                DropdownMenuItem(onClick = {
                    onClickMealTiming(timing)
                    mealTimingMenuExpanded = false
                }) {
                    Text(text = stringResource(id = timing.textResourceId))
                }
            }
        }
        TextWithUnderLine(
            text = meal.time.toJapaneseTime(),
            modifier = Modifier.clickable { onClickTime() },
        )
    }
}

@Composable
private fun EatenFoods(
    meal: Meal,
    onClickDeleteFood: (Food) -> Unit,
    onUpdateMealKcal: (Food) -> Unit,
    onUpdateMealNumber: (Food) -> Unit,
) {
    val foods = meal.foods
    Column(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.label_all_kcal),
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = meal.totalKcal.toKcal()
            )
        }
        Divider(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
        )
        foods.forEachIndexed { index, food ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 削除ボタン
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.clickable { onClickDeleteFood(food) }
                )
                Spacer(modifier = Modifier.size(10.dp))
                // 食べ物の名前
                Text(
                    text = food.name,
                    style = TextStyle(lineBreak = LineBreak.Simple),
                    modifier = Modifier.widthIn(min = 100.dp, max = 150.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.weight(1F))
                // 個
                TextWithUnderLine(
                    text = food.number.toInt().toNumber(),
                    modifier = Modifier.clickable {
                        onUpdateMealNumber(food)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                // カロリー
                TextWithUnderLine(
                    text = food.kcal.toKcal(),
                    modifier = Modifier.clickable {
                        onUpdateMealKcal(food)
                    }
                )
            }
            if (index != foods.lastIndex) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
private fun TextWithCandidate(
    onValueChange: (String) -> Unit,
    onClickSearchedFood: (Food) -> Unit,
    candidates: List<Food>,
    type: MealFormViewModel.Type
) {
    val focusRequester = remember { FocusRequester() }
    if (type == MealFormViewModel.Type.Add) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    var searchText by remember { mutableStateOf("") }
    Column(modifier = Modifier.imePadding()) {
        val roundedConnerShape = RoundedCornerShape(
            topStart = CornerSize(10.dp),
            topEnd = CornerSize(10.dp),
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        )
        LazyColumn(
            modifier = Modifier
                .offset(0.dp, 0.5.dp)
                .fillMaxWidth(1F)
                .shadow(0.5.dp, roundedConnerShape)
                .border(0.5.dp, Color.LightGray, roundedConnerShape)
                .background(Color.White, roundedConnerShape)
        ) {
            if (candidates.isNotEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = stringResource(R.string.label_result_of_search),
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .padding(top = 15.dp)
                        )
                    }
                }
            }
            itemsIndexed(candidates) { _, candidate ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onClickSearchedFood(candidate)
                            searchText = ""
                            onValueChange("")
                        }
                        .height(50.dp)
                        .padding(vertical = 5.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val foodName = if (candidate.id == NEW_ID) {
                        candidate.name
                    } else {
                        candidate.name + " (${candidate.kcal.toKcal()})"
                    }
                    Text(
                        text = foodName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.9F),
                    )

                    val textResource = if (candidate.id == NEW_ID) {
                        R.string.message_save_and_add
                    } else {
                        R.string.message_add_food
                    }
                    Text(
                        text = stringResource(id = textResource),
                        fontSize = 11.sp
                    )
                }
            }
        }
        BasicTextField(
            value = searchText,
            textStyle = TextStyle(
                lineHeight = 150.sp
            ),
            modifier = Modifier
                .background(Color.White)
                .offset(0.dp, 0.5.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            onValueChange = {
                searchText = it.trim()
                onValueChange(it)
            },
            decorationBox = @Composable { innerTextField ->
                Box(
                    modifier = Modifier
                        .border(0.5.dp, Color.LightGray)
                        .padding(10.dp),
                ) {
                    if (searchText.isBlank()) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.message_search_and_add),
                                color = Color.Gray,
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Box(modifier = Modifier.weight(0.8F)) {
                            innerTextField()
                        }
                        if (searchText.isNotBlank()) {
                            Row(modifier = Modifier.weight(0.2F)) {
                                Spacer(modifier = Modifier.weight(1F))
                                Icon(
                                    imageVector = Icons.Sharp.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        searchText = ""
                                        onValueChange("")
                                    }
                                )
                            }
                        }
                    }
                }
            },
            singleLine = true,
        )
    }
}

@Composable
private fun SaveForm(
    onClickSave: () -> Unit,
    onClickTakePhoto: () -> Unit,
    enable: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Colors.background)
            .shadow(0.5.dp, clip = true),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1F))
            CustomButton(
                onClickSave,
                R.string.save,
                backgroundColor = Colors.theme,
                enable = enable
            )
            Spacer(modifier = Modifier.size(20.dp))
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = null,
                modifier = Modifier.clickable { onClickTakePhoto() }
            )
        }
    }
}

@Composable
private fun IconAndText(
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 7.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(5.dp))
        Column {
            Text(text = text)
        }
    }
}
