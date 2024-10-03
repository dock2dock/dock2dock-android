package io.dock2dock.android.freightmanagement.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.dock2dock.android.freightmanagement.components.ConsignmentManifestScreen
import io.dock2dock.android.freightmanagement.components.ConsignmentManifestsScreen
import io.dock2dock.android.freightmanagement.components.PrintConsignmentManifestItemScreen

class ConsignmentManifestsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "ConsignmentManifests")
            {
                composable(route = "ConsignmentManifests") {
                    ConsignmentManifestsScreen(navController) {
                        finish()
                    }
                }
                composable(route = "ConsignmentManifests/{id}") { backStackEntry ->
                    backStackEntry.arguments?.getString("id")?.let {
                        ConsignmentManifestScreen(it, navController)
                    }
                }
                composable(route = "ConsignmentManifests/{id}/printShippingLabels") { backStackEntry ->
                    backStackEntry.arguments?.getString("id")?.let {
                        PrintConsignmentManifestItemScreen(it) {
                            navController.navigateUp()
                        }
                    }
                }
            }
        }
    }
}