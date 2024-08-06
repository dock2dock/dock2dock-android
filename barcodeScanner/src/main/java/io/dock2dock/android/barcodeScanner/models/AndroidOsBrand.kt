package io.dock2dock.android.barcodeScanner.models

enum class AndroidOsBrand {
    Honeywell {
        override val id: String = "Honeywell"
    };

    abstract val id: String
}