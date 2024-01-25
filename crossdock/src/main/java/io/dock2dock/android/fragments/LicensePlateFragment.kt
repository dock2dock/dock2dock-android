package io.dock2dock.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import io.dock2dock.android.components.LicensePlateLauncher
import io.dock2dock.android.viewModels.LicensePlateViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class LicensePlateFragment(val viewModel: LicensePlateViewModel): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val licensePlateScreen = LicensePlateLauncher(viewModel)
        return ComposeView(requireContext()).apply {
            setContent {
                licensePlateScreen.launch()
            }
        }
    }
}
