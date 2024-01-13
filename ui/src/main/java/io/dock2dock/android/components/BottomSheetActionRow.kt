package io.dock2dock.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetActionRow(contentColor: Color = Color.Black, name: String, imageVector: ImageVector, onclick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onclick()
        }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Icon(
                imageVector = imageVector,
                contentDescription = "contentDescription"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = name)
        }

    }
}