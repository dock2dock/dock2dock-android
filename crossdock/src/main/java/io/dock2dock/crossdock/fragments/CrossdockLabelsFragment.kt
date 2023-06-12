package io.dock2dock.crossdock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import io.dock2dock.crossdock.CrossdockLabelDataTable

class CrossdockLabelsFragment(private val salesOrderNo: String): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val crossdockLabelDataTable = CrossdockLabelDataTable(salesOrderNo)
        return ComposeView(requireContext()).apply {
            setContent {
                crossdockLabelDataTable.launch(requireContext())
            }
        }
    }
}