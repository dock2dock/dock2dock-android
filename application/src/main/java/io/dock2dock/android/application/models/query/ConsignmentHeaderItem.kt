package io.dock2dock.android.application.models.query

data class ConsignmentHeaderItem(
    val id: String,
    val consignmentHeaderId: String,
    val consignmentHeaderNo: String,
    val consignmentProductId: String,
    val consignmentProductTypeId: String,
    val consignmentProductName: String,
    val weight: Double,
    val length: Double,
    val width: Double,
    val height: Double,
    val quantity: Double,
    val pallets: Int,
    val locked: Boolean
) {
    val description: String
        get() {
            return if (isPallet) {
                "$consignmentProductName · Qty: $pallets · Wgt: $weight"
            } else {
                "$consignmentProductName · Qty: ${"%.0f".format(quantity)} · Wgt: $weight"
            }
        }

    val isPallet: Boolean
        get() = consignmentProductTypeId == "pallet"
}