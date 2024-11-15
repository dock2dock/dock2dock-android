package io.dock2dock.android.barcodeScanner.models

import com.google.gson.annotations.SerializedName

enum class HoneywellBarcodeType(val codeId: String) {
    @SerializedName("0")
    EAN_13("d") {
        override val id: Int = 0
    },
    @SerializedName("1")
    GS1_128("I") {
        override val id: Int = 1
    },
    @SerializedName("2")
    QR("s") {
        override val id: Int = 2
    },
    @SerializedName("3")
    CODE39("b") {
        override val id: Int = 3
    },
    @SerializedName("4")
    CODE128("j") {
        override val id: Int = 4
    };

    abstract val id: Int

    companion object {
        fun getBarcodeTypeByName(codeId: String) = values().firstOrNull { it.codeId == codeId }
    }
}

