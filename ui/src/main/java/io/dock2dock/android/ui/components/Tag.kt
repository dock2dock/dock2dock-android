package io.dock2dock.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Tag(modifier: Modifier = Modifier,
        text: String,
        color: Color,
        fontSize: TextUnit = 11.sp) {
    Text(
        modifier = modifier
            .background(color, RectangleShape)
            .padding(3.dp),
        textAlign = TextAlign.End,
        text = text,
        style = TextStyle(fontSize = fontSize),
        color = Color.White)
}