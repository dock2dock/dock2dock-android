package io.dock2dock.example.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import io.dock2dock.android.freightmanagement.components.ShippingContainersScreen

@Composable
fun ShippingContainersPageScreen(
    navController: NavController) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "Shipping Containers",
                navController = navController
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ShippingContainersScreen {
                navController.navigate("shippingContainers/$it")
            }
        }
    }
}