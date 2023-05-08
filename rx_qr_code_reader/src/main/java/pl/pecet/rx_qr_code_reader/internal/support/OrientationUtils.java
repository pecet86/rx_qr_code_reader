package pl.pecet.rx_qr_code_reader.internal.support;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.Surface.ROTATION_0;
import static android.view.Surface.ROTATION_180;
import static android.view.Surface.ROTATION_270;
import static android.view.Surface.ROTATION_90;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

/**
 * Static methods related to device orientation.
 */
public class OrientationUtils {
    private OrientationUtils() {
    }

    /**
     * Locks the device window in landscape mode.
     */
    public static void lockOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Locks the device window in portrait mode.
     */
    public static void lockOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Locks the device window in actual screen mode.
     */
    public static void lockOrientation(Activity activity) {
        final var orientation = activity.getResources().getConfiguration().orientation;
        final var rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        if (rotation == ROTATION_0 || rotation == ROTATION_90) {
            if (orientation == ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else if (rotation == ROTATION_180 || rotation == ROTATION_270) {
            if (orientation == ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else if (orientation == ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    /**
     * Unlocks the device window in user defined screen mode.
     */
    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(SCREEN_ORIENTATION_USER);
    }

    public static int getDeviceDefaultOrientation(Activity activity) {
        var windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        var config = activity.getResources().getConfiguration();
        var rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == ROTATION_0 || rotation == ROTATION_180) && config.orientation == ORIENTATION_LANDSCAPE)
                || ((rotation == ROTATION_90 || rotation == ROTATION_270) && config.orientation == ORIENTATION_PORTRAIT)) {
            return ORIENTATION_LANDSCAPE;
        } else {
            return ORIENTATION_PORTRAIT;
        }
    }
}