package io.dock2dock.android.barcodeScanner.models

enum class HoneywellBarcodeType(val codeId: String, val barcodeType: BarcodeType) {
    CODE39("b", BarcodeType.CODE39),
    CODE128("j", BarcodeType.CODE128),
    UPC_A("c", BarcodeType.UPC_A),
    EAN_13("d", BarcodeType.EAN_13),
    CODABAR("a", BarcodeType.CODABAR),
    CODE93("i", BarcodeType.CODE93),
    QR("s", BarcodeType.QR),
    DATAMATRIX("w", BarcodeType.DATAMATRIX),
    GS1_DATABAR("y", BarcodeType.GS1_DATABAR);

    companion object {
        fun getBarcodeType(codeId: String): BarcodeType =
            values().firstOrNull { it.codeId == codeId }?.barcodeType ?: BarcodeType.UNKNOWN
    }
}
