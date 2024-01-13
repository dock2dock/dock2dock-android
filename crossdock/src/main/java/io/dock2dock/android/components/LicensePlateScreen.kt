package io.dock2dock.android.components

import androidx.annotation.RestrictTo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dokar.sheets.rememberBottomSheetState
import io.dock2dock.android.models.query.LicensePlate
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.viewModels.LicensePlateViewModel
import kotlinx.coroutines.launch
import java.util.Date

internal data class LicensePlateLauncher(val viewModel: LicensePlateViewModel) {
    @Composable
    fun launch() {
        return LicensePlateScreen(viewModel)
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Composable
fun LicensePlateScreen(
    viewModel: LicensePlateViewModel
) {
    val licensePlate by viewModel.licensePlate.collectAsState(null)

    val showLinesSheetState = rememberBottomSheetState()

    val coroutineScope = rememberCoroutineScope()

    licensePlate?.let { lp ->
        LicensePlateContent(
            licensePlate = lp,
            onClose = { viewModel.clearLicensePlate() },
            onRefresh = { viewModel.refresh() },
            onViewLines = {
                coroutineScope.launch {
                    showLinesSheetState.expand(animate = true)
                }
            },
            onComplete = { viewModel.complete() },
            onReprint = { viewModel.reprint(false) }
        )
        LicensePlateLinesBottomSheet(showLinesSheetState, lp.no)
    }
}

@Composable
fun LicensePlateContent(
    licensePlate: LicensePlate,
    onClose: (() -> Unit) = {},
    onRefresh: (() -> Unit) = {},
    onComplete: (() -> Unit) = {},
    onReprint: (() -> Unit) = {},
    onViewLines: (() -> Unit) = {}
) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        Column(Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .background(color = PrimaryOxfordBlue)
            .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("License Plate")
                IconButton(modifier = Modifier.size(18.dp), onClick = {
                    onClose()
                }) {
                    Icon(
                        Icons.Filled.Close,
                        "contentDescription")
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = licensePlate.no, modifier = Modifier.align(Alignment.CenterVertically))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LINES", color = Color.LightGray, fontSize = 10.sp)
                    Text(text = licensePlate.totalCount.toString())
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("QUANTITY", color = Color.LightGray, fontSize = 10.sp)
                    Text(text = licensePlate.quantityDescription)
                }

                Row(Modifier.align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(modifier = Modifier.size(24.dp), onClick = {
                        onComplete()
                    }) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            "contentDescription")
                    }
                    IconButton(modifier = Modifier.size(24.dp), onClick = {
                        onViewLines()
                    }) {
                        Icon(
                            Icons.Filled.List,
                            "contentDescription")
                    }

                    IconButton(modifier = Modifier.size(24.dp), onClick = {
                        onRefresh()
                    }) {
                        Icon(
                            Icons.Filled.Sync,
                            "contentDescription")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
internal fun PreviewLicensePlateContent() {
    val licensePlate = LicensePlate("LP000008", "Chilled Chep", false, "000942190400001123456", Date(2023,12,4,12,22,0), 12.5, 2, 12)

    LicensePlateContent(licensePlate)
}