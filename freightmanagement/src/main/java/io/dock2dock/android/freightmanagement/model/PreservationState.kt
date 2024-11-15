package io.dock2dock.android.freightmanagement.model

import androidx.compose.ui.graphics.Color

class PreservationState {
    companion object {
        fun getColor(preservationStateId: String): Color {
            return when (preservationStateId) {
                    "chilled" -> Color(0xFF272E81)
                    "frozen" -> Color(0xFF239BD2)
                    "ambient" -> Color(0xFF71B443)
                else -> Color.Black // Default color for unknown status
            }
        }
    }
}