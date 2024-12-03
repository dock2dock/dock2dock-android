package io.dock2dock.android.freightmanagement.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.freightmanagement.viewModels.PrintConsignmentItemViewModel
import io.dock2dock.android.freightmanagement.viewModels.ShippingContainersDataTableViewModel
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.ButtonVariant
import io.dock2dock.android.ui.components.Dock2DockCheckbox
import io.dock2dock.android.ui.components.Dock2DockDropdown
import io.dock2dock.android.ui.components.Dock2DockNumberTextField
import io.dock2dock.android.ui.components.Dock2DockSwitch
import io.dock2dock.android.ui.components.Dock2DockTextArea
import io.dock2dock.android.ui.components.Dock2DockTextField
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.PrimaryButton
import io.dock2dock.android.ui.components.ValidationErrorMessage
import io.dock2dock.android.ui.dialogs.Dock2DockDialogFooter
import io.dock2dock.android.ui.dialogs.Dock2DockDialogHeader
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent
import java.util.Date

@Composable
fun PrintConsignmentItemScreen(consignmentHeaderNo: String, onDismissRequest: () -> Unit)
{
    val viewModel = viewModel {
        PrintConsignmentItemViewModel(consignmentHeaderNo) {
            onDismissRequest()
        }
    }

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    Surface(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Dock2DockDialogHeader(title = "Print Shipping Labels", onDismissRequest)

            if (errorMessage.isNotEmpty()) {
                ValidationErrorMessage(errorMessage)
            }

            Column(
                Modifier
                    .weight(1f, true)
                    .padding(top = 16.dp)
                    .verticalScroll(scrollState)
            ) {

                FormItem("Manual") {
                    Dock2DockSwitch(
                        checked = viewModel.isManual,
                        checkedChanged = {
                            viewModel.isManualValueChanged(it)
                        })
                }
                if (!viewModel.isManual) {
                    FormItem("Consignment Item") {
                        Dock2DockDropdown(
                            options = viewModel.consignmentHeaderItems,
                            selectedTextExpression = { it.description },
                            selectedText = viewModel.consignmentHeaderItemName,
                            errorMessage = viewModel.consignmentHeaderItemErrorMessage,
                            isError = viewModel.consignmentHeaderItemIsError,
                            placeholderText = "--Select--",
                            selectedItemChanged = {
                                viewModel.onConsignmentHeaderItemValueChanged(it)
                            }
                        )
                        {
                            BasicDropdownMenuItem(it.description)
                        }
                    }
                }

                if (viewModel.isManual) {
                    FormItem("Consignment Product") {
                        Dock2DockDropdown(
                            options = viewModel.consignmentProducts,
                            selectedTextExpression = { it.name },
                            selectedText = viewModel.consignmentProductName,
                            errorMessage = viewModel.consignmentProductErrorMessage,
                            isError = viewModel.consignmentProductIsError,
                            placeholderText = "--Select--",
                            selectedItemChanged = {
                                viewModel.onConsignmentProductValueChanged(it)
                            }
                        )
                        {
                            BasicDropdownMenuItem(it.name)
                        }
                    }
                }
                FormItem("Quantity") {
                    Dock2DockNumberTextField(
                        value = viewModel.quantity,
                        valueChanged = {
                            viewModel.onQuantityValueChanged(it)
                        },
                        errorMessage = viewModel.quantityErrorMessage,
                        isError = viewModel.quantityIsError,
                        placeholderText = "Quantity"
                    )
                }
                FormItem("Delivery Instructions") {
                    Dock2DockTextArea(
                        value = viewModel.deliveryInstructions ?: "",
                        placeholderText = "Delivery Instructions",
                        valueChanged = {
                            viewModel.onDeliveryInstructionsValueChanged(it)
                        }
                    )
                }
            }
            Dock2DockDialogFooter {
                OutlinedButton(
                    onClick = {
                        onDismissRequest()
                    },
                    shape = RectangleShape,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 32.dp),
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 300, heightDp = 300)
@Composable
internal fun PreviewPrintConsignmentItemScreen() {
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")

    PrintConsignmentItemScreen(
        "12345"
    ) {}
}