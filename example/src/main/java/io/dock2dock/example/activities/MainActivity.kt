package io.dock2dock.example.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.eventBus.Dock2DockEventBus
import io.dock2dock.android.application.events.Dock2DockBarcodeFailedEvent
import io.dock2dock.android.application.events.Dock2DockBarcodeScannedEvent
import io.dock2dock.android.barcodeScanner.models.AndroidOsBrand
import io.dock2dock.android.barcodeScanner.models.HoneywellBarcodeReader
import io.dock2dock.android.barcodeScanner.models.IBarcodeReader
import io.dock2dock.android.barcodeScanner.models.IBarcodeReaderListener
import io.dock2dock.android.barcodeScanner.models.IBarcodeTriggerListener
import io.dock2dock.android.freightmanagement.components.PrintConsignmentItemScreen
import io.dock2dock.example.BuildConfig
import io.dock2dock.example.components.ConsignmentHeaderPageScreen
import io.dock2dock.example.components.HomeScreen
import io.dock2dock.example.components.SalesOrderScreen
import io.dock2dock.example.components.ShippingContainerPageScreen
import io.dock2dock.example.components.ShippingContainersPageScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity(),
    IBarcodeReaderListener,
    IBarcodeTriggerListener {

    private lateinit var barcodeReader: IBarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, BuildConfig.Dock2Dock_ApiKey, "http://localhost:3020")
        initialiseBarcodeReader()
        setContent {
            Dock2DockApp()
        }
    }

    //region Barcode

    private fun initialiseBarcodeReader() {
        val brand = android.os.Build.BRAND

        if (brand == AndroidOsBrand.Honeywell.id) {
            barcodeReader = HoneywellBarcodeReader(this)

            lifecycle.addObserver(barcodeReader as HoneywellBarcodeReader)

            barcodeReader.initialise()
            barcodeReader.setBarcodeReaderListener(this)
            barcodeReader.setBarcodeTriggerListener(this)
        }
    }

    override fun onBarcodeScanned(barcode: String) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val event = Dock2DockBarcodeScannedEvent(barcode)
                Dock2DockEventBus.publish(event)
            }
        }
    }

    override fun onBarcodeFailure() {
        runBlocking {
            withContext(Dispatchers.IO) {
                val event =
                    Dock2DockBarcodeFailedEvent("Error occurred while scanning barcode. Try again!")
                Dock2DockEventBus.publish(event)
            }
        }
    }

    override fun onBarcodeTrigger() {
    }

    //endregion
}

@Composable
fun Dock2DockApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable(route = "Home") {
            HomeScreen(navController)
        }
        composable(route = "ShippingContainers") {
            ShippingContainersPageScreen(navController)
        }
        composable(
            route = "ShippingContainers/{shippingContainerId}",
            arguments = listOf(navArgument("shippingContainerId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("shippingContainerId")?.let {
                ShippingContainerPageScreen(it, navController)
            }
        }
        composable(route = "SalesOrder") {
            SalesOrderScreen(navController)
        }

        composable(route = "ConsignmentHeader") {
            ConsignmentHeaderPageScreen(navController)
        }
        composable(route = "consignmentHeader/{consignmentHeaderNo}/printShippingLabels") { backStackEntry ->
            backStackEntry.arguments?.getString("consignmentHeaderNo")?.let {
                PrintConsignmentItemScreen(it) {
                    navController.navigateUp()
                }
            }
        }
    }
}




