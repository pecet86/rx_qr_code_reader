package pl.pecet.rx_qr_code_reader.internal.support;

import static com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;

@ExperimentalGetImage
public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {

    private final Context context;
    private final BiConsumer<List<Barcode>, Bitmap> onQrCodesDetected;

    public QrCodeAnalyzer(@NonNull Context context, @NonNull BiConsumer<List<Barcode>, Bitmap> onQrCodesDetected) {
        this.context = context;
        this.onQrCodesDetected = onQrCodesDetected;
    }

    private static BarcodeScannerOptions getOptions() {
        return new BarcodeScannerOptions
                .Builder()
                .setBarcodeFormats(FORMAT_QR_CODE)
                .build();
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        var image = imageProxy.getImage();
        if (image != null) {
            new Analyzer(imageProxy, image).analyze();
        }
    }

    private static ByteBuffer imageToByteBuffer(final Image image) {
        final var crop = image.getCropRect();
        final var width = crop.width();
        final var height = crop.height();

        final var planes = image.getPlanes();
        final var rowData = new byte[planes[0].getRowStride()];
        final var bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
        final var output = ByteBuffer.allocateDirect(bufferSize);

        var channelOffset = 0;
        var outputStride = 0;

        for (int planeIndex = 0; planeIndex < 3; planeIndex++) {
            switch (planeIndex) {
                case 0 -> {
                    channelOffset = 0;
                    outputStride = 1;
                }
                case 1 -> {
                    channelOffset = width * height + 1;
                    outputStride = 2;
                }
                case 2 -> {
                    channelOffset = width * height;
                    outputStride = 2;
                }
            }

            final var buffer = planes[planeIndex].getBuffer();
            final var rowStride = planes[planeIndex].getRowStride();
            final var pixelStride = planes[planeIndex].getPixelStride();

            final var shift = (planeIndex == 0) ? 0 : 1;
            final var widthShifted = width >> shift;
            final var heightShifted = height >> shift;

            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));

            for (int row = 0; row < heightShifted; row++) {
                final int length;

                if (pixelStride == 1 && outputStride == 1) {
                    length = widthShifted;
                    buffer.get(output.array(), channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (widthShifted - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);

                    for (int col = 0; col < widthShifted; col++) {
                        output.array()[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }

                if (row < heightShifted - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }

        return output;
    }

    public static Bitmap cloneToBitmap(Context context, Image image, ImageProxy imageProxy) {
        // Get the YUV data
        final var yuvBytes = imageToByteBuffer(image);

        // Convert YUV to RGB
        final var rs = RenderScript.create(context);

        final var bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        final var allocationRgb = Allocation.createFromBitmap(rs, bitmap);

        final var allocationYuv = Allocation.createSized(rs, Element.U8(rs), yuvBytes.array().length);
        allocationYuv.copyFrom(yuvBytes.array());

        ScriptIntrinsicYuvToRGB scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        scriptYuvToRgb.setInput(allocationYuv);
        scriptYuvToRgb.forEach(allocationRgb);

        allocationRgb.copyTo(bitmap);

        // Release
        //bitmap.recycle();

        allocationYuv.destroy();
        allocationRgb.destroy();
        rs.destroy();

        return rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());
    }

    public static Bitmap rotateBitmap(Bitmap image, float degrees) {
        var matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    private class Analyzer {

        @NonNull
        private final ImageProxy imageProxy;
        @NonNull
        private final Image image;

        private Analyzer(@NonNull ImageProxy imageProxy, @NonNull Image image) {
            this.imageProxy = imageProxy;
            this.image = image;
        }

        private InputImage fromMediaImage() {
            return InputImage.fromMediaImage(
                    image,
                    imageProxy.getImageInfo().getRotationDegrees()
            );
        }

        private void analyze() {
            BarcodeScanning
                    .getClient(QrCodeAnalyzer.getOptions())
                    .process(fromMediaImage())
                    .addOnCompleteListener(this::onComplete)
                    .addOnFailureListener(this::onFailure);
        }

        private void onComplete(@NonNull Task<List<Barcode>> barcodes) {
            onQrCodesDetected.accept(barcodes.getResult(), cloneToBitmap(context, image, imageProxy));
            image.close();
            imageProxy.close();
        }

        private void onFailure(@NonNull Exception failure) {
            //failure.printStackTrace();
            image.close();
            imageProxy.close();
        }
    }
}
