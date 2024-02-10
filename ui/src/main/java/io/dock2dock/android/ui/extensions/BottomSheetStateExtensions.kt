package io.dock2dock.android.ui.extensions

import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetState.expandProgress: Float
    get() {
        return when (progress.from) {
            BottomSheetValue.Collapsed -> {
                when (progress.to) {
                    BottomSheetValue.Collapsed -> 0f
                    BottomSheetValue.Expanded -> progress.fraction
                }
            }
            BottomSheetValue.Expanded -> {
                when (progress.to) {
                    BottomSheetValue.Collapsed -> 1f - progress.fraction
                    BottomSheetValue.Expanded -> 1f
                }
            }
        }
    }