package io.dock2dock.android.models.commands

import java.text.DecimalFormat

data class CreateLicensePlateRequest(
    val salesOrderNo: String,
    var handlingUnitId: String)

data class CompleteLicensePlateRequest(
    val licensePlateId: String,
    val printerId: String)

data class MoveContentBetweenLicencePlatesRequest(
    val oldLicensePlateNo: String,
    val newLicensePlateNo: String,
    val no: String,
    val description: String,
    val quantity: DecimalFormat,
    val uOM: String,
    val units: Long?)