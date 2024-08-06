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