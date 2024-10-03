package io.dock2dock.example.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.dock2dock.android.freightmanagement.components.ShippingContainerScreen
import io.dock2dock.android.freightmanagement.viewModels.ShippingContainerViewModel

@Composable
fun ShippingContainerPageScreen(
    shippingContainerId: String,
    navController: NavController) {

    val viewModel = viewModel {
        ShippingContainerViewModel(shippingContainerId) {
            navController.popBackStack()
        }
    }

    val shippingContainer by viewModel.shippingContainer.collectAsState(null)

    val title = "Shipping Container - ${shippingContainer?.barcode ?: ""}"
    Scaffold(
        topBar = {
            io.dock2dock.android.ui.components.DefaultTopAppBar(
                title = title,
                navController = navController
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ShippingContainerScreen(viewModel)
        }
    }
}