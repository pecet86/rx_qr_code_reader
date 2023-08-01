package pl.pecet.rx_code_reader.ean.internal.ui;

import static pl.pecet.rx_code_reader.ean.R.id.check;
import static pl.pecet.rx_code_reader.ean.R.id.clear;
import static pl.pecet.rx_code_reader.ean.R.id.display_value;
import static pl.pecet.rx_code_reader.ean.R.id.image;
import static pl.pecet.rx_code_reader.ean.R.id.replay;
import static pl.pecet.rx_code_reader.ean.R.id.type;
import static pl.pecet.rx_code_reader.ean.R.id.value;
import static pl.pecet.rx_code_reader.ean.R.layout.rx_code_reader_fragment_view;
import static pl.pecet.rx_code_reader.ean.R.string.rx_code_reader_title_view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import lombok.Getter;
import lombok.Setter;
import pl.pecet.rx_code_reader.ean.api.EanConfig;
import pl.pecet.rx_code_reader.ean.internal.data.EanCode;

public class ViewFragment extends BaseFragment {

    public static final String TAG = ViewFragment.class.getSimpleName();

    @Setter
    @Getter
    @NonNull
    private EanCode qrCode;
    private Listener listener;
    private EanConfig config;

    private MaterialTextView typeView;
    private MaterialTextView valueView;
    private MaterialTextView displayValueView;
    private AppCompatImageView imageView;
    private FloatingActionButton replayView;
    private FloatingActionButton checkView;
    private FloatingActionButton clearView;

    public ViewFragment() {
        super(rx_code_reader_fragment_view);
    }

    void init(@NonNull EanConfig config, @NonNull Listener listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(rx_code_reader_title_view));

        initView();
        initValues();
        initEvents();
    }

    private void initView() {
        typeView = requireView().findViewById(type);
        valueView = requireView().findViewById(value);
        displayValueView = requireView().findViewById(display_value);
        imageView = requireView().findViewById(image);

        replayView = requireView().findViewById(replay);
        checkView = requireView().findViewById(check);
        clearView = requireView().findViewById(clear);
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

        void check(@NonNull EanCode qrCode);

        void clear();
    }
}
