package io.dock2dock.android.freightmanagement.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.freightmanagement.dialogs.CreateShippingContainerDialog
import io.dock2dock.android.freightmanagement.viewModels.ShippingContainersDataTableViewModel
import io.dock2dock.android.ui.components.TableLoading
import io.dock2dock.android.ui.components.TableNoRecords
import io.dock2dock.android.ui.components.TableRowDivider
import io.dock2dock.android.ui.dialogs.ErrorDialog
import io.dock2dock.android.ui.theme.ColorSuccess
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryPlatinum
import io.dock2dock.settings.ui.dialog.SettingsDialog
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ShippingContainersScreen(onShippingContainerClicked: (String) -> Unit) {
    val viewModel = viewModel {
        ShippingContainersDataTableViewModel()
    }
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val showErrorDialog = errorMessage.isNotEmpty()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var showAddDialog by remember { mutableStateOf(false) }

    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    backgroundColor = PrimaryPlatinum,
                    onClick = { showSettingsDialog = !showSettingsDialog }
                ) {
                    Icon(Icons.Filled.Settings, "Floating action button.", tint = PrimaryOxfordBlue)
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                FloatingActionButton(
                    backgroundColor = PrimaryPlatinum,
                    onClick = { viewModel.refresh() }
                ) {
                    Icon(Icons.Filled.Refresh, "Floating action button.", tint = PrimaryOxfordBlue)
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                FloatingActionButton(
                    backgroundColor = PrimaryOxfordBlue,
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Filled.Add, "Floating action button.", tint = Color.White)
                }
            }
        }
    ) {
        ShippingContainersDataTable(
            modifier = Modifier.padding(it),
            isLoading = isLoading,
            viewModel = viewModel
        ) { shippingContainer ->
            onShippingContainerClicked(shippingContainer.id)
        }
    }

    CreateShippingContainerDialog(
        visible = showAddDialog,
        onDismissRequest = { showAddDialog = !showAddDialog },
        onSuccessRequest = {
            showAddDialog = !showAddDialog

            onShippingContainerClicked(it.shippingContainerId)
        }
    )

    ErrorDialog(
        show = showErrorDialog,
        message = errorMessage,
        onDismiss = viewModel::onCloseErrorDialog,
        onConfirm = viewModel::onCloseErrorDialog
    )

    SettingsDialog(
        visible = showSettingsDialog,
        onDismissRequest = { showSettingsDialog = !showSettingsDialog }
    )
}

@Composable
internal fun ShippingContainersDataTable(isLoading: Boolean,
                                         modifier: Modifier,
                                        viewModel: ShippingContainersDataTableViewModel,
                                         onSelected: ((ShippingContainer) -> Unit)
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
                TableRow(
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
internal fun TableRow(
    item: ShippingContainer,
    modifier: Modifier = Modifier
) {

    val sdf = SimpleDateFormat("EEE MMM dd, h:mm aa")
    val dateAsString = sdf.format(item.dateCreated)

    Row(
        modifier = modifier.padding(8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(4f)) {
            Text(
                text = item.barcode,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.padding(top = 1.dp))
            Text(
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                text = dateAsString,
                maxLines = 1,
            )
        }
        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = item.consignmentProductName,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.padding(top = 1.dp))
            Text(
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                text = item.weightDescription,
                maxLines = 1,
            )
        }
        Box(modifier = Modifier.weight(0.3f)) {
            if (item.completed) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ColorSuccess)
            }
        }
    }
    TableRowDivider()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewTableRow() {
    val shippingContainer = ShippingContainer(
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "00090022680000000021",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "Pallet",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "CH1000",
        10,
        134.6,
        false,
        null,
        null,
        Date(),
        Date(),
        listOf()
    )

    TableRow(shippingContainer)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewTableRow_Completed() {
    val shippingContainer = ShippingContainer(
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "00090022680000000021",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "Pallet",
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "CH1000",
        10,
        134.6,
        true,
        null,
        null,
        Date(),
        Date(),
        listOf()
    )

    TableRow(shippingContainer)
}