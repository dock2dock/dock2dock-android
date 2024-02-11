package io.dock2dock.android.application.models.query

data class CrossdockSalesOrder(
    val id: String,
    val no: String,
    val shipped: Boolean,
    val labels: ArrayList<CrossdockLabel>,
    val isCrossdock: Boolean
)