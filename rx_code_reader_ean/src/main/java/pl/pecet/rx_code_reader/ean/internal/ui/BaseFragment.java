package pl.pecet.rx_code_reader.ean.internal.ui;

import android.util.Log;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

class BaseFragment extends Fragment {

    protected String TAG;
    private CharSequence oldTitle;

    {
        TAG = getClass().getSimpleName();
    }

    @ContentView
    protected BaseFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value, Throwable th) {
        Log.e(String.format("%s.%s", TAG, path), value, th);
    }

    @SuppressWarnings("SameParameterValue")
    protected static void log(String TAG, String path, String value) {
        Log.d(String.format("%s.%s", TAG, path), value);
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
    //</editor-fold>

    protected void setTitle(CharSequence title) {
        if (title == null) {
            return;
        }
        var activity = requireActivity();
        if (activity instanceof AppCompatActivity) {
            var actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                oldTitle = actionBar.getTitle();
                actionBar.setTitle(title);
            }
        } else {
            var actionBar = activity.getActionBar();
            if (actionBar != null) {
                oldTitle = actionBar.getTitle();
                actionBar.setTitle(title);
            }
        }
    }
}
