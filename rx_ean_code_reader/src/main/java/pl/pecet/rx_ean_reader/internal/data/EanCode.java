package pl.pecet.rx_ean_reader.internal.data;

import android.graphics.Bitmap;

import com.google.mlkit.vision.barcode.common.Barcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EanCode {

    @Getter
    private final Barcode barcode;
    @Getter
    private final Bitmap image;


    //<editor-fold desc="delegate">
    public EanCodeType getType() {
        return EanCodeType.fromType(barcode.getValueType());
    }

    public EanCodeFormat getFormat() {
        return EanCodeFormat.fromFormat(barcode.getFormat());
    }

    public String getRawValue() {
        return barcode.getRawValue();
    }

    public String getDisplayValue() {
        return barcode.getDisplayValue();
    }
    //</editor-fold>
}
