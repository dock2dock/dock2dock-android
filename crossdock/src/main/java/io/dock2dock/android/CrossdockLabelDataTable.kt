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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.PrimaryButton
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.dialogs.DialogPrintCrossdockLabel
import io.dock2dock.android.dialogs.SettingsDialog
import io.dock2dock.android.models.query.CrossdockLabel
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite
import io.dock2dock.android.viewModels.CrossdockLabelDataTableViewModel
import io.dock2dock.android.views.RetryErrorView

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

    val loadError by viewModel.loadError.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val items by viewModel.items.observeAsState(listOf())

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
        Row(modifier = Modifier.padding(8.dp, 0.dp), horizontalArrangement = Arrangement.Start) {

            PrimaryButton(text = "Print", variant = ButtonVariant.Primary) {
                showDialog = true
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
        DataTable(loadError = loadError,
            isLoading = isLoading,
            items = items,
            reloadItems = { viewModel.getCrossdockLabels() },
            onRowDelete = { viewModel.deleteCrossdockLabel(it) }
        )
    }
}

@Composable
internal fun DataTable(isLoading: Boolean,
              loadError: String,
              items: List<CrossdockLabel>,
              reloadItems: (() -> Unit) = {},
              onRowDelete: (CrossdockLabel) -> Unit,
              modifier: Modifier = Modifier) {


    if (isLoading) {
        TableLoading()
    }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            TableHeaderRow()
        }

        if (loadError.isNotEmpty()) {
            item {
                RetryErrorView(
                    error = loadError,
                    title = "We're sorry",
                    onClick = reloadItems
                )
            }

        }
        else if (!isLoading && items.isEmpty()) {
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
                    onRowDelete = onRowDelete
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
            .padding(0.dp, 6.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(0.44f)
                .padding(start = 8.dp),
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
    onRowDelete: (CrossdockLabel) -> Unit) {
    Row(
        modifier = modifier.padding(0.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val modifier = Modifier
            .weight(1f)
            .padding(start = 8.dp)
        Text(
            modifier = modifier,
            text = item.barcode,
        )
        Text(
            modifier = modifier,
            text = item.handlingUnitName,
        )
        Icon(Icons.Filled.Delete, "contentDescription",
            Modifier.clickable {
                onRowDelete(item)
            })
    }
    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
}

@Preview(showBackground = true)
@Composable
internal fun PreviewDataTable() {
    DataTable(
        items = listOf(
            CrossdockLabel(
                "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
                "00090022680000000021",
                "Chilled Carton",
                false
            ),
            CrossdockLabel(
                "7cafc3ae-edb1-42f8-820d-0d9003242dfd",
                "00090022680000000021",
                "Chilled Carton",
                true
            ),
            CrossdockLabel(
                "7cafc3ae-edb1-42f9-820d-0d9003242dfd",
                "00090022680000000021",
                "Chilled Carton",
                false
            ),
            CrossdockLabel(
                "7cafc3ae-edb1-42fd-820d-0d9003242dfd",
                "00090022680000000021",
                "Chilled Carton",
                false
            )),
        onRowDelete = {},
        isLoading = false,
        loadError = "")
}

@Preview(showBackground = true)
@Composable
internal fun PreviewEmptyDataTable() {
    DataTable(
        items = listOf(),
        onRowDelete = {},
        isLoading = false,
        loadError = "")
}

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
