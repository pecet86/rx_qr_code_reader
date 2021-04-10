package pl.pecet.rx_qr_code_reader.internal.data;

import com.google.mlkit.vision.barcode.Barcode;

import lombok.Getter;

public enum WiFiEncryptionType {
    OPEN(Barcode.WiFi.TYPE_OPEN),
    WPA(Barcode.WiFi.TYPE_WPA),
    WEP(Barcode.WiFi.TYPE_WEP);

    @Getter
    private final int type;

    WiFiEncryptionType(int type) {
        this.type = type;
    }

    public static WiFiEncryptionType fromType(int value) {
        for (WiFiEncryptionType type : WiFiEncryptionType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("wrong value");
    }
}
