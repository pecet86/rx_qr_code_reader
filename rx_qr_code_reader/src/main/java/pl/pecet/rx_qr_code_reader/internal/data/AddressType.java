package pl.pecet.rx_qr_code_reader.internal.data;

import com.google.mlkit.vision.barcode.Barcode;

import lombok.Getter;

public enum AddressType {
    UNKNOWN(Barcode.Address.TYPE_UNKNOWN),
    HOME(Barcode.Address.TYPE_HOME),
    WORK(Barcode.Address.TYPE_WORK);

    @Getter
    private final int type;

    AddressType(int type) {
        this.type = type;
    }

    public static AddressType fromType(int value) {
        for (AddressType type : AddressType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
