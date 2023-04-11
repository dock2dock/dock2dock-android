package io.dock2dock.application.models.query

data class CrossdockLabel(val id: String, val barcode: String, val handlingUnitName: String, var isDeleted: Boolean);
