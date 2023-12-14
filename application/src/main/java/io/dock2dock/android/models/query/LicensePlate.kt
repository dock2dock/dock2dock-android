package io.dock2dock.android.models.query

import java.util.Date

data class LicensePlate(
    val no: String,
    val handlingUnitName: String,
    val locked: Boolean,
    val ssccBarcode: String?,
    val dateCreated: Date
) {
    val description: String
        get() {
            return "$no - $handlingUnitName"
        }
}