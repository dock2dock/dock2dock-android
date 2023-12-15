package io.dock2dock.android.models.commands

data class CreateLicensePlateRequest(
    val salesOrderNo: String,
    var handlingUnitId: String)

data class CreateLicensePlateResponse(
    val licensePlateNo: String)

data class CompleteLicensePlateRequest(
    val licensePlateNo: String,
    val printerId: String)

data class MoveContentBetweenLicencePlatesRequest(
    val oldLicensePlateNo: String,
    val newLicensePlateNo: String,
    val no: String,
    val description: String,
    val quantity: Double,
    val uOM: String,
    val units: Long?)