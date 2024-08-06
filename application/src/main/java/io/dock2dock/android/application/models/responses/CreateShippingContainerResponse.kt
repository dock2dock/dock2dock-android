package io.dock2dock.android.application.models.responses

data class CreateShippingContainerResponse(
    val shippingContainerId: String,
    val barcode: String)

data class AddPackageToShippingContainerResponse(
    val consignmentPackageBarcode: String,
    val consignmentProductName: String,
    val consignmentHeaderNo: String,
    val customerName: String,
    val description: String,
    val quantity: Double)
