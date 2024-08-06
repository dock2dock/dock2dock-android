package io.dock2dock.android.crossdock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import io.dock2dock.android.crossdock.components.CrossdockLabelDataTableUI
import io.dock2dock.android.crossdock.viewModels.CrossdockLabelDataTableViewModel

class CrossdockLabelsFragment(private val salesOrderNo: String): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = CrossdockLabelDataTableViewModel(salesOrderNo)
        return ComposeView(requireContext()).apply {
            setContent {
                CrossdockLabelDataTableUI(viewModel)
            }
        }
    }
}