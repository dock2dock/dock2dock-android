package io.dock2dock.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "Home",
                navController = navController
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(16.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MenuItemContent("Sales Order", Icons.Filled.LocalShipping) {
                        navController.navigate("SalesOrder")
                    }
                }
                item {
                    MenuItemContent(
                        "Shipping Containers",
                        Icons.AutoMirrored.Filled.Assignment
                    ) {
                        navController.navigate("ShippingContainers")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemContent(title: String, imageVector: ImageVector, onClick: (() -> Unit) = {}) {
    Box(
        Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(color = Color.White)
            .clickable {
                onClick()
            }) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Icon(
                imageVector = imageVector,
                modifier = Modifier.size(40.dp),
                contentDescription = "contentDescription")
            Text(title)
        }

    }
}