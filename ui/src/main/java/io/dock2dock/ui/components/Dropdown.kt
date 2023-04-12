package io.dock2dock.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
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
import io.dock2dock.ui.ui.theme.PrimaryDark
import io.dock2dock.ui.ui.theme.PrimaryOxfordBlue
import io.dock2dock.ui.views.NoRecordsFoundError

@Composable
fun <T> FluentDropdown(options: List<T>,
                       valueChanged: ((T) -> Unit)? = {},
                       selectedTextExpression: (T) -> String,
                       selectedOption: String,
                       errorMessage: String? = null,
                       isError: Boolean = false,
                       itemTemplate: @Composable (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    var textFieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {
                selectedText = it
                            },
            modifier = Modifier
                .defaultMinSize(minHeight = 32.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            shape = RoundedCornerShape(0),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryDark,
                focusedBorderColor = PrimaryOxfordBlue
            ),
            isError = isError,
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
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
                        selectedText = selectedTextExpression(label)
                        if (valueChanged != null) {
                            valueChanged(label)
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
    FluentDropdown(
        options = listOf("Kotlin", "Java", "C#", "Swift"),
        selectedTextExpression =  { it -> it },
        selectedOption =  "Kotlin")
        {
        BasicDropdownMenuItem(text = it)

    }

}

@Preview(showBackground = true)
@Composable
fun FluentSubtitleDropdownPreview() {
    SubTitleDropdownMenuItem("TSC DA220", "Warehouse")
}
