package io.dock2dock.android.freightmanagement.model

import androidx.compose.ui.graphics.Color

class ConsignmentManifestStatus {
    companion object {
        fun getColor(statusId: String): Color {
            return when (statusId) {
                    "active" -> Color(0xFF36B37E)
                    "deleted" -> Color(0xFF808080)
                    "draft" -> Color(0xFFFFD60A)
                    "dispatched" -> Color(0xFF7209B7)
                    "assigned" -> Color(0xFFFF9F1C)
                else -> Color.Black // Default color for unknown status
            }
        }
    }
}

