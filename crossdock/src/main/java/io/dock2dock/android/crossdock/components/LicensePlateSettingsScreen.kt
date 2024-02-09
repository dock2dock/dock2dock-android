package io.dock2dock.android.crossdock.components

import androidx.annotation.RestrictTo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.crossdock.viewModels.LicensePlateSettingsViewModel
import io.dock2dock.android.ui.components.BasicDropdownMenuItem
import io.dock2dock.android.ui.components.FluentDropdown
import io.dock2dock.android.ui.theme.PrimaryOrangeWeb
import io.dock2dock.android.ui.theme.PrimaryOxfordBlue
import io.dock2dock.android.ui.theme.PrimaryWhite

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Composable
fun LicensePlateSettingsScreen(viewModel: LicensePlateSettingsViewModel) {
    LicensePlateSettingsContent(viewModel)
}

@Composable
fun LicensePlateSettingsContent(viewModel: LicensePlateSettingsViewModel) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        Column(
            Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(color = PrimaryOxfordBlue)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text("License Plate")

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    FluentDropdown(
                        modifier = Modifier.width(180.dp),
                        options = viewModel.handlingUnits,
                        selectedTextExpression = { it.name },
                        selectedText = viewModel.selectedHandlingUnitText,
                        placeholderText = "--None selected--",
                        darkTheme = true,
                        selectedItemChanged = {
                            viewModel.onSelectedHandlingUnitChanged(it)
                        }
                    )
                    {
                        BasicDropdownMenuItem(it.name)
                    }
                }

                if (viewModel.showPrintCrossdockLabel) {
                    Row(
                        Modifier.align(Alignment.CenterVertically),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Checkbox(
                            modifier = Modifier.size(20.dp).scale(0.7f),
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryOrangeWeb,
                                uncheckedColor = PrimaryWhite
                            ),
                            checked = viewModel.printCrossdockLabel,
                            onCheckedChange = {
                                viewModel.onPrintCrossdockLabelChanged(it)
                            }
                        )
                        Text(
                            "Print cross-dock label",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }


                Row(
                    Modifier.align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    IconButton(modifier = Modifier.size(24.dp), onClick = {
                        viewModel.onAdd()
                    }) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "contentDescription"
                        )
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 400)
@Composable
internal fun PreviewLicensePlateSettingsContent() {
    val context = LocalContext.current
    Dock2DockConfiguration.init(context, "")
    val viewModel = LicensePlateSettingsViewModel("SO1002")
    LicensePlateSettingsScreen(viewModel)
}