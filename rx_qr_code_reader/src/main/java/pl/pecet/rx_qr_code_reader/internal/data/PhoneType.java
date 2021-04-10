package pl.pecet.rx_qr_code_reader.internal.data;

import com.google.mlkit.vision.barcode.Barcode;

import lombok.Getter;

public enum PhoneType {
    UNKNOWN(Barcode.Phone.TYPE_UNKNOWN),
    HOME(Barcode.Phone.TYPE_HOME),
    WORK(Barcode.Phone.TYPE_WORK),
    FAX(Barcode.Phone.TYPE_FAX),
    MOBILE(Barcode.Phone.TYPE_MOBILE);

    @Getter
    private final int type;

    PhoneType(int type) {
        this.type = type;
    }

    public static PhoneType fromType(int value) {
        for (PhoneType type : PhoneType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
