package androidx.camera.core;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.IntRange;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QrCodeConfigure {

    @SuppressLint("RestrictedApi")
    public static void setMinLogLevel(@IntRange(from = Log.DEBUG, to = Log.ERROR) int logLevel) {
        Logger.setMinLogLevel(logLevel);
    }
}
