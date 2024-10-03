package io.dock2dock.android.application.models.query

data class ConsignmentManifestItem(
    val consignmentManifestId: String,
    val consignmentProductTypeId: String,
    val consignmentProductId: String,
    val consignmentProductName: String,
    val weight: Double,
    val quantity: Double,
    val pallets: Int,
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