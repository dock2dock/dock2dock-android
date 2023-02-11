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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.dock2dock.android.application.models.commands.DeleteCrossdockLabel
import com.dock2dock.android.application.models.query.CrossdockLabel
import com.dock2dock.android.crossdock.dialogs.DialogPrintCrossdockLabel
import com.dock2dock.android.crossdock.viewModels.CrossdockLabelDataTableViewModel
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.ui.components.*
import com.dock2dock.ui.ui.theme.*
import com.dock2dock.ui.views.RetryErrorView

data class CrossdockLabelDataTable(val salesOrderId: String) {

    @Composable
    fun launch(context: Context) {
        return CrossdockLabelDataTableUI(Dock2DockConfiguration.getInstance(context), TokenManager.getInstance(context), salesOrderId)
    }
}

@Composable
internal fun CrossdockLabelDataTableUI(dockConfiguration: Dock2DockConfiguration, tokenManager: TokenManager, salesOrderId: String)
{
    var viewModel = CrossdockLabelDataTableViewModel(tokenManager, dockConfiguration, salesOrderId)

    val showDialog = remember { mutableStateOf(false)  }

    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val items by remember { viewModel.items }

    if (showDialog.value) {
        DialogPrintCrossdockLabel(tokenManager, dockConfiguration, showDialog, salesOrderId)
    }

    Column {
        Row(modifier = Modifier.padding(8.dp, 0.dp), horizontalArrangement = Arrangement.Start) {
            PrimaryButton(text = "Add", ButtonVariant.Primary, Modifier) {
                showDialog.value = !showDialog.value
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
fun DataTable(isLoading: Boolean = false,
              loadError: String = "",
              items: List<CrossdockLabel>,
              reloadItems: (() -> Unit) = {},
              onRowDelete: (CrossdockLabel) -> Unit,
              modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        item {

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

        Box(
            contentAlignment = Center,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = PrimaryOxfordBlue)
            }
            if (loadError.isNotEmpty()) {
                RetryErrorView(
                    error = loadError,
                    title = "We're sorry",
                ) {
                    reloadItems()
                }
            }
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
            )), onRowDelete = {})
}