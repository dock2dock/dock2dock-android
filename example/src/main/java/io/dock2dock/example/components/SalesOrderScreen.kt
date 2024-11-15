package io.dock2dock.example.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.dock2dock.android.crossdock.components.CrossdockLabelDataTable
import io.dock2dock.android.crossdock.components.LicensePlateScreen
import io.dock2dock.android.crossdock.viewModels.LicensePlateViewModel

@Composable
fun SalesOrderScreen(
    navController: NavController
    ) {
    val salesOrderNumber = "SO1002"

    val licensePlateViewModel = viewModel {
        LicensePlateViewModel(salesOrderNumber)
    }

    Scaffold(
        topBar = {
            io.dock2dock.android.ui.components.DefaultTopAppBar(
                title = "Sales Order $salesOrderNumber",
                navController = navController
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row {
                CrossdockLabelDataTable(salesOrderNumber)
                LicensePlateScreen(viewModel = licensePlateViewModel)
            }
        }
    }
}