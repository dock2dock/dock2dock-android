package io.dock2dock.android.application.models.query

import java.util.Date

data class ShippingContainer(
    val id: String,
    val barcode: String,
    val consignmentProductId: String,
    val consignmentProductName: String,
    val consignmentHeaderId:String?,
    val consignmentHeaderNo:String?,
    val quantity: Int,
    val weight: Double,
    val completed:Boolean,
    val consignmentManifestId:String?,
    val consignmentManifestNo:String?,
    val lastUpdatedDate: Date,
    val dateCreated: Date,
    val shippingContainerPackages: List<ShippingContainerPackage>) {

    val weightDescription: String
        get() {
            return "$quantity - ${"%.2f".format(weight)} kg"
        }
}

