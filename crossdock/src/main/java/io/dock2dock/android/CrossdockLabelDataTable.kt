package io.dock2dock.android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.dock2dock.android.components.BottomSheetDragHandle
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.PrimaryButton
import io.dock2dock.android.dialogs.ConfirmDialog
import io.dock2dock.android.dialogs.DialogPrintCrossdockLabel
import io.dock2dock.android.dialogs.DialogReprintCrossdockLabel
import io.dock2dock.android.dialogs.ErrorDialog
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
import java.util.Date

internal data class CrossdockLabelDataTable(val salesOrderNo: String) {

    @Composable
    fun launch() {
        val viewModel = CrossdockLabelDataTableViewModel(salesOrderNo)
        return CrossdockLabelDataTableUI(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
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

    val selectedItem by viewModel.selectedItem.collectAsState(null)

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
        initialValue = if (selectedItem != null) BottomSheetValue.Expanded else BottomSheetValue.Collapsed,
            confirmValueChange = {
                if (it == BottomSheetValue.Collapsed) {
                    viewModel.setSelectedItem(null)
                }
                true
        })
    )

    ErrorDialog(
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

    CrossdockLabelActionBottomSheet(bottomSheetState, viewModel) {
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
                    onSelected = {
                        viewModel.setSelectedItem(item)
                    }
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
            .fillMaxWidth()
            .padding(8.dp, 6.dp)
    ) {
        Text(
            modifier = Modifier
                .width(200.dp),
            color = PrimaryWhite,
            text = "Barcode",
            fontWeight = FontWeight.Bold
        )
        Text(
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
    onSelected: () -> Unit
    ) {

    var isLoading by remember { mutableStateOf(false) }

    var textDecoration = if (item.isDeleted) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val sdf = SimpleDateFormat("EEE MMM dd, h:mm aa")
    var dateAsString = sdf.format(item.dateCreated)

    Row(
        modifier = modifier.padding(8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val modifier = Modifier
        Column(modifier = modifier.width(200.dp)) {
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
            modifier = modifier.weight(0.40f),
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
            }
        }
        Column(modifier = Modifier.width(24.dp))  {
            Icon(imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "contentDescription",
                Modifier.clickable {
                    onSelected()
            })
        }
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun CrossdockLabelActionBottomSheet(state: BottomSheetScaffoldState, viewModel: CrossdockLabelDataTableViewModel, content:@Composable () -> Unit) {

    var showConfirmDialog by remember { mutableStateOf(false) }

    var showReprintDialog by remember { mutableStateOf(false) }

    val selectedItem: CrossdockLabel? by viewModel.selectedItem.collectAsState(null)

    val coroutineScope = rememberCoroutineScope()

    fun closeSheet() {
        coroutineScope.launch {
            state.bottomSheetState.collapse()
            viewModel.setSelectedItem(null)
        }
    }

    var isLabelDeleted = selectedItem?.isDeleted == true
    var isLabelProcessed = selectedItem?.isProcessed == true

    fun deleteUnDeleteLabel() {
        selectedItem?.let {
            viewModel.viewModelScope.launch {
                try {
                    viewModel.deleteCrossdockLabel(it)
                } finally {
                    closeSheet()
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = state,
        sheetPeekHeight = 0.dp, // <--- new line
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomSheetDragHandle()
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    BottomSheetActionRow(name = "Reprint Label", imageVector = Icons.Filled.Print, onclick = {
                        showReprintDialog = true
                    })
                    if (!isLabelProcessed) {
                        if (isLabelDeleted) {
                            BottomSheetActionRow(
                                name = "Undelete Label",
                                imageVector = Icons.Filled.RestoreFromTrash,
                                onclick = {
                                    deleteUnDeleteLabel()
                                })
                        }
                        else {
                            BottomSheetActionRow(
                                name = "Delete Label",
                                contentColor = ColorError,
                                imageVector = Icons.Filled.Delete,
                                onclick = {
                                    showConfirmDialog = true
                                })
                        }
                    }
                }
            }
        }
    ) {
        // transparent overlay on top of content, shown if sheet is expanded
        if (state.bottomSheetState.isExpanded) {
            Box(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .fillMaxSize()
                    .clickable {
                        closeSheet()
                    }
            ) {
                content()
            }
        } else {
            content()
        }
    }

    ConfirmDialog(
        confirmText = "Delete",
        cancelText = "Cancel",
        show = showConfirmDialog,
        title = "Are you sure you want to delete this?",
        contentText = "This action can only be reversed before the sales orders has been shipped",
        onDismiss = {
            showConfirmDialog = false
            closeSheet()
        },
        onConfirm = {
            showConfirmDialog = false
            deleteUnDeleteLabel()
        })

    selectedItem?.let {
        DialogReprintCrossdockLabel(
            visible = showReprintDialog,
            onDismissRequest = {
                showReprintDialog = !showReprintDialog
                closeSheet()
                               },
            onSuccessRequest = {
                showReprintDialog = !showReprintDialog
                closeSheet()
                viewModel.getCrossdockLabels()
            },
            crossdockLabel = it
        )
    }
}

@Composable
internal fun BottomSheetActionRow(contentColor: Color = Color.Black, name: String, imageVector: ImageVector, onclick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onclick()
        }
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Icon(
                imageVector = imageVector,
                contentDescription = "contentDescription"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = name)
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewTableRowHeader() {
    TableHeaderRow()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewTableRow() {
    var label = CrossdockLabel(
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "00090022680000000021",
        false,
        "Chilled Carton cgwquygffwqfgywfguywqf",
        Date(),
        false
    )

    TableRow(label) {

    }
}

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

//@Preview(showBackground = true, widthDp = 300, heightDp = 300)
//@Composable
//fun PreviewFragmentUI()
//{
//    val context = LocalContext.current
//    Dock2DockConfiguration.init(context, "")
//    var viewModel = CrossdockLabelDataTableViewModel("42242")
//    CrossdockLabelDataTableUI(viewModel)
//}
