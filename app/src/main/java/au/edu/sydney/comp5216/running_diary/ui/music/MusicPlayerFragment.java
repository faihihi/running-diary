package au.edu.sydney.comp5216.running_diary.ui.music;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import au.edu.sydney.comp5216.running_diary.R;

public class MusicPlayerFragment extends Fragment {

    private MusicPlayerViewModel musicPlayerViewModel;

    private String[] itemsAll;
    private ListView songList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        musicPlayerViewModel =
                ViewModelProviders.of(this).get(MusicPlayerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_music, container, false);


        songList = (ListView) root.findViewById(R.id.songList);
        appExternalStoragePermission();

        return root;
    }

    public void appExternalStoragePermission(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        /* ... */
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();
        for(File individualFile : allFiles){
            if(individualFile.isDirectory() && !individualFile.isHidden()){
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            } else{
                if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma")){
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }

    private void displayAudioSongsName(){
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        itemsAll = new String[audioSongs.size()];

        for(int songCounter = 0;songCounter < audioSongs.size(); songCounter++){
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_music, R.id.music_text, itemsAll);
        songList.setAdapter(arrayAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = songList.getItemAtPosition(i).toString();

                Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);

                startActivity(intent);
            }
        });
    }
}
