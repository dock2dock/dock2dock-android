package com.dock2dock.ui.components

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
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
                  variant: ButtonVariant = ButtonVariant.Default,
                  modifier: Modifier = Modifier.fillMaxWidth(),
                  onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        enabled = true,
        modifier = modifier,
        colors = getButtonColors(variant = variant),
//        elevation = ButtonDefaults.elevation(
//            defaultElevation = 8.dp,
//            disabledElevation = 2.dp,
//            // Also pressedElevation
//        )
    ) {
        Text(text = text)
    }
}

@Composable
fun PrimaryIconButton(text: String,
                      icon: ImageVector,
                      modifier: Modifier = Modifier,
                      interactionSource: MutableInteractionSource =
                          remember { MutableInteractionSource() },
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
    PrimaryButton("Hello World") {

    }
}
