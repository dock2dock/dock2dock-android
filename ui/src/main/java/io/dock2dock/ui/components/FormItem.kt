package io.dock2dock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Composable
fun FormItem(title: String, content: @Composable () -> Unit) {
    Column( modifier = Modifier
        .padding(0.dp, 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title,
            fontSize = 12.sp,
            style = TextStyle(lineHeight = 16.sp),
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun FormItemPreview() {
    FormItem("Sales Order Id") {
        io.dock2dock.ui.components.TextField(
            value = "12345",
            placeholderText = "Sales Order Id"
        )
    }
}