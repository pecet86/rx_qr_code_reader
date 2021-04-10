package pl.pecet.rx_qr_code_reader.internal.ui;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.experimental.UseExperimental;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalLogging;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.QrCodeConfigure;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import pl.pecet.rx_qr_code_reader.R;
import pl.pecet.rx_qr_code_reader.api.QrCodeConfig;
import pl.pecet.rx_qr_code_reader.internal.data.QrCode;
import pl.pecet.rx_qr_code_reader.internal.support.QrCodeAnalyzer;

import static android.view.Surface.ROTATION_0;
import static pl.pecet.rx_qr_code_reader.R.layout.rx_qr_code_reader_fragment_read;
import static pl.pecet.rx_qr_code_reader.R.string.rx_qr_code_reader_title_read;
import static pl.pecet.rx_qr_code_reader.internal.support.OrientationUtils.getDeviceDefaultOrientation;

@UseExperimental(markerClass = ExperimentalLogging.class)
public class ReadFragment extends BaseFragment {

    public static final String TAG = ReadFragment.class.getSimpleName();

    private PreviewView textureView;
    private Consumer<QrCode> listener;
    private QrCodeConfig config;

    public ReadFragment() {
        super(rx_qr_code_reader_fragment_read);
    }

    void init(@NonNull QrCodeConfig config, @NonNull Consumer<QrCode> listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(rx_qr_code_reader_title_read));

        textureView = requireView().findViewById(R.id.texture_view);
        startCamera();

    }

    @SuppressWarnings({"SuspiciousNameCombination", "SameParameterValue"})
    private Size getSize(@AspectRatio.Ratio int aspectRatio) {
        int width = config.getResolution();
        int height = config.getResolution();

        if (aspectRatio == AspectRatio.RATIO_4_3) {
            height = width * 4 / 3;
        } else if (aspectRatio == AspectRatio.RATIO_16_9) {
            height = width * 16 / 9;
        }

        return getDeviceDefaultOrientation(requireActivity()) == Configuration.ORIENTATION_PORTRAIT
                ? new Size(width, height)
                : new Size(height, width);
    }


    @MainThread
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider
                .getInstance(requireContext());
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        QrCodeConfigure.setMinLogLevel(Log.ERROR);

        Size size = getSize(AspectRatio.RATIO_4_3);

        System.out.printf(
                "startCamera: %d:%d\n",
                config.getResolution(), config.getResolution()
        );

        CameraSelector cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview
                .Builder()
                // We want to show input from back camera of the device
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setTargetRotation(ROTATION_0)
                .build();

        preview.setSurfaceProvider(textureView.getSurfaceProvider());


        ImageCapture imageCapture = new ImageCapture
                .Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setFlashMode(config.getFlashMode())
                .setTargetRotation(ROTATION_0)
                .build();

        ImageAnalysis imageAnalyzer = new ImageAnalysis
                .Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(size)
                //.setTargetAspectRatio(aspectRatio)
                .setTargetRotation(ROTATION_0)
                .build();

        imageAnalyzer.setAnalyzer(executor, new QrCodeAnalyzer(requireContext(), this::analyzeComplete));

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
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
            for (Barcode barcode : barcodes) {
                if (barcode.getFormat() == Barcode.FORMAT_QR_CODE) {
                    listener.accept(new QrCode(barcode, cropImage(bitmap, barcode.getBoundingBox())));
                }
            }
        }
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
}
