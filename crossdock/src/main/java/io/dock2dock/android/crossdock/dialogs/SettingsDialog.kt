package io.dock2dock.android.crossdock.dialogs

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
import io.dock2dock.android.crossdock.viewModels.SettingsDialogViewModel
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.FluentDropdown
import io.dock2dock.android.ui.components.FormItem
import io.dock2dock.android.ui.components.SubTitleDropdownMenuItem
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite

@Composable
internal fun SettingsDialog(visible: Boolean, onDismissRequest: () -> Unit,) {
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

                    FormItem("Default Handling Unit") {
                        FluentDropdown(
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
                    FormItem("Default Printer") {
                        FluentDropdown(
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