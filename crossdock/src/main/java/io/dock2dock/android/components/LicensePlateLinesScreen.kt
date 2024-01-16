package io.dock2dock.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState
import io.dock2dock.android.models.query.LicensePlateLine
import io.dock2dock.android.viewModels.LicensePlateLinesSheetViewModel

@Composable
fun LicensePlateLinesBottomSheet(state: BottomSheetState, licensePlateNo: String) {
    BottomSheet(
        state = state,
        modifier = Modifier
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        LicensePlateLinesScreen(licensePlateNo)
    }
}

@Composable
fun LicensePlateLinesScreen(licensePlateNo: String) {
    val viewModel = viewModel {
        LicensePlateLinesSheetViewModel()
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.load(licensePlateNo)
    }

    val items by viewModel.licensePlateLines.collectAsState(listOf())
    val loading by viewModel.isLoading.collectAsState(false)

    LicensePlateLinesContent(items = items, isLoading = loading)
}

@Composable
fun LicensePlateLinesContent(
    isLoading: Boolean = false,
    items: List<LicensePlateLine>) {

        if (isLoading) {
            TableLoading()
        }

        LazyColumn {
            if (!isLoading && items.isEmpty()) {
                item {
                    TableNoRecords()
                }
            } else {
                items(
                    items = items,
                    key = { item -> item.no }
                ) { item ->
                    LicensePlateLineContent(
                        item = item,
                    )
                }
            }
        }
}

@Composable
fun LicensePlateLineContent(
    item: LicensePlateLine) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        Column(Modifier.weight(1f)) {
            Text(text = item.noDescription,
                fontWeight = FontWeight.Bold)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray,
                        text = "QUANTITY"
                    )
                    Text(
                        text = item.quantityDescription,
                    )
                }

                if (item.units != null) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray,
                            text = "UNITS"
                        )
                        Text(
                            text = item.units.toString()
                        )
                    }

                }
            }
        }
    }
    TableRowDivider()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewLicensePlateLineContent() {
    var licensePlateLine = LicensePlateLine("80035", "FF Pork Mince 500g", 6.00, "KG", 12)
    LicensePlateLineContent(licensePlateLine)
}