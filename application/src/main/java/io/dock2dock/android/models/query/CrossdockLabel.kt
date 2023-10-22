package io.dock2dock.android.models.query

data class CrossdockLabel(
    val id: String,
    val barcode: String,
    val handlingUnitName: String,
    var isDeleted: Boolean)
