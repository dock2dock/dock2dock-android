package io.dock2dock.android.application.events

import io.dock2dock.android.application.models.query.CrossdockHandlingUnit

data class LicensePlateSetToActiveEvent(
    val licensePlateNo: String
)

data class Dock2DockSalesOrderRetrievedEvent(
    val salesOrderNo: String,
    val isCrossdock: Boolean
)

data class Dock2DockDefaultHandlingUnitChangedEvent(
    val handlingUnit: CrossdockHandlingUnit
)

data class Dock2DockShowLPQuickCreateViewChangedEvent(
    val value: Boolean
)

data class Dock2DockLpPrintCrossdockLabelChangedEvent(
    val value: Boolean
)

