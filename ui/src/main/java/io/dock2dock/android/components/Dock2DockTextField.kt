package io.dock2dock.android.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.dock2dock.android.extensions.toIntString
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue

@Composable
fun Dock2DockTextField(
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    valueChanged: ((String) -> Unit) = {},
    placeholderText: String = "") {

    var cursorColor: Color = if (keyboardType == KeyboardType.Number) {
        Color.Transparent
    } else {
        Color.Black
    }

    OutlinedTextField(value = value,
        onValueChange = {
            valueChanged(it)
        },
        modifier = Modifier.defaultMinSize(minHeight = 32.dp),
        shape = RoundedCornerShape(0),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = PrimaryDark,
            focusedBorderColor = PrimaryOxfordBlue,
            cursorColor = cursorColor
        ),
        readOnly = readOnly,
        isError = isError,
        placeholder = { Text(placeholderText) })

    if (isError) {
        ValidationErrorMessage(errorMessage = errorMessage ?: "")
    }

}

@Composable
fun Dock2DockNumberTextField(
    value: Int,
    errorMessage: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    valueChanged: ((Int) -> Unit) = {},
    placeholderText: String = "") {

    var text by remember(value) { mutableStateOf(value.toIntString()) }

    Dock2DockTextField(
        value = text,
        valueChanged = {
            val parsed = it.toIntOrNull() ?: 0
            text = if (it.isNullOrEmpty()) {
                ""
            } else {
                parsed.toString()
            }
            valueChanged(parsed)
        },
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Number,
        readOnly = readOnly,
        isError = isError,
        placeholderText = placeholderText)
}

@Composable
fun ValidationErrorMessage(errorMessage: String) {
    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
}

@Preview(showBackground = true,
    backgroundColor = 0xFFF,
    widthDp = 200,
    heightDp = 60)
@Composable
fun TextFieldPreview() {
    Dock2DockTextField(value = "", placeholderText = "Show us this")
}

@Preview(showBackground = true,
    backgroundColor = 0xFFFFFF,
    widthDp = 200,
    heightDp = 60)
@Composable
fun NumberTextFieldPreview() {
    Dock2DockNumberTextField(value = 0, placeholderText = "Quantity")
}