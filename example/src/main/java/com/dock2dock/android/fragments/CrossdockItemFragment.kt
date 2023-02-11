package com.dock2dock.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.dock2dock.android.crossdock.CrossdockLabelDataTable

class CrossdockItemFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var crossdockLabelDataTable = CrossdockLabelDataTable("1234705")
        return ComposeView(requireContext()).apply {
            setContent {
                crossdockLabelDataTable.launch(requireContext())
            }
        }
    }
}