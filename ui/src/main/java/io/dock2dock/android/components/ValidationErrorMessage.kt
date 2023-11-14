package io.dock2dock.android.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ValidationErrorMessage(errorMessage: String) {
    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
}