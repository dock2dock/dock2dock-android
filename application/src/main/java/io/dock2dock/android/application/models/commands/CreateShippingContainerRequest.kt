package io.dock2dock.android.application.models.commands

data class CreateShippingContainerRequest(
    val consignmentProductId: String)

data class AddPackageToShippingContainerRequest(
    val shippingContainerId: String,
    val barcode: String)

data class RemovePackageFromShippingContainerRequest(
    val shippingContainerId: String,
    val barcode: String)

data class CompleteShippingContainerRequest(
    val shippingContainerId: String,
    val printerId: String)

data class ReprintShippingContainerRequest(
    val shippingContainerId: String,
    val printerId: String)

data class PrintConsignmentItemShippingLabelsRequest(
    val consignmentHeaderItemId: String,
    val printerId: String,
    val quantity: Int,
    val deliveryInstructions: String?)

data class PrintConsignmentManifestItemShippingLabelsRequest(
    val consignmentManifestId: String,
    val consignmentProductId: String,
    val printerId: String,
    val quantity: Int,
    val deliveryInstructions: String?)