package pl.pecet.rx_code_reader.ean.internal.data;

import com.google.mlkit.vision.barcode.common.Barcode;

import lombok.Getter;

public enum EanCodeType {
    UNKNOWN(Barcode.TYPE_UNKNOWN),
    PRODUCT(Barcode.TYPE_PRODUCT),
    TEXT(Barcode.TYPE_TEXT);

    @Getter
    private final int type;

    EanCodeType(int type) {
        this.type = type;
    }

    public static EanCodeType fromType(int value) {
        for (EanCodeType type : EanCodeType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
