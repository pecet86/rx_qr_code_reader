package pl.pecet.rx_qr_code_reader.example;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static autodispose2.AutoDispose.autoDisposable;
import static pl.pecet.rx_qr_code_reader.api.QrCodeConfig.FLASH_MODE_OFF;
import static pl.pecet.rx_qr_code_reader.example.R.id.button_read_ean;
import static pl.pecet.rx_qr_code_reader.example.R.id.button_read_qr_code;
import static pl.pecet.rx_qr_code_reader.example.R.id.button_read_qr_code_block_orientation;
import static pl.pecet.rx_qr_code_reader.example.R.id.button_read_qr_code_url;
import static pl.pecet.rx_qr_code_reader.example.R.id.ean_code_fragment;
import static pl.pecet.rx_qr_code_reader.example.R.id.qr_code_fragment;
import static pl.pecet.rx_qr_code_reader.example.R.layout.activity_main;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;

import java.text.MessageFormat;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import pl.pecet.rx_ean_reader.api.RxEanCode;
import pl.pecet.rx_qr_code_reader.api.QrCodeConfig;
import pl.pecet.rx_qr_code_reader.api.RxQrCode;
import pl.pecet.rx_qr_code_reader.internal.data.QrCodeType;

@ExperimentalGetImage
public class MainActivity extends AppCompatActivity {

    private RxQrCode rxQrCode;
    private RxEanCode rxEanCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        rxQrCode = new RxQrCode(qr_code_fragment, this);

        var readView = findViewById(button_read_qr_code);
        readView.setOnClickListener(v -> {
            rxQrCode
                    .request(this)
                    .doOnSuccess(qrCode -> {
                        makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                    })
                    .doOnComplete(() -> {
                        makeText(this, "doOnComplete", LENGTH_SHORT).show();
                    })
                    .doOnError((th) -> {
                        makeText(this, "doOnError", LENGTH_SHORT).show();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });

        readView = findViewById(button_read_qr_code_block_orientation);
        readView.setOnClickListener(v -> {
            var config = new QrCodeConfig(this)
                    .withLockOrientation(true)
                    .withFlashMode(FLASH_MODE_OFF);

            rxQrCode
                    .request(config)
                    .doOnSuccess(qrCode -> {
                        makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .doOnComplete(() -> {
                        makeText(this, "doOnComplete", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .doOnError((th) -> {
                        makeText(this, "doOnError", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });

        readView = findViewById(button_read_qr_code_url);
        readView.setOnClickListener(v -> {
            rxQrCode
                    .request(new QrCodeConfig(this)
                            .withType(QrCodeType.URL))
                    .doOnSuccess(qrCode -> {
                        makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                    })
                    .doOnComplete(() -> {
                        makeText(this, "doOnComplete", LENGTH_SHORT).show();
                    })
                    .doOnError((th) -> {
                        makeText(this, "doOnError", LENGTH_SHORT).show();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });

        rxEanCode = new RxEanCode(ean_code_fragment, this);

        readView = findViewById(button_read_ean);
        readView.setOnClickListener(v -> {
            rxEanCode
                    .request(this)
                    .doOnSuccess(eanCode -> {
                        makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                        Log.d("rxEanCode", MessageFormat.format("doOnSuccess: `{0}`, `{1}`, `{2}`", eanCode.getType(), eanCode.getFormat(), eanCode.getDisplayValue()));
                    })
                    .doOnComplete(() -> {
                        makeText(this, "doOnComplete", LENGTH_SHORT).show();
                    })
                    .doOnError((th) -> {
                        makeText(this, "doOnError", LENGTH_SHORT).show();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });
    }
}
