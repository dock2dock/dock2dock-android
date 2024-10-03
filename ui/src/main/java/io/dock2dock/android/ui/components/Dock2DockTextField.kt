package io.dock2dock.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.dock2dock.android.ui.extensions.toIntString
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Dock2DockTextField(
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    valueChanged: ((String) -> Unit) = {},
    placeholderText: String = "") {

    val cursorColor: Color = if (keyboardType == KeyboardType.Number) {
        Color.Transparent
    } else {
        Color.Black
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Dock2DockOutlinedTextField(value = value,
        onValueChange = {
            valueChanged(it)
        },
        modifier = Modifier.defaultMinSize(minHeight = 32.dp).fillMaxWidth(),
        shape = RoundedCornerShape(0),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Dock2DockTextArea(
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    valueChanged: ((String) -> Unit) = {},
    placeholderText: String = "") {

    val cursorColor: Color = if (keyboardType == KeyboardType.Number) {
        Color.Transparent
    } else {
        Color.Black
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Dock2DockOutlinedTextField(value = value,
        onValueChange = {
            valueChanged(it)
        },
        modifier = Modifier
            .defaultMinSize(minHeight = 32.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(0),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        singleLine = false,
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

@Composable
fun Dock2DockOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    @OptIn(ExperimentalMaterialApi::class)
    BasicTextField(
        value = value,
        modifier = if (label != null) {
            modifier.padding(top = 8.dp)
        } else {
            modifier
        }
            .background(colors.backgroundColor(enabled).value, shape)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = 32.dp
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                border = {
                    TextFieldDefaults.BorderBox(
                        enabled,
                        isError,
                        interactionSource,
                        colors,
                        shape
                    )
                },
                contentPadding = PaddingValues(8.dp)
            )
        }
    )
}

@Preview(showBackground = true,
    backgroundColor = 0xFFFFFF,
    widthDp = 200)
@Composable
fun Dock2DockOutlinedTextFieldPreview() {
    Box(Modifier.padding(8.dp)) {
        Dock2DockOutlinedTextField(value = "--none selected--", {},
            trailingIcon = {
            Icon(
                Icons.Filled.KeyboardArrowUp, "contentDescription",
                Modifier.clickable {  })
        })
    }
}