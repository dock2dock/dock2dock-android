package io.dock2dock.example.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.dock2dock.example.ui.theme.OxfordBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(title: String,
                     actionItems: (@Composable RowScope.() -> Unit) = {},
                     navController: NavController
) {

    val canNavigateBack = navController.previousBackStackEntry != null

    fun navigateUp() {
        navController.navigateUp()
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OxfordBlue,
            titleContentColor = Color.White,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = {
                    navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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