package io.dock2dock.android.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue

@Composable
fun DefaultTopAppBar(title: String,
                     actionItems: (@Composable RowScope.() -> Unit) = {},
                     navigationIcon: (@Composable () -> Unit)? = null,
                     navController: NavController
) {

    val canNavigateBack = navController.previousBackStackEntry != null

    fun navigateUp() {
        navController.navigateUp()
    }

    TopAppBar(
        backgroundColor = PrimaryOxfordBlue,
        contentColor = Color.White,
        navigationIcon = {
            if (navigationIcon != null) {
                navigationIcon()
            }
            else if (canNavigateBack) {
                IconButton(onClick = {
                    navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "arrowBack"
                    )
                }
            }
        },
        title = {
            Text(title, fontSize = 17.sp)
        },
        actions = actionItems
    )
}