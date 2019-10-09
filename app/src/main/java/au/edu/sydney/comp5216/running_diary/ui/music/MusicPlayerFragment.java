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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

import au.edu.sydney.comp5216.running_diary.R;

/**
 * MusicPlayerFragment start when music player navigation is clicked
 */
public class MusicPlayerFragment extends Fragment {

    // Set variables
    private String[] itemsAll;
    private ListView songList;

    /**
     * When fragment start, song list view is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music, container, false);

        songList = (ListView) root.findViewById(R.id.songList);
        // Get permission to access external storage and get song list from storage
        appExternalStoragePermission();

        return root;
    }

    /**
     * Get permission to read external storage
     */
    public void appExternalStoragePermission(){
        // Request permission and listen if granted or not
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    /**
                     * When permission is granted, display song list
                     * @param response from permission request
                     */
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();
                    }

                    /**
                     * When permission is denied, do nothing
                     * @param response
                     */
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {}

                    /**
                     * When permission rationale required, request again with token
                     * @param permission
                     * @param token
                     */
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Read audio files from storage and add to arrayList
     * @param file
     * @return arrayList
     */
    public ArrayList<File> readOnlyAudioSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();
        for(File individualFile : allFiles){
            // If file is directory and not hidden, recursively go into the directory, check for files, and add to arrayList
            if(individualFile.isDirectory() && !individualFile.isHidden()){
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            } else{ // Add only files with .mp3 and .wav extensions to arrayList
                if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma")){
                    arrayList.add(individualFile);
                }
            }
        }
        return arrayList;
    }

    /**
     * Display song list and setup onitemclick listener to start MusicPlayerActivity when a song is selected
     */
    private void displayAudioSongsName(){
        // Get songs from external storage
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        itemsAll = new String[audioSongs.size()];

        // Add songs name to itemsAll list
        for(int songCounter = 0; songCounter < audioSongs.size(); songCounter++){
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        // Create adapter of song list and setup adapter to display song list view
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.song_list, R.id.song_title, itemsAll);
        songList.setAdapter(arrayAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * When a song on the list is clicked, create intent and start music player activity
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = songList.getItemAtPosition(i).toString();

                // Set up intent with info of selected song
                Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);

                startActivity(intent);
            }
        });
    }
}
