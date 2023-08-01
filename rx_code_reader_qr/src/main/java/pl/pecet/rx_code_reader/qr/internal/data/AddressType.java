package pl.pecet.rx_code_reader.qr.internal.data;

import static com.google.mlkit.vision.barcode.common.Barcode.Address;

import lombok.Getter;

public enum AddressType {
    UNKNOWN(Address.TYPE_UNKNOWN),
    HOME(Address.TYPE_HOME),
    WORK(Address.TYPE_WORK);

    @Getter
    private final int type;

    AddressType(int type) {
        this.type = type;
    }

    public static AddressType fromType(int value) {
        for (var type : AddressType.values()) {
            if (type.type == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
