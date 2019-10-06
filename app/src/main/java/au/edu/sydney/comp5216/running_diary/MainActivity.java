package au.edu.sydney.comp5216.running_diary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

import au.edu.sydney.comp5216.running_diary.directionhelpers.TaskLoadedCallback;
import au.edu.sydney.comp5216.running_diary.ui.gpstracker.GpsTrackerFragment;
import au.edu.sydney.comp5216.running_diary.ui.runninglog.RunningLogFragment;

public class MainActivity extends AppCompatActivity implements TaskLoadedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pace, R.id.navigation_tracker, R.id.navigation_log, R.id.navigation_music)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onTaskDone(Object... values) {
        if(GpsTrackerFragment.currentPolyline != null){
            GpsTrackerFragment.currentPolyline.remove();
        } else{
            GpsTrackerFragment.currentPolyline = GpsTrackerFragment.mMap.addPolyline((PolylineOptions)values[0]);
        }
    }

}
