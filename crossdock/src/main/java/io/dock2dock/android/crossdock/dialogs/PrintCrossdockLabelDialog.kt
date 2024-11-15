package io.dock2dock.android.crossdock.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.crossdock.viewModels.DialogPrintCrossdockLabelViewModel
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.ButtonVariant
import io.dock2dock.android.ui.components.Dock2DockNumberTextField
import io.dock2dock.android.ui.components.Dock2DockTextField
import io.dock2dock.android.ui.components.Dock2DockDropdown
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.PrimaryButton
import io.dock2dock.android.ui.components.SubTitleDropdownMenuItem
import io.dock2dock.android.ui.components.ValidationErrorMessage
import io.dock2dock.android.ui.dialogs.Dock2DockDialogFooter
import io.dock2dock.android.ui.dialogs.Dock2DockDialogHeader
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent

@Composable
internal fun DialogPrintCrossdockLabel(visible: Boolean,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: () -> Unit,
                              salesOrderNo: String) {
    if (visible) {
        val viewModel = DialogPrintCrossdockLabelViewModel(
            salesOrderNo = salesOrderNo,
            onSuccess = onSuccessRequest
        )

        Dialog(
            properties = DialogProperties(
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false,
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = { onDismissRequest() }
        ) {

            DialogPrintCrossdockLabelContent(viewModel, onDismissRequest)
        }

    }
}

@Composable
internal fun DialogPrintCrossdockLabelContent(viewModel: DialogPrintCrossdockLabelViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    val errorMessage by viewModel.loadError.observeAsState("")

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.imePadding().fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Dock2DockDialogHeader(title = "Print Crossdock Label", onDismissRequest)

            if (!errorMessage.isNullOrEmpty()) {
                ValidationErrorMessage(errorMessage)
            }

            Column(
                Modifier
                    .weight(1f, true)
                    .verticalScroll(scrollState)
            ) {

                FormItem("Sales Order No") {
                    Dock2DockTextField(
                        readOnly = true,
                        value = viewModel.salesOrderNo,
                        placeholderText = "Sales Order No"
                    )
                }

                FormItem("Handling Unit") {
                    Dock2DockDropdown(
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

                FormItem(title = "Printer") {
                    Dock2DockDropdown(
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
            }
            Dock2DockDialogFooter {
                OutlinedButton(
                    onClick = {
                        onDismissRequest()
                    },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f).heightIn(min = 32.dp),
                    border = BorderStroke(1.dp, PrimaryDark),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = PrimaryDark,
                        backgroundColor = Transparent
                    )
                ) {
                    Text("Cancel")
                }
                PrimaryButton(
                    text = "Print",
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Primary,
                    isLoading = isLoading
                ) {
                    viewModel.onSubmit()
                }
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
internal fun PreviewDialogPrintCrossdockLabel() {
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")
    DialogPrintCrossdockLabel(true, {}, {}, salesOrderNo = "12345")
}