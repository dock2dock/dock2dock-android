package io.dock2dock.android.models.query

data class CrossdockSalesOrder(
    val id: String,
    val no: String,
    val shipped: Boolean,
    val labels: ArrayList<CrossdockLabel>
)