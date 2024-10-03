package io.dock2dock.android.freightmanagement.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.dock2dock.android.application.models.query.FreightConsignmentManifest
import io.dock2dock.android.freightmanagement.viewModels.ConsignmentManifestsScreenViewModel
import io.dock2dock.android.ui.components.DefaultTopAppBar
import io.dock2dock.android.ui.components.TableLoading
import io.dock2dock.android.ui.components.TableNoRecords
import io.dock2dock.android.ui.components.TableRowDivider
import io.dock2dock.android.ui.components.Tag
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryPlatinum
import io.dock2dock.android.ui.theme.PrimaryWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConsignmentManifestsScreen(
    navController: NavController,
    onBackPressed: () -> Unit = {}) {
    val viewModel = viewModel {
        ConsignmentManifestsScreenViewModel()
    }

    val errorMessage by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)


    val canNavigateBack = navController.previousBackStackEntry != null

    fun navigateUp() {
        navController.navigateUp()
    }


    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "Consignment Manifests",
                navController = navController,
                navigationIcon = {
                    IconButton(onClick = {
                        if (canNavigateBack) {
                            navigateUp()
                        } else {
                            onBackPressed()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    backgroundColor = PrimaryPlatinum,
                    onClick = { viewModel.refresh() }
                ) {
                    Icon(Icons.Filled.Refresh, "Floating action button.", tint = PrimaryOxfordBlue)
                }
            }
        }
    )
    {
        ConsignmentManifestsDataTable(
            modifier = Modifier.padding(it),
            isLoading = isLoading,
            viewModel = viewModel
        ) { consignmentManifest ->
            navController.navigate("consignmentManifests/${consignmentManifest.id}")
        }
    }
}

@Composable
internal fun ConsignmentManifestsDataTable(isLoading: Boolean,
                                         modifier: Modifier,
                                         viewModel: ConsignmentManifestsScreenViewModel,
                                         onSelected: ((FreightConsignmentManifest) -> Unit)
) {

    val items by viewModel.items.collectAsStateWithLifecycle()

    if (isLoading) {
        TableLoading()
    }

    LazyColumn(modifier = modifier) {
        if (!isLoading && items.isEmpty()) {
            item {
                TableNoRecords()
            }
        } else {
            items(
                items = items,
                key = { item -> item.id }
            ) { item ->
                ConsignmentManifestTableRow(
                    item = item,
                    modifier = Modifier.clickable {
                        onSelected(item)
                    }
                )
            }
        }

    }
}

@Composable
internal fun ConsignmentManifestTableRow(
    item: FreightConsignmentManifest,
    modifier: Modifier = Modifier
) {

    val sdf = SimpleDateFormat("E MMM dd, yyyy", Locale.ENGLISH)
    val dateAsString = sdf.format(item.dispatchDate)

    Column(
        modifier = modifier
            .padding(8.dp, 8.dp)
            .fillMaxWidth(1f),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            Text(
                text = "#${item.no}",
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.crossdockEnabled) {
                    Tag(text ="Crossdock", color = PrimaryOrangeWeb, fontSize = 9.sp)
                    Spacer(modifier = Modifier.padding(end = 16.dp))
                }

                Tag(text = item.statusName, color = PrimaryOxfordBlue)
            }
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            Text(
                text = item.carrierName,
            )
            Spacer(modifier = Modifier.padding(top = 1.dp))
            Text(
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                text = dateAsString,
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            ConsignmentManifestDescriptionContent(item)
            Spacer(modifier = Modifier.padding(top = 1.dp))
            Tag(text = item.preservationStateName, color = PrimaryOxfordBlue)
        }
    }
    TableRowDivider()
}

@Composable
fun ConsignmentManifestDescriptionContent(item: FreightConsignmentManifest) {
    val boldStyle = SpanStyle(
        fontWeight = FontWeight.Bold
    )

    val text = buildAnnotatedString {
        withStyle(style = boldStyle) {
            append("QTY: ")
        }
        append(item.consignmentHeaderCount.toString())
        append(" ")

        withStyle(style = boldStyle) {
            append("PLT: ")
        }
        append(item.pallets.toString())
        append(" ")

        withStyle(style = boldStyle) {
            append("WGT: ")
        }
        append(item.totalWeight.toString())
        append(" kg ")

        withStyle(style = boldStyle) {
            append("RUN: ")
        }
        append(item.runSheetNo ?: "")
    }

    Text(text = text,
        style = MaterialTheme.typography.caption,
        color = Color.Gray)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewConsignmentManifestTableRow_CrossEnabled() {
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
        Date(2024,9,26, 11,0,0),
        Date(2024,9,25,11,0,0),
    )

    ConsignmentManifestTableRow(manifest)
}
