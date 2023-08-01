package pl.pecet.rx_code_reader.qr.internal.ui;

import static android.Manifest.permission.CAMERA;
import static android.widget.Toast.LENGTH_SHORT;
import static pl.pecet.rx_code_reader.qr.R.id.qr_code_fragment;
import static pl.pecet.rx_code_reader.qr.R.layout.rx_code_reader_fragment;
import static pl.pecet.rx_code_reader.qr.R.string.rx_code_reader_wrong_type;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.MaybeEmitter;
import pl.pecet.rx_code_reader.qr.api.QrCodeConfig;
import pl.pecet.rx_code_reader.qr.internal.data.QrCode;
import pl.pecet.rx_code_reader.qr.internal.support.OrientationUtils;

@ExperimentalGetImage
public class RxQrCodeFragment extends BaseFragment {

    private static final String[] PERMISSIONS;

    static {
        List<String> list = Collections.singletonList(CAMERA);
        PERMISSIONS = list.toArray(new String[0]);
    }

    private RxPermissions rxPermissions;
    private ReadFragment readFragment;
    private ViewFragment viewFragment;
    private MaybeEmitter<QrCode> emitter;
    private QrCodeConfig config;

    public RxQrCodeFragment() {
        super(rx_code_reader_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rxPermissions = new RxPermissions(this);

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(requireActivity(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        var fragment = getChildFragmentManager().findFragmentById(qr_code_fragment);
                        if (fragment instanceof ReadFragment) {
                            emitter.onComplete();
                        } else if (fragment instanceof ViewFragment) {
                            toRead();
                        } else {
                            emitter.onComplete();
                        }
                    }
                });
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof ReadFragment a) {
            initReadFragment(a);
        } else if (childFragment instanceof ViewFragment b) {
            initViewFragment(b);
        }
    }

    @Override
    public void onDestroyView() {
        rxPermissions = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //OrientationUtils.unlockOrientation(requireActivity());
        super.onDestroy();
    }

    @MainThread
    private void init() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(qr_code_fragment, getReadFragment())
                .addToBackStack(ReadFragment.TAG)
                .commit();
    }

    public void request(@NonNull QrCodeConfig config, @NonNull MaybeEmitter<QrCode> emitter) {
        this.config = config;
        this.emitter = emitter;
        if (config.isLockOrientation()) {
            OrientationUtils.lockOrientation(config.getActivity());
        }
        initAndroidPermissions();
    }

    private ReadFragment getReadFragment() {
        if (readFragment == null) {
            readFragment = new ReadFragment();
        }
        return readFragment;
    }

    private ViewFragment getViewFragment() {
        if (viewFragment == null) {
            viewFragment = new ViewFragment();
        }
        return viewFragment;
    }

    void initReadFragment(@NonNull ReadFragment fragment) {
        fragment.init(config, this::toView);
    }

    void initViewFragment(@NonNull ViewFragment fragment) {
        fragment.init(config, new ViewFragment.Listener() {
            @Override
            public void replay() {
                toRead();
            }

            @Override
            public void check(@NonNull QrCode qrCode) {
                emitter.onSuccess(qrCode);
            }

            @Override
            public void clear() {
                emitter.onComplete();
            }
        });
    }

    private synchronized void toRead() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(qr_code_fragment, getReadFragment())
                .addToBackStack(ReadFragment.TAG)
                .commit();
    }

    private synchronized void toView(QrCode qrCode) {
        if (config.getType() != null && !Objects.equals(qrCode.getType(), config.getType())) {
            Toast.makeText(requireContext(), rx_code_reader_wrong_type, LENGTH_SHORT).show();
            return;
        }

        getViewFragment().setQrCode(qrCode);

        getChildFragmentManager()
                .beginTransaction()
                .replace(qr_code_fragment, getViewFragment())
                .addToBackStack(ViewFragment.TAG)
                .commit();
    }

    //<editor-fold desc="Permissions">
    @SuppressLint("AutoDispose")
    @MainThread
    private void initAndroidPermissions() {
        if (!isGranted(rxPermissions, PERMISSIONS)) {
            rxPermissions
                    .requestEach(PERMISSIONS)
                    .doOnComplete(this::initAndroidPermissions)
                    .ignoreElements()
                    .onErrorComplete(th -> logOnComplete("initAndroidPermissions", th.getMessage(), th))
                    .subscribe();
        } else {
            init();
        }
    }

    public static boolean isGranted(RxPermissions rxPermissions, String permission) {
        return rxPermissions.isGranted(permission);
    }

    public static boolean isGranted(RxPermissions rxPermissions, String... permissions) {
        for (var permission : permissions) {
            if (!isGranted(rxPermissions, permission)) {
                return false;
            }
        }
        return true;
    }
    //</editor-fold>
}
