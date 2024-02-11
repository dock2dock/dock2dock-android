package io.dock2dock.android.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Dock2DockDialogHeader(title: String, close: (() -> Unit)) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 0.dp, 0.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        IconButton(onClick = {
            close()
        }, modifier = Modifier.size(24.dp)) {
            Icon(
                Icons.Filled.Clear,
                "contentDescription"
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight(400),
            fontSize = 18.sp,
            style = TextStyle(lineHeight = 32.sp),
        )

    }
}