package au.edu.sydney.comp5216.running_diary.ui.pacecalculator;

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

public class PaceCalculatorFragment extends Fragment {

    private PaceCalculatorViewModel paceCalculatorViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        paceCalculatorViewModel =
                ViewModelProviders.of(this).get(PaceCalculatorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pacecalculator, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        paceCalculatorViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}