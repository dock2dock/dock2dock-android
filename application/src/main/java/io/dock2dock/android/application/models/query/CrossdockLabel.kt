package io.dock2dock.android.application.models.query

import java.util.Date

data class CrossdockLabel(
    val id: String,
    val barcode: String,
    val isProcessed: Boolean,
    val handlingUnitName: String,
    val dateCreated: Date,
    var isDeleted: Boolean)

