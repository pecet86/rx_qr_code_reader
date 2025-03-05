package pl.pecet.rx_code_reader.qr.internal.ui;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.Surface.ROTATION_0;
import static androidx.camera.core.AspectRatio.RATIO_16_9;
import static androidx.camera.core.AspectRatio.RATIO_4_3;
import static androidx.camera.core.AspectRatio.Ratio;
import static androidx.camera.core.CameraSelector.LENS_FACING_BACK;
import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;
import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;
import static com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE;
import static pl.pecet.rx_code_reader.qr.R.id.texture_view;
import static pl.pecet.rx_code_reader.qr.R.layout.rx_code_reader_fragment_read;
import static pl.pecet.rx_code_reader.qr.R.string.rx_code_reader_title_read;
import static pl.pecet.rx_code_reader.qr.internal.support.OrientationUtils.getDeviceDefaultOrientation;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.QrCodeConfigure;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.List;
import java.util.function.Consumer;

import pl.pecet.rx_code_reader.qr.api.QrCodeConfig;
import pl.pecet.rx_code_reader.qr.internal.data.QrCode;
import pl.pecet.rx_code_reader.qr.internal.support.QrCodeAnalyzer;

@ExperimentalGetImage
public class ReadFragment extends BaseFragment {

    public static final String TAG = ReadFragment.class.getSimpleName();

    private PreviewView textureView;
    private Consumer<QrCode> listener;
    private QrCodeConfig config;

    public ReadFragment() {
        super(rx_code_reader_fragment_read);
    }

    private static Bitmap cropImage(Bitmap image, Rect rect) {
        if (rect == null || image == null) {
            return image;
        }
        return Bitmap.createBitmap(
                image,
                rect.left, rect.top,
                rect.width(), rect.height()
        );
    }

    void init(@NonNull QrCodeConfig config, @NonNull Consumer<QrCode> listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(rx_code_reader_title_read));

        textureView = requireView().findViewById(texture_view);
        startCamera();

    }

    @SuppressWarnings({"SuspiciousNameCombination", "SameParameterValue"})
    private Size getSize(@Ratio int aspectRatio) {
        var width = config.getResolution();
        var height = config.getResolution();

        if (aspectRatio == RATIO_4_3) {
            height = width * 4 / 3;
        } else if (aspectRatio == RATIO_16_9) {
            height = width * 16 / 9;
        }

        return getDeviceDefaultOrientation(requireActivity()) == ORIENTATION_PORTRAIT
                ? new Size(width, height)
                : new Size(height, width);
    }

    @MainThread
    private void startCamera() {
        var cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        var executor = ContextCompat.getMainExecutor(requireContext());
        QrCodeConfigure.setMinLogLevel(Log.ERROR);

        var size = getSize(RATIO_4_3);

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

        imageAnalyzer.setAnalyzer(executor, new QrCodeAnalyzer(requireContext(), this::analyzeComplete));

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
                if (barcode.getFormat() == FORMAT_QR_CODE) {
                    listener.accept(new QrCode(barcode, cropImage(bitmap, barcode.getBoundingBox())));
                }
            }
        }
    }
}
