package pl.pecet.rx_ean_reader.internal.ui;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.Surface.ROTATION_0;
import static androidx.camera.core.AspectRatio.RATIO_16_9;
import static androidx.camera.core.AspectRatio.RATIO_4_3;
import static androidx.camera.core.CameraSelector.LENS_FACING_BACK;
import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;
import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;
import static pl.pecet.rx_ean_reader.R.layout.rx_ean_reader_fragment_read;
import static pl.pecet.rx_ean_reader.R.string.rx_ean_reader_title_read;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.EanConfigure;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.List;
import java.util.function.Consumer;

import pl.pecet.rx_ean_reader.R;
import pl.pecet.rx_ean_reader.api.EanConfig;
import pl.pecet.rx_ean_reader.internal.data.EanCode;
import pl.pecet.rx_ean_reader.internal.data.EanCodeFormat;
import pl.pecet.rx_ean_reader.internal.support.EanCodeAnalyzer;
import pl.pecet.rx_ean_reader.internal.support.OrientationUtils;

@ExperimentalGetImage
public class ReadFragment extends BaseFragment {

    public static final String TAG = ReadFragment.class.getSimpleName();

    private PreviewView textureView;
    private Consumer<EanCode> listener;
    private EanConfig config;

    public ReadFragment() {
        super(rx_ean_reader_fragment_read);
    }

    void init(@NonNull EanConfig config, @NonNull Consumer<EanCode> listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(rx_ean_reader_title_read));

        textureView = requireView().findViewById(R.id.texture_view);
        startCamera();
    }

    @SuppressWarnings({"SuspiciousNameCombination", "SameParameterValue"})
    private Size getSize(@AspectRatio.Ratio int aspectRatio) {
        var width = config.getResolution();
        var height = config.getResolution();

        if (aspectRatio == RATIO_4_3) {
            height = width * 4 / 3;
        } else if (aspectRatio == RATIO_16_9) {
            height = width * 16 / 9;
        }

        return OrientationUtils.getDeviceDefaultOrientation(requireActivity()) == ORIENTATION_PORTRAIT
                ? new Size(width, height)
                : new Size(height, width);
    }


    @MainThread
    private void startCamera() {
        var cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        var executor = ContextCompat.getMainExecutor(requireContext());
        EanConfigure.setMinLogLevel(Log.ERROR);

        Size size = getSize(RATIO_4_3);

        /*System.out.printf(
                "startCamera: %d:%d\n",
                config.getResolution(), config.getResolution()
        );*/

        var cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(LENS_FACING_BACK)
                .build();
        var preview = new Preview
                .Builder()
                // We want to show input from back camera of the device
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setTargetRotation(ROTATION_0)
                .build();

        preview.setSurfaceProvider(textureView.getSurfaceProvider());


        var imageCapture = new ImageCapture
                .Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setFlashMode(config.getFlashMode())
                .setTargetRotation(ROTATION_0)
                .build();

        var imageAnalyzer = new ImageAnalysis
                .Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setTargetRotation(ROTATION_0)
                .build();

        imageAnalyzer.setAnalyzer(executor, new EanCodeAnalyzer(requireContext(), this::analyzeComplete));

        cameraProviderFuture.addListener(() -> {
            try {
                var cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider
                        .bindToLifecycle(
                                this,
                                cameraSelector,
                                preview,
                                imageCapture,
                                imageAnalyzer
                        )
                        .getCameraControl()
                        .enableTorch(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, executor);
    }

    @MainThread
    private void analyzeComplete(List<Barcode> barcodes, Bitmap bitmap) {
        if (barcodes != null && !barcodes.isEmpty()) {
            for (var barcode : barcodes) {
                if (EanCodeFormat.fromFormat(barcode.getFormat()) != EanCodeFormat.UNKNOWN) {
                    listener.accept(new EanCode(barcode, cropImage(bitmap, barcode.getBoundingBox())));
                }
            }
        }
    }

    private static Bitmap cropImage(Bitmap image, Rect rect) {
        if (rect == null || image == null) {
            return image;
        }
        try {
            return Bitmap.createBitmap(
                    image,
                    rect.left, rect.top,
                    rect.width(), rect.height()
            );
        } catch (IllegalArgumentException ex) {
            return image;
        }
    }
}
