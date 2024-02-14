package io.dock2dock.android.ui.components

import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb
import io.dock2dock.android.ui.theme.PrimaryPlatinum

@Composable
fun Dock2DockSwitch(checked: Boolean,
                    checkedChanged: ((Boolean) -> Unit) = {}) {
    Switch(
        checked = checked,
        onCheckedChange = {
            checkedChanged(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = PrimaryOrangeWeb,
            checkedTrackColor = Color.DarkGray,
            uncheckedThumbColor = PrimaryPlatinum,
            uncheckedTrackColor = Color.DarkGray,
        ),
    )
}

@Preview
@Composable
fun Dock2DockSwitchPreview() {
    var isChecked: Boolean by remember { mutableStateOf(false) }
    Dock2DockSwitch(checked = isChecked, checkedChanged = {
        isChecked = it
    })
}