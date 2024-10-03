package io.dock2dock.android.freightmanagement.dialogs

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.dock2dock.android.application.models.responses.CreateShippingContainerResponse
import io.dock2dock.android.freightmanagement.viewModels.CreateShippingContainerDialogViewModel
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.ButtonVariant
import io.dock2dock.android.ui.components.Dock2DockDropdown
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.PrimaryButton
import io.dock2dock.android.ui.components.ValidationErrorMessage
import io.dock2dock.android.ui.dialogs.Dock2DockDialogFooter
import io.dock2dock.android.ui.dialogs.Dock2DockDialogHeader
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent

@Composable
internal fun CreateShippingContainerDialog(visible: Boolean,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: (CreateShippingContainerResponse) -> Unit) {
    if (visible) {
        val viewModel = CreateShippingContainerDialogViewModel(
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

            CreateShippingContainerDialogContent(viewModel, onDismissRequest)
        }
    }
}

@Composable
internal fun CreateShippingContainerDialogContent(viewModel: CreateShippingContainerDialogViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    val errorMessage by viewModel.loadError.observeAsState("")

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    Surface(
        modifier = Modifier.imePadding().fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Dock2DockDialogHeader(title = "Create Shipping Container", onDismissRequest)

            if (errorMessage.isNotEmpty()) {
                ValidationErrorMessage(errorMessage)
            }

            Column(
                Modifier
                    .weight(1f, true)
                    .verticalScroll(scrollState)
            ) {

                FormItem("Consignment Product") {
                    Dock2DockDropdown(
                        options = viewModel.consignmentProducts,
                        selectedTextExpression = { it.name },
                        selectedText = viewModel.consignmentProductName,
                        errorMessage = viewModel.consignmentProductErrorMessage,
                        isError = viewModel.consignmentProductIsError,
                        placeholderText = "Select your Consignment Product",
                        selectedItemChanged = {
                            viewModel.onConsignmentProductValueChanged(it)
                        }
                    )
                    {
                        BasicDropdownMenuItem(it.name)
                    }
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
                    text = "Create",
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