package io.dock2dock.settings.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.models.query.Printer
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.Dock2DockSwitch
import io.dock2dock.android.ui.components.Dock2DockDropdown
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.FormItemLayout
import io.dock2dock.android.ui.components.FormSectionHeader
import io.dock2dock.android.ui.components.SubTitleDropdownMenuItem
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite
import io.dock2dock.settings.ui.viewModels.SettingsDialogViewModel

@Composable
public fun SettingsDialog(visible: Boolean, onDismissRequest: () -> Unit) {
    if (visible) {
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
        })
        {
            SettingsDialogUI(viewModel(), onDismissRequest)
        }
    }
}

@Composable
internal fun SettingsDialogUI(viewModel: SettingsDialogViewModel = viewModel(), onDismissRequest: () -> Unit) {

    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = PrimaryOxfordBlue,
                    contentColor = PrimaryWhite,
                    title = {
                        Text("Settings")
                    },
                    navigationIcon = {
                        IconButton(onClick = { onDismissRequest() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(24.dp)) {

                FormSectionHeader("Defaults") {
                    FormItem("Handling Unit") {
                        Dock2DockDropdown(
                            options = viewModel.handlingUnits,
                            selectedTextExpression = { it.name },
                            selectedText = viewModel.selectedHandlingUnitText,
                            placeholderText = "Please select your handling unit",
                            selectedItemChanged = {
                                viewModel.onHandlingUnitValueChanged(it)
                            }
                        )
                        {
                            BasicDropdownMenuItem(it.name)
                        }
                    }
                    FormItem("Printer") {
                        Dock2DockDropdown(
                            options = viewModel.printers,
                            selectedTextExpression = { it.name },
                            selectedText = viewModel.selectedPrinterText,
                            placeholderText = "Please select your printer",
                            selectedItemChanged = {
                                viewModel.onPrinterValueChanged(it)
                            }
                        )
                        {
                            SubTitleDropdownMenuItem(it.name, it.location)
                        }
                    }
                    FormItem("Pickup Location") {
                        Dock2DockDropdown(
                            options = viewModel.pickupLocations,
                            selectedTextExpression = { it.name },
                            selectedText = viewModel.selectedPickupLocationText,
                            placeholderText = "Please select your pickup location",
                            selectedItemChanged = {
                                viewModel.onPickupLocationValueChanged(it)
                            }
                        )
                        {
                            BasicDropdownMenuItem(it.name)
                        }
                    }
                }

                FormSectionHeader("License Plate") {
                    FormItem("Show quick create view", FormItemLayout.Horizontal) {
                        Dock2DockSwitch(viewModel.showLpQuickCreateView) {
                            viewModel.onShowLpQuickCreateViewChanged(it)
                        }

                    }
                    FormItem("Print cross-dock label", FormItemLayout.Horizontal) {
                        Dock2DockSwitch(viewModel.lpPrintCrossdockLabel) {
                            viewModel.onLpPrintCrossdockLabelChanged(it)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 800)
@Composable
internal fun PreviewSettingsDialog() {
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")
    SettingsDialog(true) {}
}