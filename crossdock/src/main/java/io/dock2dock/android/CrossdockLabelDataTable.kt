package io.dock2dock.android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.PrimaryButton
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.dialogs.DialogPrintCrossdockLabel
import io.dock2dock.android.dialogs.SettingsDialog
import io.dock2dock.android.models.query.CrossdockLabel
import io.dock2dock.android.ui.theme.ColorError
import io.dock2dock.android.ui.theme.ColorSuccess
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite
import io.dock2dock.android.viewModels.CrossdockLabelDataTableViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

internal data class CrossdockLabelDataTable(val salesOrderNo: String) {

    @Composable
    fun launch() {
        val viewModel = CrossdockLabelDataTableViewModel(salesOrderNo)
        return CrossdockLabelDataTableUI(viewModel)
    }
}

@Composable
internal fun CrossdockLabelDataTableUI(viewModel: CrossdockLabelDataTableViewModel)
{
    var showDialog by remember { mutableStateOf(false) }

    var showSettingsDialog by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val salesOrder by viewModel.salesOrder.observeAsState(null)
    val salesOrderNotFound by viewModel.salesOrderNotFound.observeAsState(false)
    val showServerErrorDialog by viewModel.showErrorDialog.observeAsState(false)

    ServerAlertDialog(
        show = showServerErrorDialog,
        message = errorMessage,
        onDismiss = viewModel::onCloseErrorDialog,
        onConfirm = viewModel::onCloseErrorDialog
    )

    DialogPrintCrossdockLabel(
        visible = showDialog,
        onDismissRequest = { showDialog = !showDialog },
        onSuccessRequest = {
            showDialog = !showDialog
            viewModel.getCrossdockLabels()
        },
        viewModel.salesOrderNo
    )

    SettingsDialog(
        visible = showSettingsDialog,
        onDismissRequest = { showSettingsDialog = !showSettingsDialog }
    )

    Column {
        Row(modifier = Modifier.padding(8.dp, 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {

            if (salesOrderNotFound) {
                Text(text = "Crossdock sales order not found",
                    color = ColorError,
                    fontSize = 14.sp)
            }
            else if (salesOrder?.shipped == true) {
                Text(text = "Shipped",
                    fontSize = 16.sp,
                        modifier = Modifier
                        .background(color = PrimaryOrangeWeb)
                        .padding(7.dp, 0.dp),
                        color = Color.White)
            }
            else if (salesOrder != null) {
                PrimaryButton(text = "Print", variant = ButtonVariant.Primary) {
                    showDialog = true
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                viewModel.getCrossdockLabels()
            }) {
                Icon(Icons.Filled.Refresh,
                    "contentDescription")
            }
            IconButton(onClick = {
                showSettingsDialog = true
            }) {
                Icon(Icons.Filled.Settings,
                    "contentDescription")
            }

        }
        DataTable(loadError = errorMessage,
            isLoading = isLoading,
            viewModel = viewModel
        )
    }
}

@Composable
fun ServerAlertDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                {
                    Text(text = "Ok", color = PrimaryOxfordBlue)
                }
            },
            title = { Text(text = "Error!") },
            text = { Text(text = message) }
        )
    }
}

@Composable
internal fun DataTable(isLoading: Boolean,
              loadError: String,
              viewModel: CrossdockLabelDataTableViewModel,
              modifier: Modifier = Modifier) {

    val items by viewModel.items.collectAsStateWithLifecycle()

    if (isLoading) {
        TableLoading()
    }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            TableHeaderRow()
        }

        if (!isLoading && items.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(all = 20.dp),
                    textAlign = TextAlign.Center,
                    text = "No Records Found!")
            }
        } else {
            items(
                items = items,
                key = { item -> item.id }
            ) { item ->
                TableRow(
                    item = item,
                    viewModel = viewModel
                )
            }
        }

    }
}


@Composable
internal fun TableLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CircularProgressIndicator(color = PrimaryOxfordBlue)
            Text(text = "Loading...")
        }
    }
}

@Composable
internal fun TableHeaderRow() {
    Row(
        Modifier
            .background(PrimaryOxfordBlue)
            .padding(4.dp, 6.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(0.44f),
            color = PrimaryWhite,
            text = "Barcode",
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 8.dp),
            color = PrimaryWhite,
            text = "Handling Unit",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun TableRow(
    item: CrossdockLabel,
    modifier: Modifier = Modifier,
    viewModel: CrossdockLabelDataTableViewModel) {

    var isLoading by remember { mutableStateOf(false) }

    var deleteIcon = if (item.isDeleted) {
        Icons.Filled.RestoreFromTrash
    } else {
        Icons.Filled.Delete
    }

    var textDecoration = if (item.isDeleted) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val sdf = SimpleDateFormat("EEE MMM dd, h:mm aa")
    var dateAsString = sdf.format(item.dateCreated)

    Row(
        modifier = modifier.padding(4.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val modifier = Modifier
            .weight(1f)
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = item.barcode,
                maxLines = 1,
                textDecoration = textDecoration
            )
            Spacer(modifier = Modifier.padding(top = 1.dp))
            Text(
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                text = dateAsString,
                maxLines = 1,
            )
        }
        Text(
            modifier = modifier,
            text = item.handlingUnitName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = textDecoration
        )
        Column(modifier = Modifier.width(24.dp)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Black
                )
            }
            else if (item.isProcessed) {
                Icon(imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "contentDescription",
                    tint = ColorSuccess)
            } else {
                Icon(imageVector = deleteIcon,
                    contentDescription = "contentDescription",
                    Modifier.clickable {
                        isLoading = true

                        viewModel.viewModelScope.launch {
                            // Update the item
                            viewModel.deleteCrossdockLabel(item)

                            isLoading = false // Hide loading indicator
                        }
                    })
            }
        }

    }
    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
}
//
//@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
//@Composable
//internal fun PreviewDataTable() {
//    var viewModel =
//    DataTable(
//        items = listOf(
//            CrossdockLabel(
//                "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
//                "00090022680000000021",
//                false,
//                "Chilled Carton cgwquygffwqfgywfguywqf",
//                false
//            ),
//            CrossdockLabel(
//                "7cafc3ae-edb1-42f8-820d-0d9003242dfd",
//                "00090022680000000021",
//                false,
//                "Chilled Carton",
//                true
//            ),
//            CrossdockLabel(
//                "7cafc3ae-edb1-42f9-820d-0d9003242dfd",
//                "00090022680000000021",
//                false,
//                "Chilled Carton",
//                false
//            ),
//            CrossdockLabel(
//                "7cafc3ae-edb1-42fd-820d-0d9003242dfd",
//                "00090022680000000021",
//                true,
//                "Chilled Carton",
//                false
//            )),
//        onRowDelete = {
//            Thread.sleep(5000)
//            it.isDeleted = !it.isDeleted
//        },
//        isLoading = false,
//        loadError = "")
//}

//@Preview(showBackground = true)
//@Composable
//internal fun PreviewEmptyDataTable() {
//    DataTable(
//        items = listOf(),
//        onRowDelete = {},
//        isLoading = false,
//        loadError = "")
//}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun PreviewTableLoading()
{
    Box {
        Text("No records found")
        TableLoading()
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun PreviewFragmentUI()
{
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")
    var viewModel = CrossdockLabelDataTableViewModel("12345")
    CrossdockLabelDataTableUI(viewModel)
}
