package pl.pecet.rx_qr_code_reader.internal.ui;

import static pl.pecet.rx_qr_code_reader.R.layout.rx_qr_code_reader_fragment_view;
import static pl.pecet.rx_qr_code_reader.R.string.rx_qr_code_reader_title_view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import lombok.Getter;
import lombok.Setter;
import pl.pecet.rx_qr_code_reader.R;
import pl.pecet.rx_qr_code_reader.api.QrCodeConfig;
import pl.pecet.rx_qr_code_reader.internal.data.QrCode;

public class ViewFragment extends BaseFragment {

    public static final String TAG = ViewFragment.class.getSimpleName();

    @Setter
    @Getter
    @NonNull
    private QrCode qrCode;
    private Listener listener;
    private QrCodeConfig config;

    private MaterialTextView typeView;
    private MaterialTextView valueView;
    private MaterialTextView displayValueView;
    private AppCompatImageView imageView;
    private FloatingActionButton replayView;
    private FloatingActionButton checkView;
    private FloatingActionButton clearView;

    public ViewFragment() {
        super(rx_qr_code_reader_fragment_view);
    }

    void init(@NonNull QrCodeConfig config, @NonNull Listener listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(rx_qr_code_reader_title_view));

        initView();
        initValues();
        initEvents();
    }

    private void initView() {
        typeView = requireView().findViewById(R.id.type);
        valueView = requireView().findViewById(R.id.value);
        displayValueView = requireView().findViewById(R.id.display_value);
        imageView = requireView().findViewById(R.id.image);

        replayView = requireView().findViewById(R.id.replay);
        checkView = requireView().findViewById(R.id.check);
        clearView = requireView().findViewById(R.id.clear);
    }

    private void initValues() {
        typeView.setText(qrCode.getType().toString());
        valueView.setText(qrCode.getRawValue());
        displayValueView.setText(qrCode.getDisplayValue());
        imageView.setImageBitmap(qrCode.getImage() != null ? qrCode.getImage() : null);
    }

    private void initEvents() {
        replayView.setOnClickListener(v -> listener.replay());
        checkView.setOnClickListener(v -> listener.check(qrCode));
        clearView.setOnClickListener(v -> listener.clear());
    }

    public interface Listener {
        void replay();

        void check(@NonNull QrCode qrCode);

        void clear();
    }
}
