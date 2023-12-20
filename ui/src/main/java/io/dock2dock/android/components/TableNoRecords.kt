package io.dock2dock.android.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun TableNoRecords() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(all = 20.dp),
        textAlign = TextAlign.Center,
        text = "No Records Found!")
}