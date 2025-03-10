package pl.pecet.rx_code_reader.qr.api;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.app.Activity;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import pl.pecet.rx_code_reader.qr.internal.data.QrCodeType;
import pl.pecet.rx_code_reader.qr.internal.support.OrientationUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class QrCodeConfig {

    public static final int FLASH_MODE_AUTO = androidx.camera.core.ImageCapture.FLASH_MODE_AUTO;
    public static final int FLASH_MODE_ON = androidx.camera.core.ImageCapture.FLASH_MODE_ON;
    public static final int FLASH_MODE_OFF = androidx.camera.core.ImageCapture.FLASH_MODE_OFF;
    @NonNull
    private final Activity activity;
    private int flashMode = FLASH_MODE_AUTO;
    @With
    private QrCodeType type;
    @With
    private boolean lockOrientation;
    @With
    @IntRange(from = 720)
    private int resolution = 4000;

    @NonNull
    public QrCodeConfig withFlashMode(@FlashMode int flashMode) {
        this.flashMode = flashMode;
        return this;
    }

    public void unlockOrientation() {
        if (lockOrientation) {
            OrientationUtils.unlockOrientation(activity);
        }
    }

    @IntDef({FLASH_MODE_AUTO, FLASH_MODE_ON, FLASH_MODE_OFF})
    @Retention(SOURCE)
    @RestrictTo(LIBRARY_GROUP)
    public @interface FlashMode {
    }
}
