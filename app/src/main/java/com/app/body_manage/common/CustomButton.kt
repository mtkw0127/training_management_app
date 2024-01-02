package com.app.body_manage.common

import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CustomButton(
    onClick: () -> Unit,
    @StringRes valueResourceId: Int,
    backgroundColor: Color = Color.White,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        )
    ) {
        Text(
            text = stringResource(id = valueResourceId),
            textAlign = TextAlign.Center,
        )
    }
}