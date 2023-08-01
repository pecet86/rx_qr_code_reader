package pl.pecet.rx_code_reader.qr.internal.data;

import static com.google.mlkit.vision.barcode.common.Barcode.WiFi;

import lombok.Getter;

public enum WiFiEncryptionType {
    OPEN(WiFi.TYPE_OPEN),
    WPA(WiFi.TYPE_WPA),
    WEP(WiFi.TYPE_WEP);

    @Getter
    private final int type;

    WiFiEncryptionType(int type) {
        this.type = type;
    }

    public static WiFiEncryptionType fromType(int value) {
        for (var type : WiFiEncryptionType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("wrong value");
    }
}
