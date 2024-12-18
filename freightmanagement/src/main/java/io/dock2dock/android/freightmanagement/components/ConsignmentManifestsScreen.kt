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
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
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
import io.dock2dock.android.application.models.query.FreightAddress
import io.dock2dock.android.application.models.query.FreightConsignmentManifest
import io.dock2dock.android.freightmanagement.model.ConsignmentManifestStatus
import io.dock2dock.android.freightmanagement.model.PreservationState
import io.dock2dock.android.freightmanagement.viewModels.ConsignmentManifestsScreenViewModel
import io.dock2dock.android.ui.components.DefaultTopAppBar
import io.dock2dock.android.ui.components.TableLoading
import io.dock2dock.android.ui.components.TableNoRecords
import io.dock2dock.android.ui.components.TableRowDivider
import io.dock2dock.android.ui.components.Tag
import io.dock2dock.android.ui.components.ValidationErrorMessage
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryPlatinum
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
        Column {
            if (errorMessage.isNotEmpty()) {
                ValidationErrorMessage(errorMessage = errorMessage)
            } else {
                ConsignmentManifestsDataTable(
                    modifier = Modifier.padding(it),
                    isLoading = isLoading,
                    viewModel = viewModel
                ) { consignmentManifest ->
                    navController.navigate("consignmentManifests/${consignmentManifest.id}")
                }
            }
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
                    },
                    viewModel.showPickupLocation
                )
            }
        }

    }
}

@Composable
internal fun ConsignmentManifestTableRow(
    item: FreightConsignmentManifest,
    modifier: Modifier = Modifier,
    showPickupLocation: Boolean
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

                Tag(text = item.statusName, color = ConsignmentManifestStatus.getColor(item.statusId))
            }
        }
        if (showPickupLocation) {

            Spacer(modifier = Modifier.padding(top = 4.dp))
            IconAndText(text = item.pickupAddress.name, icon = Icons.Filled.PinDrop)
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            IconAndText(modifier = Modifier.weight(weight = 1f), text = item.carrierName, icon = Icons.Filled.LocalShipping)
            Spacer(modifier = Modifier.padding(end = 6.dp))
            IconAndText(text = dateAsString, icon = Icons.Filled.CalendarToday)
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            ConsignmentManifestDescriptionContent(modifier = Modifier.weight(weight = 1f), item)
            Spacer(modifier = Modifier.padding(end = 4.dp))
            Tag(text = item.preservationStateName, color = PreservationState.getColor(item.preservationStateId))
        }
    }
    TableRowDivider()
}

@Composable
fun IconAndText(modifier: Modifier = Modifier,
                text: String,
                icon: ImageVector) {
    val iconId = "iconId"

    val annotatedString = buildAnnotatedString {
        appendInlineContent(iconId, "[icon]")
        append(" ")
        append(text)
    }

    val inlineContent = mapOf(
        Pair(
            iconId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(icon,"")
            }
        )
    )

    Text(text = annotatedString,
        style = MaterialTheme.typography.caption,
        inlineContent = inlineContent,
        modifier = modifier)
}

@Composable
fun ConsignmentManifestDescriptionContent(modifier: Modifier,
                                          item: FreightConsignmentManifest) {
    val boldStyle = SpanStyle(
        fontWeight = FontWeight.Bold
    )

    val text = buildAnnotatedString {
        withStyle(style = boldStyle) {
            append("CONS: ")
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

    Text(modifier = modifier,
        text = text,
        style = MaterialTheme.typography.caption,
        fontSize = 11.sp,
        color = Color.Gray)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 400)
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
        "chilled",
        "Chilled",
        false,
        "RS1023",
        20,
        148.4,
        14,
        "fssi",
        "Foodstuffs South Island",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "Hornby Temperature Control DC (DC08)",
        Date(2024,9,26, 11,0,0),
        Date(2024,9,25,11,0,0),
        FreightAddress("fqwfwfqwf", "Harris Farms Cheviot")
    )

    ConsignmentManifestTableRow(manifest, showPickupLocation = true)
}
