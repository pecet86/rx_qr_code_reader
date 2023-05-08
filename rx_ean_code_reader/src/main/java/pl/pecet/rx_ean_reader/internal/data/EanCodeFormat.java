package pl.pecet.rx_ean_reader.internal.data;

import com.google.mlkit.vision.barcode.common.Barcode;

import lombok.Getter;

public enum EanCodeFormat {
    UNKNOWN(-1),
    EAN_8(Barcode.FORMAT_EAN_8),
    EAN_13(Barcode.FORMAT_EAN_13),
    ITF(Barcode.FORMAT_ITF),
    UPC_A(Barcode.FORMAT_UPC_A),
    UPC_E(Barcode.FORMAT_UPC_E);

    @Getter
    private final int type;

    EanCodeFormat(int type) {
        this.type = type;
    }

    public static EanCodeFormat fromFormat(int value) {
        for (var type : EanCodeFormat.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
