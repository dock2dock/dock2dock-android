package io.dock2dock.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.dock2dock.android.components.LicensePlatesSheetScreen
import io.dock2dock.android.models.query.LicensePlate
import io.dock2dock.android.viewModels.LicensePlatesSheetViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class LicensePlatesFragment(
    private val salesOrderNo: String,
    private val onLicensePlateSetActive: ((LicensePlate) -> Unit) = {}
): Fragment() {

    private lateinit var viewModel: LicensePlatesSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = LicensePlatesSheetViewModel(salesOrderNo, onLicensePlateSetActive)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LicensePlatesSheetScreen(viewModel)
            }
        }
    }

    fun refresh() {
        viewModel.refresh()
    }


}