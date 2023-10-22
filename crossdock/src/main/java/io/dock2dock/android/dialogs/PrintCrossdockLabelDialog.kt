package io.dock2dock.android.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.dock2dock.android.components.BasicDropdownMenuItem
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.Dock2DockNumberTextField
import io.dock2dock.android.components.Dock2DockTextField
import io.dock2dock.android.components.FluentDropdown
import io.dock2dock.android.components.FormItem
import io.dock2dock.android.components.PrimaryButton
import io.dock2dock.android.components.SubTitleDropdownMenuItem
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent
import io.dock2dock.android.viewModels.DialogPrintCrossdockLabelViewModel

@Composable
internal fun DialogPrintCrossdockLabel(visible: Boolean,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: () -> Unit,
                              salesOrderNo: String) {
    if (visible) {
        val viewModel = DialogPrintCrossdockLabelViewModel(
            salesOrderNo = salesOrderNo,
            onSuccess = onSuccessRequest)
        Dialog(onDismissRequest = {
            onDismissRequest()
        })
        {
            DialogPrintCrossdockLabelUI(viewModel, onDismissRequest)
        }
    }
}

@Composable
internal fun DialogPrintCrossdockLabelUI(viewModel: DialogPrintCrossdockLabelViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(size = 8.dp)
    ) {

        Column(modifier = Modifier.padding(all = 24.dp)) {

            DialogHeader(title = "Print Crossdock Label", onDismissRequest)

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState(), true)
                .weight(1f, false)) {
                FormItem("Sales Order No") {
                    Dock2DockTextField(
                        readOnly = true,
                        value = viewModel.salesOrderNo,
                        placeholderText = "Sales Order No"
                    )
                }

                FormItem("Handling Unit") {
                    FluentDropdown(
                        options = viewModel.handlingUnits,
                        selectedTextExpression = { it.name },
                        selectedText = viewModel.handlingUnitName,
                        errorMessage = viewModel.handlingUnitIdErrorMessage,
                        isError = viewModel.handlingUnitIdIsError,
                        placeholderText = "Select your handling unit",
                        selectedItemChanged = {
                            viewModel.onHandlingUnitValueChanged(it)
                        }
                    )
                    {
                        BasicDropdownMenuItem(it.name)
                    }
                }

                FormItem(title = "Quantity") {
                    Dock2DockNumberTextField(
                        value = viewModel.quantity,
                        valueChanged = {
                            viewModel.onQuantityValueChanged(it)
                        },
                        errorMessage = viewModel.quantityValidationMessage,
                        isError = viewModel.quantityIsError,
                        placeholderText = "Quantity"
                    )
                }

                FormItem(title = "Printer") {
                    FluentDropdown(
                        options = viewModel.printers,
                        selectedTextExpression = { it.name },
                        selectedText = viewModel.printerName,
                        errorMessage = viewModel.printerIdErrorMessage,
                        isError = viewModel.printerIdIsError,
                        placeholderText = "Select your printer",
                        selectedItemChanged = {
                            viewModel.onPrinterValueChanged(it)
                        }) {
                        SubTitleDropdownMenuItem(it.name, it.location)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismissRequest()
                    },
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, PrimaryDark),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = PrimaryDark,
                        backgroundColor = Transparent
                    ),
                    modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                PrimaryButton(
                    text = "Print",
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Primary,
                    isLoading = isLoading) {
                    viewModel.onSubmit()
                }
            }
        }
    }
}

@Composable
internal fun DialogHeader(title: String, close: (() -> Unit)) {
    Row(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        IconButton(onClick = {
            close()
        }, modifier = Modifier.size(24.dp)) {
            Icon(

                Icons.Filled.Clear,
                "contentDescription"
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight(400),
            fontSize = 18.sp,
            style = TextStyle(lineHeight = 32.sp),
        )

    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
internal fun PreviewDialogPrintCrossdockLabel() {
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")
    DialogPrintCrossdockLabel(true, {}, {}, salesOrderNo = "12345")
}