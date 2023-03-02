package com.dock2dock.android.crossdock

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.dock2dock.android.application.models.query.CrossdockLabel
import com.dock2dock.android.crossdock.dialogs.DialogPrintCrossdockLabel
import com.dock2dock.android.crossdock.viewModels.CrossdockLabelDataTableViewModel
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.ui.components.*
import com.dock2dock.ui.ui.theme.*
import com.dock2dock.ui.views.RetryErrorView

data class CrossdockLabelDataTable(val salesOrderNo: String) {

    @Composable
    fun launch(context: Context) {
        val viewModel = CrossdockLabelDataTableViewModel(TokenManager.getInstance(context), Dock2DockConfiguration.getInstance(context), salesOrderNo)
        return CrossdockLabelDataTableUI(viewModel)
    }
}

@Composable
internal fun CrossdockLabelDataTableUI(viewModel: CrossdockLabelDataTableViewModel)
{
    var showDialog by remember { mutableStateOf(false) }

    val loadError by viewModel.loadError.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val items by viewModel.items.observeAsState(listOf())

    if (showDialog) {
        DialogPrintCrossdockLabel(
            viewModel.tokenManager,
            viewModel.dock2DockConfiguration,
            visible = showDialog,
            onDismissRequest = { showDialog = !showDialog },
            onSuccessRequest = {
                showDialog = !showDialog
                viewModel.getCrossdockLabels()
                               },
            viewModel.salesOrderNo
        )
    }

    Column {
        Row(modifier = Modifier.padding(8.dp, 0.dp), horizontalArrangement = Arrangement.Start) {
            PrimaryButton(text = "Add", variant = ButtonVariant.Primary, modifier = Modifier) {
                showDialog = true
            }

            PrimaryIconButton(
                text = "Refresh",
                icon = Icons.Filled.Refresh
            ) {
                viewModel.getCrossdockLabels()
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
fun DataTable(isLoading: Boolean,
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
                Text("No Records Found!")
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
fun TableHeaderRow() {
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
fun TableRow(
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
fun PreviewDataTable() {
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

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun PreviewTableLoading()
{
    Box {
        Text("No records found")
        TableLoading()
    }
}
