package io.dock2dock.android.models.query

import java.util.Date

data class CrossdockLabel(
    val id: String,
    val barcode: String,
    val isProcessed: Boolean,
    val handlingUnitName: String,
    val dateCreated: Date,
    var isDeleted: Boolean)

