package io.dock2dock.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite
import io.dock2dock.android.ui.views.NoRecordsFoundError

@Composable
fun <T> Dock2DockDropdown(modifier: Modifier = Modifier,
                          options: List<T>,
                          selectedItemChanged: ((T) -> Unit)? = {},
                          selectedTextExpression: (T) -> String,
                          selectedText: String = "",
                          darkTheme: Boolean = false,
                          errorMessage: String? = null,
                          isError: Boolean = false,
                          placeholderText: String = "",
                          itemTemplate: @Composable (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    var selectedRememberText by remember { mutableStateOf(selectedText) }

    var textFieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val unfocusedBorderColor: Color = if (darkTheme)
        PrimaryWhite
    else
        PrimaryDark

    val focusedBorderColor: Color = if (darkTheme)
        PrimaryWhite
    else
        PrimaryOxfordBlue

    val textColor: Color = if (darkTheme)
        PrimaryWhite
    else
        PrimaryDark

    fun onExpand() {
        expanded = !expanded
    }

    Column {
        Dock2DockOutlinedTextField(
            value = selectedText,
            onValueChange = {
                selectedRememberText = it
            },
            modifier = modifier
                .defaultMinSize(minHeight = 20.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            shape = RoundedCornerShape(0),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = unfocusedBorderColor,
                focusedBorderColor = focusedBorderColor,
                textColor = textColor,
                trailingIconColor = textColor,
                placeholderColor = textColor
            ),
            singleLine = true,
            placeholder = {
                Text(placeholderText)
            },
            readOnly = true,
            isError = isError,
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { onExpand() })
            }
        )
        if (isError) {
            ValidationErrorMessage(errorMessage = errorMessage ?: "")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            if (options.any()) {
                options.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedRememberText = selectedTextExpression(label)
                        if (selectedItemChanged != null) {
                            selectedItemChanged(label)
                        }
                        expanded = false
                    }) {
                        itemTemplate(label)
                    }
                }
            } else
            {
                NoRecordsFoundError("No menu items found. Please create new items in the dashboard portal.",
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun BasicDropdownMenuItem(text: String) {
    Text(text = text)
}

@Composable
fun SubTitleDropdownMenuItem(title: String, subTitle: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title)
        if (!subTitle.isNullOrEmpty()) {
            Text(subTitle,
                fontSize = 12.sp,
                style = TextStyle(color = Color.DarkGray),
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FluentDropdownPreview() {
    var selectedText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
        TextField(value = selectedText, onValueChange = {
            selectedText = it
        })
        Dock2DockDropdown(
            options = listOf("Kotlin", "Java", "C#", "Swift"),
            selectedTextExpression = { it -> it },
            selectedText = selectedText,
        )
        {
            BasicDropdownMenuItem(text = it)

        }
    }

}

@Preview(showBackground = true)
@Composable
fun FluentSubtitleDropdownPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
        SubTitleDropdownMenuItem("TSC DA220", "Warehouse")
        SubTitleDropdownMenuItem("TSC DA220", "Warehouse")
        SubTitleDropdownMenuItem("TSC DA220", "Warehouse")
    }
}
