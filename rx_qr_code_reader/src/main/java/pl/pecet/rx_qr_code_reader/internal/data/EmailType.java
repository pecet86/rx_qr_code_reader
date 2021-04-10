package pl.pecet.rx_qr_code_reader.internal.data;

import com.google.mlkit.vision.barcode.Barcode;

import lombok.Getter;

public enum EmailType {
    UNKNOWN(Barcode.Email.TYPE_UNKNOWN),
    HOME(Barcode.Email.TYPE_HOME),
    WORK(Barcode.Email.TYPE_WORK);

    @Getter
    private final int type;

    EmailType(int type) {
        this.type = type;
    }

    public static EmailType fromType(int value) {
        for (EmailType type : EmailType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
