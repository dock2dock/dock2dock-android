package io.dock2dock.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb

@Composable
fun Dock2DockCheckbox(checked: Boolean,
                      disabled: Boolean = false,
                    checkedChanged: ((Boolean) -> Unit) = {}) {
    Checkbox(
        checked = checked,
        onCheckedChange = {
            checkedChanged(it)
        },
        enabled = !disabled,
        colors = CheckboxDefaults.colors(
            checkedColor = PrimaryOrangeWeb,
            uncheckedColor = PrimaryDark,
            checkmarkColor = PrimaryOrangeWeb,
        ),
    )
}

@Preview
@Composable
fun Dock2DockCheckboxPreview() {
    var isChecked: Boolean by remember { mutableStateOf(false) }
    Box(Modifier.background(color = Color.Gray)) {
        Dock2DockCheckbox(checked = isChecked, checkedChanged = { isChecked = it })
    }
}