package io.dock2dock.android.events

import java.util.Date

sealed class Dock2DockEvent {
    public abstract val type: String
    public abstract val createdAt: Date
    public abstract val rawCreatedAt: String?
}

data class LicensePlateSetToActiveEvent(
    val licensePlateNo: String
)