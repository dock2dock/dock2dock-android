package io.dock2dock.android.crossdock.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.dock2dock.android.application.models.query.CrossdockLabel
import io.dock2dock.android.crossdock.viewModels.ReprintCrossdockLabelViewModel
import io.dock2dock.android.ui.components.ButtonVariant
import io.dock2dock.android.ui.components.Dock2DockTextField
import io.dock2dock.android.ui.components.FluentDropdown
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.PrimaryButton
import io.dock2dock.android.ui.components.SubTitleDropdownMenuItem
import io.dock2dock.android.ui.components.ValidationErrorMessage
import io.dock2dock.android.ui.dialogs.Dock2DockDialogFooter
import io.dock2dock.android.ui.dialogs.Dock2DockDialogHeader
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent

@Composable
internal fun DialogReprintCrossdockLabel(visible: Boolean,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: () -> Unit,
                              crossdockLabel: CrossdockLabel
) {
    if (visible) {
        val viewModel = ReprintCrossdockLabelViewModel(
            crossdockLabel = crossdockLabel,
            onSuccess = onSuccessRequest)
        Dialog(onDismissRequest = {
            onDismissRequest()
        })
        {
            DialogReprintCrossdockLabelContent(viewModel, onDismissRequest)
        }
    }
}

@Composable
internal fun DialogReprintCrossdockLabelContent(viewModel: ReprintCrossdockLabelViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    val errorMessage by viewModel.loadError.observeAsState("")
    val barcode = viewModel.crossdockLabel.barcode

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

            Dock2DockDialogHeader(title = "Reprint Crossdock Label", onDismissRequest)

            if (!errorMessage.isNullOrEmpty()) {
                ValidationErrorMessage(errorMessage)
            }

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState(), true)
                .weight(1f, false)) {
                FormItem("Barcode") {
                    Dock2DockTextField(
                        readOnly = true,
                        value = barcode,
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
            Dock2DockDialogFooter {
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