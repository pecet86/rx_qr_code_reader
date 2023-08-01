package pl.pecet.rx_code_reader.qr.api;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import pl.pecet.rx_code_reader.qr.internal.data.QrCode;
import pl.pecet.rx_code_reader.qr.internal.ui.RxQrCodeFragment;

@ExperimentalGetImage
public class RxQrCode {

    private static final String TAG = RxQrCode.class.getSimpleName();
    @VisibleForTesting
    private final Lazy<RxQrCodeFragment> fragments;
    @IdRes
    private final int containerViewId;

    public RxQrCode(@IdRes int containerViewId, @NonNull FragmentActivity activity) {
        this.containerViewId = containerViewId;
        fragments = getLazySingleton(activity.getSupportFragmentManager());
    }

    public RxQrCode(@IdRes int containerViewId, @NonNull Fragment fragment) {
        this.containerViewId = containerViewId;
        fragments = getLazySingleton(fragment.getChildFragmentManager());
    }

    @NonNull
    private Lazy<RxQrCodeFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<>() {
            private RxQrCodeFragment fragment;

            @Override
            public synchronized RxQrCodeFragment get() {
                if (fragment == null) {
                    fragment = getRxQrCodeFragment(fragmentManager);
                }
                return fragment;
            }

            @Override
            public synchronized void remove() {
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                    fragment = null;
                }
            }
        };
    }

    private RxQrCodeFragment getRxQrCodeFragment(@NonNull FragmentManager fragmentManager) {
        RxQrCodeFragment fragment = findRxQrCodeFragment(fragmentManager);
        if (fragment == null) {
            fragment = new RxQrCodeFragment();
            fragmentManager.beginTransaction().add(containerViewId, fragment, TAG).commitNow();
        }
        return fragment;
    }

    private RxQrCodeFragment findRxQrCodeFragment(@NonNull FragmentManager fragmentManager) {
        return (RxQrCodeFragment) fragmentManager.findFragmentByTag(TAG);
    }

    private void clear() {
        log(TAG, "clear", "ok");
        fragments.remove();
    }

    public Maybe<QrCode> request(Fragment fragment) {
        return request(new QrCodeConfig(fragment.requireActivity()));
    }

    public Maybe<QrCode> request(Activity activity) {
        return request(new QrCodeConfig(activity));
    }

    public Maybe<QrCode> request(@NonNull QrCodeConfig config) {
        return Maybe
                .create((MaybeEmitter<QrCode> emitter) -> fragments.get().request(config, emitter))
                .doOnSuccess(barcode -> clear())
                .doOnComplete(this::clear)
                .doOnError(throwable -> clear())
                .doOnTerminate(this::clear)
                .doOnDispose(this::clear)
                .subscribeOn(mainThread());
    }

    //<editor-fold desc="log">
    @SuppressWarnings("SameParameterValue")
    protected boolean logOnComplete(String path, String value, Throwable th) {
        log(TAG, path, value, th);
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value, Throwable th) {
        Log.e(String.format("%s.%s", TAG, path), value, th);
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value) {
        Log.d(String.format("%s.%s", TAG, path), value);
    }
    //</editor-fold>

    private interface Lazy<V> {
        V get();

        void remove();
    }

}
