package com.dock2dock.android.crossdock.dialogs

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.dock2dock.android.crossdock.viewModels.DialogPrintCrossdockLabelViewModel
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.ui.components.*

@Composable
fun DialogPrintCrossdockLabel(tokenManager: TokenManager,
                              dock2DockConfiguration: Dock2DockConfiguration,
                              visible: MutableState<Boolean>,
                              salesOrderId: String) {
    var dismissAction = { visible.value = false }
    Dialog(onDismissRequest = dismissAction)
    {
        DialogPrintCrossdockLabelUI(tokenManager, dock2DockConfiguration, visible, salesOrderId, dismissAction)
    }
}

@Composable
fun DialogPrintCrossdockLabelUI(tokenManager: TokenManager,
                                dock2DockConfiguration: Dock2DockConfiguration,
                                visible: MutableState<Boolean>,
                                salesOrderId: String,
                                onDismissRequest: () -> Unit) {
    var viewModel = DialogPrintCrossdockLabelViewModel(tokenManager, dock2DockConfiguration, salesOrderId = salesOrderId)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(size = 8.dp)
    ) {

        Column(modifier = Modifier.padding(all = 16.dp)) {

            DialogHeader(title = "Print Crossdock Label")

            Column(modifier = Modifier.verticalScroll(rememberScrollState(), true).weight(1f, false)) {
                FormItem("Sales Order Id") {
                    com.dock2dock.ui.components.TextField(
                        readOnly = true,
                        value = viewModel.salesOrderId,
                        placeholderText = "Sales Order Id"
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
                        visible.value = false
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    PrimaryButton(text = "Print", variant = ButtonVariant.Primary) {
                        viewModel.submit {
                            onDismissRequest()
                        }
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
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewDialogPrintCrossdockLabel() {
    //DialogPrintCrossdockLabel(mutableStateOf(false), salesOrderId = "12345")
}