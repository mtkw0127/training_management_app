package com.app.body_manage.ui.trainingMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ChipDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.style.Colors
import com.app.body_manage.ui.common.LabelAndContentRow
import com.app.body_manage.ui.common.TrainingMenuItem
import com.app.body_manage.ui.top.TextWithUnderLine

@Composable
fun TrainingMenuListScreen(
    selectedPart: TrainingMenu.Part? = null, // nullは全てと判定する
    selectedType: TrainingMenu.Type? = null, // nullは全てと判定する
    trainingMenus: List<TrainingMenu>,
    onClickBackPress: () -> Unit = {},
    onEditMenu: (TrainingMenuEntity) -> Unit = {},
    onSaveMenu: (TrainingMenuEntity) -> Unit = {},
    onClickPart: (TrainingMenu.Part?) -> Unit = {},
    onClickType: (TrainingMenu.Type?) -> Unit = {},
) {
    val isEditDialogOpening = rememberSaveable { mutableStateOf(false) }
    val isOpenAddDialog = rememberSaveable { mutableStateOf(false) }

    val isTypeDropDownMenuOpening = rememberSaveable { mutableStateOf(false) }
    val isPartDropDownMenuOpening = rememberSaveable { mutableStateOf(false) }
    val id = rememberSaveable { mutableLongStateOf(0L) }
    val memo = rememberSaveable { mutableStateOf("") }
    val trainingName = rememberSaveable { mutableStateOf("") }
    val type = rememberSaveable { mutableStateOf(TrainingMenu.Type.FREE) }
    val part = rememberSaveable { mutableStateOf(TrainingMenu.Part.ARM) }

    if (isOpenAddDialog.value || isEditDialogOpening.value) {
        Dialog(
            onDismissRequest = {
                isOpenAddDialog.value = false
                isEditDialogOpening.value = false
            },
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(10.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    LabelAndContentRow(
                        modifier = Modifier.width(60.dp),
                        label = R.string.label_menu_type_name
                    ) {
                        Spacer(modifier = Modifier.size(10.dp))
                        TextField(
                            value = trainingName.value,
                            onValueChange = {
                                trainingName.value = it
                            },
                        )
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Box {
                        LabelAndContentRow(
                            modifier = Modifier.width(60.dp),
                            label = R.string.label_training_target_part
                        ) {
                            Spacer(modifier = Modifier.size(10.dp))
                            TextWithUnderLine(
                                stringResourceId = part.value.nameStringResourceId,
                                modifier = Modifier.clickable {
                                    isPartDropDownMenuOpening.value =
                                        isPartDropDownMenuOpening.value.not()
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = isPartDropDownMenuOpening.value,
                            onDismissRequest = {
                                isPartDropDownMenuOpening.value =
                                    isPartDropDownMenuOpening.value.not()
                            }
                        ) {
                            TrainingMenu.Part.entries.forEach { partItem ->
                                DropdownMenuItem(
                                    onClick = {
                                        part.value = partItem
                                        isPartDropDownMenuOpening.value = false
                                    }
                                ) {
                                    Text(stringResource(partItem.nameStringResourceId))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Box {
                        LabelAndContentRow(
                            modifier = Modifier.width(60.dp),
                            label = R.string.label_type
                        ) {
                            Spacer(modifier = Modifier.size(10.dp))
                            TextWithUnderLine(
                                stringResourceId = type.value.nameStringRes,
                                modifier = Modifier.clickable {
                                    isTypeDropDownMenuOpening.value =
                                        isPartDropDownMenuOpening.value.not()
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = isTypeDropDownMenuOpening.value,
                            onDismissRequest = {
                                isTypeDropDownMenuOpening.value =
                                    isTypeDropDownMenuOpening.value.not()
                            }
                        ) {
                            TrainingMenu.Type.entries.forEach { typeItem ->
                                DropdownMenuItem(
                                    onClick = {
                                        type.value = typeItem
                                        isTypeDropDownMenuOpening.value = false
                                    }
                                ) {
                                    Text(stringResource(typeItem.nameStringRes))
                                }
                            }
                        }
                    }
                }
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomButton(
                        onClick = {
                            if (isOpenAddDialog.value) {
                                onSaveMenu(
                                    TrainingMenuEntity(
                                        id = 0,
                                        name = trainingName.value,
                                        part = part.value.index,
                                        type = type.value.index,
                                        memo = "",
                                    )
                                )
                            } else {
                                onEditMenu(
                                    TrainingMenuEntity(
                                        id = id.longValue,
                                        name = trainingName.value,
                                        part = part.value.index,
                                        type = type.value.index,
                                        memo = memo.value,
                                    )
                                )
                            }
                            isOpenAddDialog.value = false
                            isEditDialogOpening.value = false
                        },
                        enable = trainingName.value.isNotEmpty(),
                        valueResourceId = if (isOpenAddDialog.value) {
                            R.string.label_register_training
                        } else {
                            R.string.label_update_training
                        },
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Colors.theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isOpenAddDialog.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(10.dp)
        ) {
            LazyColumn {
                item {
                    Filters(
                        selectedPart = selectedPart,
                        selectedType = selectedType,
                        onClickPart = onClickPart,
                        onClickType = onClickType,
                    )
                }
                items(trainingMenus) { menu ->
                    TrainingMenuItem(
                        trainingMenu = menu,
                        onClick = {
                            isEditDialogOpening.value = true
                            id.longValue = it.id.value
                            memo.value = it.memo
                            trainingName.value = it.name
                            type.value = it.type
                            part.value = it.part
                        }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun Filters(
    onClickPart: (TrainingMenu.Part?) -> Unit,
    onClickType: (TrainingMenu.Type?) -> Unit,
    selectedPart: TrainingMenu.Part?,
    selectedType: TrainingMenu.Type?,
) {
    Row {
        DropDownFilterChipForPart(
            selectedPart = selectedPart,
            onClickPart = onClickPart,
        )

        Spacer(modifier = Modifier.size(10.dp))

        DropDownFilterChipForType(
            selectedType = selectedType,
            onClickType = onClickType,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropDownFilterChipForType(
    selectedType: TrainingMenu.Type?,
    onClickType: (TrainingMenu.Type?) -> Unit,
) {
    val allAddedMenus = listOf(null) + TrainingMenu.Type.entries
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        FilterChip(
            enabled = true,
            selected = true,
            onClick = { expanded = true },
            colors = ChipDefaults.filterChipColors(
                selectedBackgroundColor = Colors.theme,
            ),
        ) {
            Text(
                text = stringResource(
                    id = R.string.label_training_target_type_selected,
                    if (selectedType != null) {
                        stringResource(selectedType.nameStringRes)
                    } else {
                        stringResource(R.string.label_all)
                    }
                ),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allAddedMenus.forEach { menu ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onClickType(menu)
                    }
                ) {
                    Text(
                        text = if (menu != null) {
                            stringResource(menu.nameStringRes)
                        } else {
                            stringResource(R.string.label_all)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropDownFilterChipForPart(
    selectedPart: TrainingMenu.Part?,
    onClickPart: (TrainingMenu.Part?) -> Unit,
) {
    val allAddedMenus = listOf(null) + TrainingMenu.Part.entries
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        FilterChip(
            enabled = true,
            selected = true,
            onClick = { expanded = true },
            colors = ChipDefaults.filterChipColors(
                selectedBackgroundColor = Colors.theme,
            ),
        ) {
            Text(
                text = stringResource(
                    id = R.string.label_training_target_part_selected,
                    if (selectedPart != null) {
                        stringResource(selectedPart.nameStringResourceId)
                    } else {
                        stringResource(R.string.label_all)
                    },
                ),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allAddedMenus.forEach { menu ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onClickPart(menu)
                    }
                ) {
                    Text(
                        text = if (menu != null) {
                            stringResource(menu.nameStringResourceId)
                        } else {
                            stringResource(R.string.label_all)
                        },
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun TrainingMenuListScreenPreview() {
    TrainingMenuListScreen(
        trainingMenus =
        List(3) { eventIndex ->
            createSampleTrainingMenu(eventIndex.toLong())
        } + List(3) { eventIndex ->
            createSampleOwnWeightTrainingMenu(eventIndex.toLong() + 2)
        }
    )
}
