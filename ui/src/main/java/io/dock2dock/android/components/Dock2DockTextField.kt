package io.dock2dock.android.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        ),
        readOnly = readOnly,
        isError = isError,
        placeholder = { Text(placeholderText) })

    if (isError) {
        ValidationErrorMessage(errorMessage = errorMessage ?: "")
    }

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