package pl.pecet.rx_qr_code_reader.internal.ui;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

class BaseFragment extends Fragment {

    protected String TAG;

    {
        TAG = getClass().getSimpleName();
    }

    private CharSequence oldTitle;

    @ContentView
    protected BaseFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onDestroyView() {
        setTitle(oldTitle);
        super.onDestroyView();
    }

    //<editor-fold desc="log">
    @SuppressWarnings("SameParameterValue")
    protected boolean logOnComplete(String path, String value, Throwable th) {
        log(TAG, path, value, th);
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value, Throwable th) {
        Log.e(String.format("%s.%s", TAG, path), value, th);
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value) {
        Log.d(String.format("%s.%s", TAG, path), value);
    }
    //</editor-fold>

    protected void setTitle(CharSequence title) {
        if (title == null) {
            return;
        }
        Activity activity = requireActivity();
        if (activity instanceof AppCompatActivity) {
            androidx.appcompat.app.ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                oldTitle = actionBar.getTitle();
                actionBar.setTitle(title);
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                oldTitle = actionBar.getTitle();
                actionBar.setTitle(title);
            }
        }
    }
}
