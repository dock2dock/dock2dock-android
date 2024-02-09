package io.dock2dock.android.crossdock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import io.dock2dock.android.crossdock.components.CrossdockLabelDataTable

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class CrossdockLabelsFragment(private val salesOrderNo: String): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val crossdockLabelDataTable = CrossdockLabelDataTable(salesOrderNo)
        return ComposeView(requireContext()).apply {
            setContent {
                crossdockLabelDataTable.launch()
            }
        }
    }
}