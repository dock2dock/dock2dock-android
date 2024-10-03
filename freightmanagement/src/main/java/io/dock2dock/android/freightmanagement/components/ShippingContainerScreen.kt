package io.dock2dock.android.freightmanagement.components

import androidx.compose.foundation.background
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
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.dock2dock.android.application.models.query.ShippingContainerPackage
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.freightmanagement.viewModels.ShippingContainerViewModel
import io.dock2dock.android.ui.components.TableLoading
import io.dock2dock.android.ui.components.TableNoRecords
import io.dock2dock.android.ui.components.TableRowDivider
import io.dock2dock.android.ui.dialogs.ErrorDialog
import io.dock2dock.android.ui.theme.ColorError
import io.dock2dock.android.ui.theme.ColorSuccess
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryPlatinum
import io.dock2dock.android.ui.theme.PrimaryWhite
import io.dock2dock.android.ui.theme.WhiteSmoke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun ShippingContainerScreen(viewModel: ShippingContainerViewModel) {
    val shippingContainer by viewModel.shippingContainer.collectAsState(null)

    val consignmentPackages by viewModel.shippingContainerPackages.collectAsState(listOf())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val deleteMode by viewModel.deleteMode.collectAsState(false)

    val errorMessage by viewModel.errorMessage.collectAsState("")
    val showErrorDialog = errorMessage.isNotEmpty()

    val deleteModeBgColor = if (deleteMode) {
        ColorError
    } else {
        PrimaryPlatinum
    }

    val deleteModeColor = if (deleteMode) {
        PrimaryWhite
    } else {
        PrimaryOxfordBlue
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val localCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.deleteMode.collect {
            if (deleteMode) {
                localCoroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Scan barcode to remove",
                        duration = SnackbarDuration.Short
                    )
                }
            } else {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }

    shippingContainer?.let { shipContainer ->
        val notCompleted = !shipContainer.completed && shipContainer.quantity > 0
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                Column {
                    if (notCompleted) {
                        FloatingActionButton(
                            backgroundColor = deleteModeBgColor,
                            onClick = { viewModel.onDeleteModeChanged() }
                        ) {
                            Icon(
                                Icons.Filled.Remove,
                                "Floating action button.",
                                tint = deleteModeColor
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                    } else {
                        FloatingActionButton(
                            backgroundColor = PrimaryPlatinum,
                            onClick = { viewModel.reprint() }
                        ) {
                            Icon(
                                Icons.Filled.Print,
                                "Floating action button.",
                                tint = PrimaryOxfordBlue
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                    }
                    FloatingActionButton(
                        backgroundColor = PrimaryPlatinum,
                        onClick = { viewModel.refresh() }
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            "Floating action button.",
                            tint = PrimaryOxfordBlue
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    if (notCompleted) {
                        FloatingActionButton(
                            backgroundColor = PrimaryOxfordBlue,
                            onClick = { viewModel.complete() }
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                "Floating action button.",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        ) { it ->
            Column(modifier = Modifier.padding(it)) {
                Header(shippingContainer = shipContainer, deleteMode)

                LazyColumn {

                    if (!isLoading && consignmentPackages.isEmpty()) {
                        item {
                            TableNoRecords()
                        }
                    } else {
                        items(
                            items = consignmentPackages.sortedByDescending { p -> p.dateCreated },
                            key = { item -> item.barcode }
                        ) { item ->
                            ConsignmentPackageTableRow(
                                item = item
                            )
                        }
                    }
                }
            }
        }
    }

    if (isLoading) {
        TableLoading()
    }

    ErrorDialog(
        show = showErrorDialog,
        message = errorMessage,
        onDismiss = viewModel::onCloseErrorDialog,
        onConfirm = viewModel::onCloseErrorDialog
    )
}



@Composable
fun Header(shippingContainer: ShippingContainer, deleteMode: Boolean) {

    var oldQtyValue by remember { mutableIntStateOf(0) }
    var initialised by remember { mutableStateOf(false) }

    val quantity = shippingContainer.quantity

    var quantityViewBackgroundColor by remember { mutableStateOf(WhiteSmoke) }
    var quantityViewForegroundColor by remember { mutableStateOf( PrimaryDark) }

    fun setQuantityViewColor() {
        if (quantity > oldQtyValue) {
            quantityViewBackgroundColor = ColorSuccess
            quantityViewForegroundColor = PrimaryWhite
        } else if (quantity < oldQtyValue) {
            quantityViewBackgroundColor = Color.Red
            quantityViewForegroundColor = PrimaryWhite
        }
    }

    fun resetQuantityViewColor() {
        quantityViewBackgroundColor = WhiteSmoke
        quantityViewForegroundColor = PrimaryDark
    }

    LaunchedEffect(shippingContainer.quantity) {

        if (!initialised) {
            oldQtyValue = quantity
            initialised = true
            return@LaunchedEffect
        }

        setQuantityViewColor()
        delay(1.seconds)
        resetQuantityViewColor()
        oldQtyValue = quantity
    }

    data class HeaderInfo(val text: String, val value: String)

    val info = arrayOf(
        HeaderInfo("Packages", shippingContainer.quantity.toString()),
        HeaderInfo("Gross Weight", "%.2f".format(shippingContainer.weight))
    )
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)) {
        Row {
            info.forEach {
                Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .background(quantityViewBackgroundColor)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    CompositionLocalProvider(LocalContentColor provides quantityViewForegroundColor) {
                        Text(
                            text = it.value,
                            fontSize = 24.sp,
                            fontWeight = FontWeight . Bold
                        )
                        Text(
                            text = it.text
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Packages",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6,
            )
            if (shippingContainer.completed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Completed", style = MaterialTheme.typography.caption)
                    Spacer(modifier = Modifier.padding(end = 4.dp))
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ColorSuccess)
                }
            }
        }
        val showSubtitle = !shippingContainer.completed && !deleteMode
        if (showSubtitle) {
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = "Scan package barcodes to add to shipping container",
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
internal fun ConsignmentPackageTableRow(item: ShippingContainerPackage, modifier: Modifier = Modifier) {

    val barcodeShortener =
        if (item.barcode.length > 30) {
            "${item.barcode.take(20)}...${item.barcode.takeLast(8)}"
    } else if (item.barcode.length > 13) {
        "${item.barcode.take(6)}...${item.barcode.takeLast(6)}"
    } else if (item.barcode.length > 10) {
        "${item.barcode.take(4)}...${item.barcode.takeLast(6)}"
    }
    else {
        item.barcode.take(10)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp)
    ) {
        Row(modifier = modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = barcodeShortener,
                maxLines = 1,
            )
            Text(
                text = item.consignmentProductName,
                maxLines = 1,
            )
        }
        Spacer(modifier = modifier.padding(top = 4.dp))
        ConsignmentPackageDescriptionContent(item)
    }
    TableRowDivider()
}

@Composable
fun ConsignmentPackageDescriptionContent(item: ShippingContainerPackage) {
    val productNameIconId = "productNameIconId"
    val businessIconId = "businessIconId"
    val qtyIconId = "qtyIconId"
    val consignmentIconId = "consignmentIconId"
    val dateCreatedId = "dateCreatedId"

    val sdf = SimpleDateFormat("EEE MMM dd, h:mm aa")
    val dateAsString = sdf.format(item.dateCreated)

    val text = buildAnnotatedString {
        appendInlineContent(productNameIconId, "[icon]")
        append(" ")
        append(item.description)
        append(" · ")
        appendInlineContent(businessIconId, "[icon]")
        append(" ")
        append(item.customerName)
        append(" · ")
        appendInlineContent(dateCreatedId, "[icon]")
        append(" ")
        append(dateAsString)
        append(" · ")
        appendInlineContent(qtyIconId, "[icon]")
        append(" ")
        append(item.qtyDescription)
        append(" · ")
        appendInlineContent(consignmentIconId, "[icon]")
        append(" ")
        append(item.consignmentHeaderNo)
    }

    val inlineContent = mapOf(
        Pair(
            productNameIconId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(Icons.Filled.ProductionQuantityLimits,"")
            }
        ),
        Pair(
            businessIconId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(Icons.Filled.Business,"")
            }
        ),
        Pair(
            dateCreatedId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(Icons.Filled.CalendarMonth,"")
            }
        ),
        Pair(
            qtyIconId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(Icons.Filled.Scale,"")
            }
        ),
        Pair(
            consignmentIconId,
            InlineTextContent(
                Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(Icons.Filled.Tag,"")
            }
        )
    )

    Text(text = text,
        style = MaterialTheme.typography.caption,
        color = Color.Gray,
        inlineContent = inlineContent)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 300, heightDp = 300)
@Composable
internal fun PreviewHeader() {
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

    Header(shippingContainer, false)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 350)
@Composable
internal fun ConsignmentPackageTableRowPreview() {
    val shippingContainerPackage = ShippingContainerPackage(
        "7cafc3ae-edb1-42f7-820d-0d9003242dfd",
        "00090022680000000021",
        "New World Rolleston",
        "Carton",
        "CH1000",
        "Italian Shorts Sausages 350g MAP",
        13.67,
        false,
        Date()
    )

    ConsignmentPackageTableRow(shippingContainerPackage)
}