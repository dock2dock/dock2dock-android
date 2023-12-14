package io.dock2dock.android.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TableRowDivider() {
    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
}