package com.dock2dock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dock2dock.ui.ui.theme.PrimaryDark
import com.dock2dock.ui.ui.theme.PrimaryOxfordBlue
import com.dock2dock.ui.ui.theme.PrimaryPlatinum
import com.dock2dock.ui.ui.theme.PrimaryWhite

enum class ButtonVariant {
    Primary, Default
}

@Composable
fun getButtonColors(variant: ButtonVariant): ButtonColors {
    return when(variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            backgroundColor = PrimaryOxfordBlue,
            contentColor = PrimaryWhite,
            disabledBackgroundColor = PrimaryPlatinum
                .copy(alpha = 0.2f)
                .compositeOver(PrimaryOxfordBlue)
        )
        ButtonVariant.Default -> ButtonDefaults.buttonColors(
            backgroundColor = PrimaryPlatinum,
            contentColor = PrimaryDark,
            disabledBackgroundColor = PrimaryPlatinum
                .copy(alpha = 0.2f)
                .compositeOver(PrimaryOxfordBlue)
        )
    }
}

@Composable
fun PrimaryButton(text: String,
                  modifier: Modifier = Modifier.fillMaxWidth(),
                  variant: ButtonVariant = ButtonVariant.Default,
                  isLoading: Boolean = false,
                  onClick: () -> Unit) {

    val progressIndicatorColor = getButtonColors(variant = variant)

    Button(
        onClick = onClick,
        shape = RectangleShape,
        enabled = true,
        modifier = modifier,
        colors = getButtonColors(variant = variant),
    ) {
        if (isLoading) {
            LoadingPleaseWait(progressIndicatorColor.contentColor(enabled = true).value)
        } else {
            Text(text = text)
        }
    }
}

@Composable
fun LoadingPleaseWait(color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = "Loading..",
            textAlign = TextAlign.Center
        )

        CircularProgressIndicator(
            strokeWidth = 2.dp,
            color = color,
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.CenterVertically)
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
        )
    }
}

@Composable
fun PrimaryIconButton(text: String,
                      icon: ImageVector,
                      modifier: Modifier = Modifier,
                      interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
                      onClick: () -> Unit) {
    val isPressed by interactionSource.collectIsPressedAsState()

    var backgroundColor by remember { mutableStateOf(Color.Transparent) }

    OutlinedButton(
        onClick = onClick,
        shape = RectangleShape,
        border = BorderStroke(0.dp, Color.Transparent),
        enabled = true,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)) {
        AnimatedVisibility(visible = isPressed) {
            if (isPressed) {
                backgroundColor = PrimaryOxfordBlue
            }
        }
        Icon(
            icon,
            contentDescription = "Localized description"
        )
        Text(text)
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    var isLoading: Boolean by remember { mutableStateOf(false) }
    PrimaryButton("Hello World",
        variant = ButtonVariant.Primary,
        isLoading = isLoading,
        onClick =  { isLoading = !isLoading })
}
