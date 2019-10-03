package au.edu.sydney.comp5216.running_diary.ui.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import au.edu.sydney.comp5216.running_diary.R;

public class MusicPlayerFragment extends Fragment {

    private MusicPlayerViewModel musicPlayerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        musicPlayerViewModel =
                ViewModelProviders.of(this).get(MusicPlayerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_music, container, false);
        final TextView textView = root.findViewById(R.id.text_music);
        musicPlayerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
