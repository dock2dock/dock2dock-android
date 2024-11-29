package io.dock2dock.example.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import io.dock2dock.android.ui.components.DefaultTopAppBar

@Composable
fun ConsignmentHeaderPageScreen(
    navController: NavController
) {
    val salesOrderNumber = "FC1008"

    fun navigateToPrintShippingLabels() {
        navController.navigate("consignmentHeader/$salesOrderNumber/printShippingLabels")
    }

    val actionItems: @Composable RowScope.() -> Unit = {
        IconButton(onClick = {
            navigateToPrintShippingLabels()
        }) {
            Icon(
                imageVector = Icons.Filled.Print,
                tint = Color.White,
                contentDescription = null
            )
        }
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "Consignment $salesOrderNumber",
                navController = navController,
                actionItems = actionItems
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        }
    }
}