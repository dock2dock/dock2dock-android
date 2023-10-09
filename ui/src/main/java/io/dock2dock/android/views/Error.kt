package io.dock2dock.android.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.PrimaryButton

@Composable
fun ErrorView(
    title: String,
    error: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    action: @Composable (() -> Unit)? = null
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(error, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (action != null) {
                action()
            }
        }
    }
}

@Composable
fun NoRecordsFoundError(error: String, modifier: Modifier = Modifier) {
    ErrorView(title = "No Items Found",
        error = error,
        modifier = modifier)
}

@Composable
fun UnauthorisedErrorView() {
    ErrorView(title = "Unauthorised", error = "With the credentials provided we are unable to authenticate with Dock2Dock")
}

@Composable
fun RetryErrorView(title: String, error: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ErrorView(
        title = title,
        error = error,
        modifier = modifier.padding(16.dp)) {
        PrimaryButton(
            text = "Retry",
            onClick = { onClick() },
            variant = ButtonVariant.Primary,
            modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRetryErrorView() {
    RetryErrorView(title = "We're Sorry",
        error = "With the credentials provided we are unable to authenticate with Dock2Dock",
        modifier = Modifier,
        onClick = {}
    )
}