package io.dock2dock.android.application.models.query

data class LicensePlateLine(
    val no : String,
    val description : String,
    val quantity: Double,
    val uom : String,
    val units: Long?
) {
    val noDescription: String
        get() {
            return "$no · $description"
        }

    val quantityDescription: String
        get() {
            return "${"%.2f".format(quantity)} $uom"
        }
}
