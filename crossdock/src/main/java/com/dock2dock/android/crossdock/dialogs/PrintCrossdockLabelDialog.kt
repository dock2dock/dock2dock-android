package com.dock2dock.android.crossdock.dialogs

import androidx.compose.material.Text
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.dock2dock.android.crossdock.viewModels.DialogPrintCrossdockLabelViewModel
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.ui.components.*

@Composable
fun DialogPrintCrossdockLabel(tokenManager: TokenManager,
                              dock2DockConfiguration: Dock2DockConfiguration,
                              visible: Boolean,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: () -> Unit,
                              salesOrderNo: String) {

    var viewModel = DialogPrintCrossdockLabelViewModel(
        tokenManager = tokenManager,
        dock2DockConfiguration =  dock2DockConfiguration,
        salesOrderNo = salesOrderNo,
        onSuccess = onSuccessRequest)

    if (visible) {
        Dialog(onDismissRequest = {
            onDismissRequest()
        })
        {
            DialogPrintCrossdockLabelUI(viewModel, onDismissRequest)
        }
    }
}

@Composable
fun DialogPrintCrossdockLabelUI(viewModel: DialogPrintCrossdockLabelViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(size = 8.dp)
    ) {

        Column(modifier = Modifier.padding(all = 16.dp)) {

            DialogHeader(title = "Print Crossdock Label")

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState(), true)
                .weight(1f, false)) {
                FormItem("Sales Order No") {
                    com.dock2dock.ui.components.TextField(
                        readOnly = true,
                        value = viewModel.salesOrderNo,
                        placeholderText = "Sales Order No"
                    )
                }

                FormItem("Handling Unit") {
                    FluentDropdown(
                        options = viewModel.handlingUnits,
                        selectedTextExpression = { it.name },
                        selectedOption = viewModel.handlingUnitId,
                        errorMessage = viewModel.handlingUnitIdErrorMessage,
                        isError = viewModel.handlingUnitIdIsError,
                        valueChanged = {
                            viewModel.onHandlingUnitIdValueChanged(it.id)
                        }
                    )
                    {
                        BasicDropdownMenuItem(it.name)
                    }
                }

                FormItem(title = "Quantity") {
                    com.dock2dock.ui.components.TextField(
                        value = viewModel.quantity.toString(),
                        valueChanged = {
                            viewModel.onQuantityValueChanged(it?.toIntOrNull() ?: 0)
                                       },
                        errorMessage = viewModel.quantityValidationMessage,
                        keyboardType = KeyboardType.Number,
                        isError = viewModel.quantityIsError,
                        placeholderText = "Quantity"
                    )
                }

                FormItem(title = "Printer") {
                    FluentDropdown(
                        options = viewModel.printers,
                        selectedTextExpression = { it.name },
                        selectedOption = viewModel.printerId,
                        errorMessage = viewModel.printerIdErrorMessage,
                        isError = viewModel.printerIdIsError,
                        valueChanged = {
                            viewModel.onPrinterIdValueChanged(it.id)
                        }) {
                        BasicDropdownMenuItem(it.name)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = 10.dp,
                    alignment = Alignment.End
                )
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    PrimaryButton(text = "Cancel") {
                        onDismissRequest()
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    PrimaryButton(text = "Print", variant = ButtonVariant.Primary, isLoading = isLoading) {
                        viewModel.onSubmit()
                    }
                }
            }
        }
    }
}

@Composable
fun DialogHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight(400),
        fontSize = 24.sp,
        style = TextStyle(lineHeight = 32.sp),
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
    )
}

@Preview
@Composable
fun PreviewDialogPrintCrossdockLabel() {
    //DialogPrintCrossdockLabel(mutableStateOf(false), salesOrderId = "12345")
}