package io.dock2dock.android.dialogs.licensePlate

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
import io.dock2dock.android.components.BasicDropdownMenuItem
import io.dock2dock.android.components.ButtonVariant
import io.dock2dock.android.components.FluentDropdown
import io.dock2dock.android.components.FormItem
import io.dock2dock.android.components.PrimaryButton
import io.dock2dock.android.components.ValidationErrorMessage
import io.dock2dock.android.dialogs.Dock2DockDialogFooter
import io.dock2dock.android.dialogs.Dock2DockDialogHeader
import io.dock2dock.android.ui.theme.PrimaryDark
import io.dock2dock.android.ui.theme.Transparent
import io.dock2dock.android.viewModels.AddLicensePlateDialogViewModel

@Composable
internal fun AddLicensePlateDialog(visible: Boolean,
                                   salesOrderNo: String,
                              onDismissRequest: () -> Unit,
                              onSuccessRequest: () -> Unit) {
    if (visible) {
        val viewModel = AddLicensePlateDialogViewModel(salesOrderNo, onSuccessRequest)
        Dialog(onDismissRequest = {
            onDismissRequest()
        })
        {
            AddLicensePlateDialogContent(viewModel, onDismissRequest)
        }
    }
}

@Composable
internal fun AddLicensePlateDialogContent(viewModel: AddLicensePlateDialogViewModel, onDismissRequest: () -> Unit) {

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    val errorMessage by viewModel.loadError.observeAsState("")

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

            Dock2DockDialogHeader(title = "Add License Plate", onDismissRequest)

            if (!errorMessage.isNullOrEmpty()) {
                ValidationErrorMessage(errorMessage)
            }

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState(), true)
                .weight(1f, false)) {

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
                    text = "Add",
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Primary,
                    isLoading = isLoading) {
                    viewModel.onSubmit()
                }
            }
        }
    }
}