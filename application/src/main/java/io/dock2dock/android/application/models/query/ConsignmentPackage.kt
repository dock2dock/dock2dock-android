package io.dock2dock.android.application.models.query

import java.util.Date

data class ConsignmentPackage(
    val id: String,
    val barcode: String,
    val customerName: String,
    val consignmentProductName: String,
    val consignmentHeaderNo: String,
    val description: String,
    val quantity: Double,
    val verified:Boolean,
    val dateCreated: Date
) {

    val qtyDescription: String
        get() {
            return "${"%.2f".format(quantity)} kg"
        }
}