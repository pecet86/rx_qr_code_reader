# RxQrCodeReader
===

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxQrCodeReader-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5329)
[![codecov](https://codecov.io/gh/yshrsmz/historian/branch/master/graph/badge.svg)](https://codecov.io/gh/yshrsmz/historian)
[![](https://jitpack.io/v/pecet86/rx_qr_code_reader.svg)](https://jitpack.io/#pecet86/rx_qr_code_reader)
![License](https://img.shields.io/github/license/pecet86/rx_qr_code_reader.svg)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-orange.svg)](http://makeapullrequest.com)

RxQrCodeReader is a library that allows you to read QRCode.

## Installation

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
  def version = '<version>'
  implementation "com.github.pecet86:rx_qr_code_reader:$version"
}
```

## Usage

```java
public class MainActivity extends AppCompatActivity {

    private RxQrCode rxQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxQrCode = new RxQrCode(R.id.qr_code_fragment, this);
        
        ExtendedFloatingActionButton readView = findViewById(R.id.button_read_url);
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
```

## Libraries definition

- use [Scan Barcodes with ML Kit on Android](https://developers.google.com/ml-kit/vision/barcode-scanning/android)
- use [ReactiveX](https://github.com/ReactiveX/RxJava/tree/3.x)
- use [CameraX](https://developer.android.com/jetpack/androidx/releases/camera)

## License

```
Copyright 2021 Paweł Cal (pecet86)

GNU GENERAL PUBLIC LICENSE  
Version 3, 29 June 2007

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
