package io.dock2dock.android.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue

@Composable
fun ErrorDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = { onConfirm() }, colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryOxfordBlue)) {
                    Text(text = "Ok", color = Color.White)
                }
            },
            title = { Text(text = "Error!") },
            text = { Text(text = message) }
        )
    }
}