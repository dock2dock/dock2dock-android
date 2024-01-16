package io.dock2dock.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import io.dock2dock.android.dialogs.SettingsDialog
import io.dock2dock.android.dialogs.licensePlate.AddLicensePlateDialog
import io.dock2dock.android.models.Constants.SnackBarDurationVeryShort
import io.dock2dock.android.models.query.LicensePlate
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.viewModels.LicensePlatesSheetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun LicensePlatesSheetScreen(
    viewModel: LicensePlatesSheetViewModel,
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    val licensePlates by viewModel.licensePlates.collectAsState(listOf())
    val loading by viewModel.refreshing.collectAsState(false)
    val successMessage by viewModel.successMessage.collectAsState(null)
    val isSnackBarShowing by viewModel.isSnackBarShowing.collectAsState(false)

    var showAddLicensePlateDialog by remember { mutableStateOf(false) }

    var showSettingsDialog by remember { mutableStateOf(false) }

    val bottomSheetState = rememberBottomSheetState()

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    if (isSnackBarShowing) {
        LaunchedEffect(isSnackBarShowing) {
            try {
                val job = launch {
                    when (scaffoldState.snackbarHostState.showSnackbar(
                        successMessage ?: "", duration = SnackbarDuration.Indefinite
                    )) {
                        SnackbarResult.Dismissed -> {
                        }

                        else -> {

                        }
                    }
                }
                delay(SnackBarDurationVeryShort)
                job.cancel()
            } finally {
                viewModel.onDismissSnackBar()
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                Row(
                    modifier = Modifier.padding(8.dp, 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrimaryButton(text = "Add", variant = ButtonVariant.Primary) {
                        showAddLicensePlateDialog = true
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        viewModel.load()
                    }) {
                        Icon(
                            Icons.Filled.Refresh,
                            "contentDescription"
                        )
                    }
                    IconButton(onClick = {
                        showSettingsDialog = true
                    }) {
                        Icon(
                            Icons.Filled.Settings,
                            "contentDescription"
                        )
                    }

                }
                LicensePlatesTableContent(loading, licensePlates) {
                    viewModel.setSelectedItem(it)
                    scope.launch {
                        bottomSheetState.expand(animate = true)
                    }
                }
            }
        }
    }


    ActionBottomSheet(bottomSheetState, viewModel)

    AddLicensePlateDialog(
        visible = showAddLicensePlateDialog,
        salesOrderNo = viewModel.salesOrderNo,
        onSuccessRequest = {
            showAddLicensePlateDialog = !showAddLicensePlateDialog
            viewModel.refresh()
                           },
        onDismissRequest = { showAddLicensePlateDialog = !showAddLicensePlateDialog }
    )

    SettingsDialog(
        visible = showSettingsDialog,
        onDismissRequest = { showSettingsDialog = !showSettingsDialog }
    )
}

@Composable
internal fun HeaderRow() {
    Row(
        Modifier
            .background(PrimaryOxfordBlue)
            .fillMaxWidth()
            .padding(8.dp, 6.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {

            Text(
                modifier = Modifier
                    .width(200.dp),
                text = "#",
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .width(60.dp),
                text = "Lines",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Qty",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LicensePlatesTableContent(
    isLoading: Boolean = false,
    items: List<LicensePlate>,
    setSelected: ((LicensePlate) -> Unit) = {}
) {

    if (isLoading) {
        TableLoading()
    }

    LazyColumn(
    ) {
        item {
            HeaderRow()
        }

        if (!isLoading && items.isEmpty()) {
            item {
                TableNoRecords()
            }
        } else {
            items(
                items = items,
                key = { item -> item.no }
            ) { item ->
                LicensePlateRowContent(
                    item = item,
                    onSelected = {
                        setSelected(item)
                    }
                )
            }
        }

    }
}

@Composable
fun LicensePlateRowContent(
    item: LicensePlate,
    onSelected: (() -> Unit) = {}
) {

    val sdf = SimpleDateFormat("EEE MMM dd, h:mm aa")

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        Column(Modifier.weight(1f)) {
            Text(text = item.description,
                fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth()) {
                Text(
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray,
                    text = sdf.format(item.dateCreated),
                    maxLines = 1,
                    modifier = Modifier.width(200.dp)
                )
                Text(
                    text = item.totalCount.toString(),
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text = item.quantityDescription,
                )
            }
        }
        IconButton(modifier = Modifier.size(24.dp), onClick = {
            onSelected()
        }) {
            Icon(
                Icons.Filled.MoreHoriz,
                "contentDescription")
        }
    }
    TableRowDivider()
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun ActionBottomSheet(
    state: com.dokar.sheets.BottomSheetState,
    viewModel: LicensePlatesSheetViewModel) {

    val selectedItem: LicensePlate? by viewModel.selectedItem.collectAsState(null)

    val coroutineScope = rememberCoroutineScope()

    val showLinesSheetState = rememberBottomSheetState(
        confirmValueChange = {
            if (it == com.dokar.sheets.BottomSheetValue.Collapsed) {
                viewModel.setSelectedItem(null)
            }
            true
        },
    )

    fun closeSheet() {
        coroutineScope.launch {
            state.collapse()
        }
    }

    selectedItem?.let {
        LicensePlateLinesBottomSheet(showLinesSheetState, it.no)
    }

    BottomSheet(
        state = state,
        modifier = Modifier
            .heightIn(min = 250.dp),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                BottomSheetActionRow(
                    name = "Reprint Manifest",
                    description = "Labels will be printed to the default printer. Go to settings to change the printer.",
                    imageVector = Icons.Filled.Print,
                    onclick = {
                        selectedItem?.let {
                            viewModel.reprintLicensePlate(it, false)
                            closeSheet()
                        }
                    })
                if (!selectedItem?.ssccBarcode.isNullOrEmpty()) {
                    BottomSheetActionRow(
                        name = "Reprint LP & SSCC Label",
                        description = "Labels will be printed to the default printer. Go to settings to change the printer.",
                        imageVector = Icons.Filled.Print,
                        onclick = {
                            selectedItem?.let {
                                viewModel.reprintLicensePlate(it, true)
                                closeSheet()
                            }
                        })
                }
                BottomSheetActionRow(
                    name = "Set Active",
                    imageVector = Icons.Filled.Check,
                    onclick = {
                        selectedItem?.let {
                            viewModel.setActive(it)
                            closeSheet()
                        }
                    })
                BottomSheetActionRow(
                    name = "Show License Plate Lines",
                    imageVector = Icons.Filled.List,
                    onclick = {
                        closeSheet()
                        coroutineScope.launch {
                            showLinesSheetState.expand(animate = true)
                        }
                    })
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewLicensePlateRowContent() {
    var licensePlate = LicensePlate("LP000008", "Chilled Chep", false, "00094214090000112345",
        Date(2023,12,4,12,22,0),12.5, 2, 12
    )
    LicensePlateRowContent(licensePlate)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewLicensePlatesTableContent() {
    var items = arrayListOf(
        LicensePlate("LP000008", "Chilled Chep", false, "00094214090000112345",
            Date(2023,12,4,12,22,0), 12.5, 2, 12),
        LicensePlate("LP000009", "Chilled Chep", false, "00094214090000112345",
            Date(2023,12,4,12,22,0),12.5, 2, 12))

    LicensePlatesTableContent(false, items)
}