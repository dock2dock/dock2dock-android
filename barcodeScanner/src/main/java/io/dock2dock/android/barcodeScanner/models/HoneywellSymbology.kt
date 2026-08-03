package io.dock2dock.android.barcodeScanner.models

enum class HoneywellSymbology(val codeId: String, val barcodeType: BarcodeType) {
    CODABAR("a", BarcodeType.CODABAR),
    CODE39("b", BarcodeType.CODE39),
    UPC_A("c", BarcodeType.UPC_A),
    EAN_13("d", BarcodeType.EAN_13),
    I25("e", BarcodeType.I25),
    CODE93("i", BarcodeType.CODE93),
    CODE128("j", BarcodeType.CODE128),
    PDF417("r", BarcodeType.PDF417),
    QR("s", BarcodeType.QR),
    DATAMATRIX("w", BarcodeType.DATAMATRIX),
    MAXICODE("x", BarcodeType.MAXICODE),
    GS1_DATABAR("y", BarcodeType.GS1_DATABAR),
    AZTEC_CODE("z", BarcodeType.AZTEC_CODE),
    EAN_8("D", BarcodeType.EAN_8),
    UPC_E("E", BarcodeType.UPC_E),
    GS1_128("I", BarcodeType.GS1_128);

    companion object {
        private val byCodeId = values().associateBy { it.codeId }

        fun fromCodeId(codeId: String?): BarcodeType =
            codeId?.let { byCodeId[it]?.barcodeType } ?: BarcodeType.UNKNOWN
    }
}
