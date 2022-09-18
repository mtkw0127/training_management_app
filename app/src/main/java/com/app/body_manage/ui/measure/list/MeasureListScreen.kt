package com.app.body_manage.ui.measure.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.common.Calendar
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.entity.BodyMeasureModel
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.style.Colors
import com.app.body_manage.util.DateUtil
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MeasureListScreen(
    uiState: MeasureListState,
    clickSaveBodyInfo: () -> Unit,
    bottomSheetDataList: List<BottomSheetData>,
    setTall: (String) -> Unit,
    resetSnackBarMessage: () -> Unit,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit,
    clickFab: () -> Unit,
    showPhotoDetail: (Int) -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    var showCalendar by remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBar(backgroundColor = Colors.theme) {
                Text(
                    text = DateUtil.localDateConvertJapaneseFormatYearMonthDay(uiState.date),
                    modifier = Modifier
                        .offset(x = 20.dp)
                        .clickable {
                            showCalendar = true
                        },
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        },
        content = { padding ->
            Column {
                if (showCalendar) {
                    Calendar(
                        onClickDate = {
                            showCalendar = false
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(top = 10.dp)
                        .fillMaxHeight()
                ) {
                    when (uiState) {
                        is MeasureListState.BodyMeasureListState -> {
                            if (uiState.message.isNotEmpty()) {
                                LaunchedEffect(uiState.message) {
                                    coroutineScope.launch {
                                        state.snackbarHostState.showSnackbar(
                                            message = uiState.message,
                                            duration = SnackbarDuration.Short
                                        )
                                        resetSnackBarMessage.invoke()
                                    }
                                }
                            }
                            if (uiState.list.isNotEmpty()) {
                                TallSetField(
                                    tall = uiState.tall,
                                    setTall = setTall,
                                    clickSaveBodyInfo = clickSaveBodyInfo,
                                )
                                Divider(modifier = Modifier.padding(12.dp))
                                BodyMeasureList(
                                    list = uiState.list,
                                    clickBodyMeasureEdit = clickBodyMeasureEdit,
                                )
                                if (uiState.photoList.isNotEmpty()) {
                                    PhotoList(uiState.photoList, clickPhoto = showPhotoDetail)
                                }
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "未登録です\n右下のボタンから登録してください",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = clickFab) {
                Icon(Icons.Filled.Add, contentDescription = "体型登録")
            }
        },
        bottomBar = {
            BottomSheet(bottomSheetDataList)
        }
    )
}

@Composable
private fun PhotoList(photoList: List<BodyMeasurePhotoDao.PhotoData>, clickPhoto: (Int) -> Unit) {
    Text(
        text = "この日撮影した写真",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 12.dp, bottom = 10.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.95F),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyRow {
            items(photoList) {
                AsyncImage(
                    model = it.photoUri,
                    contentDescription = "当日の写真一覧",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .padding(3.dp)
                        .width(200.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable {
                            clickPhoto.invoke(it.photoId)
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TallSetField(
    tall: String,
    setTall: (String) -> Unit,
    clickSaveBodyInfo: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(start = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                Text(text = "身長[cm]")
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                TextField(
                    value = tall,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        if (it.toDoubleOrNull() != null ||
                            it.startsWith("0")
                                .not()
                        ) {
                            setTall.invoke(it)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(48.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Button(onClick = {
                    keyboardController?.hide()
                    clickSaveBodyInfo.invoke()
                }) {
                    Text(text = "保存")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BodyMeasureList(
    list: List<BodyMeasureModel>,
    clickBodyMeasureEdit: (LocalDateTime) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentWidth()
            .heightIn(min = 200.dp, max = 400.dp),
        content = {
            stickyHeader {
                Row {
                    DisplayMeasureColumn.values().forEach {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1F)
                                .background(Color.White)
                                .padding(
                                    start = 3.dp,
                                    end = 3.dp,
                                    bottom = 3.dp,
                                ),
                        ) {
                            Text(
                                text = it.display,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
            items(list) { item ->
                Row(
                    Modifier
                        .wrapContentHeight()
                        .padding(top = 3.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.capturedLocalDateTime.toJapaneseTime(),
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.weight.toString() + "Kg",
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.fat.toString() + "%",
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = item.bmi,
                        )
                    }
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "体型登録",
                        modifier = Modifier
                            .weight(1F)
                            .padding(3.dp)
                            .clickable {
                                clickBodyMeasureEdit.invoke(
                                    item.capturedLocalDateTime
                                )
                            },
                        tint = Color.Gray,
                    )
                }
            }
        },
    )
}