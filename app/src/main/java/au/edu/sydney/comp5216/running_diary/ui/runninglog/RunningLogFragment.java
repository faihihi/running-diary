package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import au.edu.sydney.comp5216.running_diary.R;

public class RunningLogFragment extends Fragment {

    private RunningLogViewModel runningLogViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        runningLogViewModel =
                ViewModelProviders.of(this).get(RunningLogViewModel.class);
        View root = inflater.inflate(R.layout.fragment_runninglog, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        runningLogViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}