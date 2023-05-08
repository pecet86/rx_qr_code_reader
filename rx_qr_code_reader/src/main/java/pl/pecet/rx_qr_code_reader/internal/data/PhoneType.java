package pl.pecet.rx_qr_code_reader.internal.data;

import static com.google.mlkit.vision.barcode.common.Barcode.Phone;

import lombok.Getter;

public enum PhoneType {
    UNKNOWN(Phone.TYPE_UNKNOWN),
    HOME(Phone.TYPE_HOME),
    WORK(Phone.TYPE_WORK),
    FAX(Phone.TYPE_FAX),
    MOBILE(Phone.TYPE_MOBILE);

    @Getter
    private final int type;

    PhoneType(int type) {
        this.type = type;
    }

    public static PhoneType fromType(int value) {
        for (var type : PhoneType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
