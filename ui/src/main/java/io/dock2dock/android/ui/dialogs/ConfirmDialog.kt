package io.dock2dock.android.ui.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue

@Composable
fun ConfirmDialog(
    confirmText: String,
    cancelText: String,
    title: String,
    contentText: String,
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

   if (show) {
       AlertDialog(
           onDismissRequest = { onDismiss() },
           title = { Text(title) },
           text = { Text(contentText) },
           confirmButton = {
               TextButton(onClick = { onConfirm() }, colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryOxfordBlue)) {
                   Text(text = confirmText, color = Color.White)
               }
           },
           dismissButton = {
               TextButton(onClick = { onDismiss() }) {
                   Text(cancelText, color = PrimaryOxfordBlue)
               }
           }
       )
   }
}