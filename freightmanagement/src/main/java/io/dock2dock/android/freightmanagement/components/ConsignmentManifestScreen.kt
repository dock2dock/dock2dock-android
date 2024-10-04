package io.dock2dock.android.freightmanagement.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.dock2dock.android.application.models.query.FreightAddress
import io.dock2dock.android.application.models.query.FreightConsignmentManifest
import io.dock2dock.android.freightmanagement.viewModels.ConsignmentManifestScreenViewModel
import io.dock2dock.android.ui.components.DefaultTopAppBar
import io.dock2dock.android.ui.components.LoadingPleaseWait
import io.dock2dock.android.ui.components.Tag
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ConsignmentManifestScreen(id: String, navController: NavController) {

    val viewModel = viewModel {
        ConsignmentManifestScreenViewModel(id)
    }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val consignmentManifest by viewModel.consignmentManifest.collectAsState(null)

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "Consignment Manifest - ${consignmentManifest?.no ?: ""}",
                navController = navController)
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton =
        {
            if (consignmentManifest?.crossdockEnabled == true) {
                Column {
                    FloatingActionButton(
                        backgroundColor = PrimaryOxfordBlue,
                        onClick = {
                            navController.navigate("consignmentManifests/$id/printShippingLabels")
                        }
                    ) {
                        Icon(
                            Icons.Filled.Print,
                            "Floating action button.",
                            tint = PrimaryWhite
                        )
                    }
                }
            }
        }
    )
    {
        if (isLoading) {
            LoadingPleaseWait(color = PrimaryOxfordBlue)
        } else {
            consignmentManifest?.let { manifest ->
                ConsignmentManifestScreenContent(manifest)
            }
        }
    }
}

@Composable
fun ConsignmentManifestScreenContent(manifest: FreightConsignmentManifest) {
    val sdf = SimpleDateFormat("E MMM dd, yyyy", Locale.ENGLISH)
    val dateAsString = sdf.format(manifest.dispatchDate)

    Column(modifier = Modifier.padding(top = 12.dp)) {
        Column {
            FormGroupTitle("Details")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column {
                        FormItemTitle("No")
                        Text(manifest.no)
                    }
                }
                item {
                    Column {
                        FormItemTitle("Status")
                        Tag(text = manifest.statusName, color = PrimaryOxfordBlue)
                    }
                }
                item {
                    Column {
                        FormItemTitle("Dispatch Date")
                        Text(dateAsString)
                    }
                }
                item {
                    Column {
                        FormItemTitle("Preservation State")
                        Tag(text = manifest.preservationStateName, color = PrimaryOxfordBlue)
                    }
                }
                item {
                    Column {
                        FormItemTitle("Run Sheet")
                        Text(manifest.runSheetNo ?: "")
                    }
                }
                item {
                    Column {
                        FormItemTitle("Pallets")
                        Text(manifest.pallets.toString())
                    }
                }
                item {
                    Column {
                        FormItemTitle("Carrier Name")
                        Text(manifest.carrierName)
                    }
                }
            }
        }

        if (manifest.crossdockEnabled) {
            val sdf1 = SimpleDateFormat("E MMM dd, h:mm aa", Locale.ENGLISH)
            val dateAsString1 = manifest.crossdockArrivalDate?.let { sdf1.format(it) }

            FormGroupTitle("Crossdock")
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Column {
                            FormItemTitle("Crossdock Partner")
                            Text(manifest.crossdockPartnerIntegrationName ?: "")
                        }
                    }
                    item {
                        Column {
                            FormItemTitle("Warehouse Location")
                            Text(manifest.crossdockWarehouseLocationName ?: "")
                        }
                    }
                    item {
                        Column {
                            FormItemTitle("Arrival Date")
                            Text(dateAsString1 ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormItemTitle(text: String) {
    Text(text = text,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.subtitle2)
}

@Composable
fun FormGroupTitle(text: String) {
    Text(
        style = MaterialTheme.typography.h6,
        text = text,
        modifier = Modifier.padding(start = 12.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun Preview_ConsignmentManifestScreenContent() {
    val manifest = FreightConsignmentManifest(
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "FM1000",
        "active",
        "Active",
        "IDL - Independent Distributors Limited",
        "Tsi (Crossdock)",
        Date(2024,9,25),
        "Chilled",
        false,
        "RS1023",
        2,
        48.4,
        4,
        "fssi",
        "Foodstuffs South Island",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "Hornby Temperature Control DC (DC08)",
        Date(2024,9,26, 16,0,0),
        Date(2024,9,25,11,0,0),
        FreightAddress("", "Harris Farms Cheviot")
    )

    ConsignmentManifestScreenContent(manifest)
}