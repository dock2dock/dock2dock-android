package io.dock2dock.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class FormItemLayout {
    Horizontal, Vertical
}

@Composable
fun FormSectionHeader(title: String,
                      content: @Composable () -> Unit) {

    Column(
        modifier = Modifier
            .padding(0.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.h6,
            fontSize = 12.sp
        )
        content()
    }

}

@Composable
fun FormItem(title: String,
             layout: FormItemLayout = FormItemLayout.Vertical,
             subTitle: String = "",
             content: @Composable () -> Unit) {

    if (layout == FormItemLayout.Vertical) {
        Column(
            modifier = Modifier
                .padding(0.dp, 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                fontSize = 12.sp
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    subTitle,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.caption
                )
            }
            content()
        }
    } else {
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp) ) {
                Text(
                    title,
                    modifier = Modifier.padding(end = 12.dp)
                )
                if (subTitle.isNotEmpty()) {
                    Text(
                        subTitle,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormItemPreview() {
    Column(modifier = Modifier.padding(24.dp)) {
        FormItem("Sales Order Id", FormItemLayout.Horizontal) {
            Dock2DockTextField(
                value = "12345",
                placeholderText = "Sales Order Id"
            )
        }
        FormSectionHeader("Crossdock") {
            FormItem("Sales Order Id") {
                Dock2DockTextField(
                    value = "12345",
                    placeholderText = "Sales Order Id"
                )
            }
            FormItem("Sales Order Id",
                subTitle = "do sodiweufiuf dwufwhufhwu wdwd",
                layout = FormItemLayout.Horizontal) {
                Dock2DockSwitch(true)
            }
        }
    }
}