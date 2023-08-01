package pl.pecet.rx_code_reader.qr.internal.data;

import static com.google.mlkit.vision.barcode.common.Barcode.Email;

import lombok.Getter;

public enum EmailType {
    UNKNOWN(Email.TYPE_UNKNOWN),
    HOME(Email.TYPE_HOME),
    WORK(Email.TYPE_WORK);

    @Getter
    private final int type;

    EmailType(int type) {
        this.type = type;
    }

    public static EmailType fromType(int value) {
        for (var type : EmailType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
