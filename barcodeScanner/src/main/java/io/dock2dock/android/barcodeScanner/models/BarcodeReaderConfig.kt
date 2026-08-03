package io.dock2dock.android.barcodeScanner.models

data class BarcodeReaderConfig(
    val code39Enabled: Boolean = true,
    val code39MaximumLength: Int = 10,
    val code128Enabled: Boolean = true,
    val gs1_128Enabled: Boolean = true,
    val dataMatrixEnabled: Boolean = true,
    val upcAEnabled: Boolean = true,
    val ean13Enabled: Boolean = true,
    val aztecEnabled: Boolean = false,
    val codabarEnabled: Boolean = false,
    val interleaved25Enabled: Boolean = false,
    val pdf417Enabled: Boolean = false,
    val centerDecodeEnabled: Boolean = true,
    val badReadNotificationEnabled: Boolean = true,
    val goodReadNotificationEnabled: Boolean = true
)
