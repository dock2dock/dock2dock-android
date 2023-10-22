package io.dock2dock.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormItem(title: String, content: @Composable () -> Unit) {
    Column( modifier = Modifier
        .padding(0.dp, 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title,
            fontSize = 12.sp
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun FormItemPreview() {
    FormItem("Sales Order Id") {
        Dock2DockTextField(
            value = "12345",
            placeholderText = "Sales Order Id"
        )
    }
}