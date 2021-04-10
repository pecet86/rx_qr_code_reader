package pl.pecet.rx_qr_code_reader.example;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import pl.pecet.qr_code_reader.example.R;
import pl.pecet.rx_qr_code_reader.api.QrCodeConfig;
import pl.pecet.rx_qr_code_reader.api.RxQrCode;
import pl.pecet.rx_qr_code_reader.internal.data.QrCodeType;

import static android.widget.Toast.LENGTH_SHORT;
import static autodispose2.AutoDispose.autoDisposable;

public class MainActivity extends AppCompatActivity {

    private RxQrCode rxQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxQrCode = new RxQrCode(R.id.qr_code_fragment, this);

        ExtendedFloatingActionButton readView = findViewById(R.id.button_read);
        readView.setOnClickListener(v -> {
            rxQrCode
                    .request(this)
                    .doOnSuccess(qrCode -> {
                        Toast.makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                    })
                    .doOnComplete(() -> {
                        Toast.makeText(this, "doOnComplete", LENGTH_SHORT).show();
                    })
                    .doOnError((th) -> {
                        Toast.makeText(this, "doOnError", LENGTH_SHORT).show();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });

        readView = findViewById(R.id.button_read_block_orientation);
        readView.setOnClickListener(v -> {
            QrCodeConfig config = new QrCodeConfig(this)
                    .withLockOrientation(true)
                    .withFlashMode(QrCodeConfig.FLASH_MODE_OFF);

            rxQrCode
                    .request(config)
                    .doOnSuccess(qrCode -> {
                        Toast.makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .doOnComplete(() -> {
                        Toast.makeText(this, "doOnComplete", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .doOnError((th) -> {
                        Toast.makeText(this, "doOnError", LENGTH_SHORT).show();
                        config.unlockOrientation();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });


        readView = findViewById(R.id.button_read_url);
        readView.setOnClickListener(v -> {
            rxQrCode
                    .request(new QrCodeConfig(this)
                            .withType(QrCodeType.URL))
                    .doOnSuccess(qrCode -> {
                        Toast.makeText(this, "doOnSuccess", LENGTH_SHORT).show();
                    })
                    .doOnComplete(() -> {
                        Toast.makeText(this, "doOnComplete", LENGTH_SHORT).show();
                    })
                    .doOnError((th) -> {
                        Toast.makeText(this, "doOnError", LENGTH_SHORT).show();
                    })
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe();
        });
    }
}
