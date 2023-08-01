package pl.pecet.rx_code_reader.ean.api;

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
import pl.pecet.rx_code_reader.ean.internal.data.EanCode;
import pl.pecet.rx_code_reader.ean.internal.ui.RxEanCodeFragment;

@ExperimentalGetImage
public class RxEanCode {

    private static final String TAG = RxEanCode.class.getSimpleName();
    @VisibleForTesting
    private final Lazy<RxEanCodeFragment> fragments;
    @IdRes
    private final int containerViewId;

    public RxEanCode(@IdRes int containerViewId, @NonNull FragmentActivity activity) {
        this.containerViewId = containerViewId;
        fragments = getLazySingleton(activity.getSupportFragmentManager());
    }

    public RxEanCode(@IdRes int containerViewId, @NonNull Fragment fragments) {
        this.containerViewId = containerViewId;
        this.fragments = getLazySingleton(fragments.getChildFragmentManager());
    }

    @NonNull
    private Lazy<RxEanCodeFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<>() {
            private RxEanCodeFragment fragment;

            @Override
            public synchronized RxEanCodeFragment get() {
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

    private RxEanCodeFragment getRxQrCodeFragment(@NonNull FragmentManager fragmentManager) {
        var fragment = findRxQrCodeFragment(fragmentManager);
        if (fragment == null) {
            fragment = new RxEanCodeFragment();
            fragmentManager.beginTransaction().add(containerViewId, fragment, TAG).commitNow();
        }
        return fragment;
    }

    private RxEanCodeFragment findRxQrCodeFragment(@NonNull FragmentManager fragmentManager) {
        return (RxEanCodeFragment) fragmentManager.findFragmentByTag(TAG);
    }

    private void clear() {
        log(TAG, "clear", "ok");
        fragments.remove();
    }

    public Maybe<EanCode> request(Fragment fragment) {
        return request(new EanConfig(fragment.requireActivity()));
    }

    public Maybe<EanCode> request(Activity activity) {
        return request(new EanConfig(activity));
    }

    public Maybe<EanCode> request(@NonNull EanConfig config) {
        return Maybe
                .create((MaybeEmitter<EanCode> emitter) -> fragments.get().request(config, emitter))
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
