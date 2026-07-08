package io.dock2dock.android.barcodeScanner.models

data class BarcodeScannedEvent(
    val barcode: String,
    val barcodeType: BarcodeType
)
