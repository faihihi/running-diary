package au.edu.sydney.comp5216.running_diary.ui.gpstracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import au.edu.sydney.comp5216.running_diary.LogItem;
import au.edu.sydney.comp5216.running_diary.R;
import au.edu.sydney.comp5216.running_diary.RunningLog;
import au.edu.sydney.comp5216.running_diary.RunningLogDB;
import au.edu.sydney.comp5216.running_diary.RunningLogDao;
import au.edu.sydney.comp5216.running_diary.directionhelpers.FetchURL;
import au.edu.sydney.comp5216.running_diary.directionhelpers.TaskLoadedCallback;
import au.edu.sydney.comp5216.running_diary.ui.runninglog.RunningLogFragment;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class GpsTrackerFragment extends Fragment implements OnMapReadyCallback, LocationListener,View.OnClickListener, TaskLoadedCallback {
    private GpsTrackerViewModel gpsTrackerViewModel;
    public static GoogleMap mMap;
    LocationManager locationManager;
    MarkerOptions mo;
    Marker marker;
    boolean previousLocation, reset_check, pause_check;
    Location loc1, loc2;
    Double initial_lat, initial_lng;
    float distanceInMeters, paused_distance;
    TextView distance_view;

    public static Polyline currentPolyline;
    MarkerOptions place1, place2;

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    Button start, stop, reset, getDirection, save;

    private ArrayList<LogItem> RunningLogArray;

    RunningLogDB db;
    RunningLogDao runningLogDao;

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static Context context = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gpsTrackerViewModel =
                ViewModelProviders.of(this).get(GpsTrackerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gpstracker, null, false);
        context = getActivity();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.gpsmap);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(0,0)).title("My Current Location");

        if(Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()){
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else{
            requestLocation();
        }

        if(!isLocationEnabled()){
            showAlert(1);
        }

        getDirection = (Button) root.findViewById(R.id.direction_btn);
        start = (Button) root.findViewById(R.id.start_btn);
        stop = (Button) root.findViewById(R.id.stop_btn);
        reset = (Button) root.findViewById(R.id.reset_btn);
        save = (Button) root.findViewById(R.id.save_btn);

        getDirection.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);

        distance_view = (TextView) root.findViewById(R.id.distance_view);

        chronometer = (Chronometer) root.findViewById(R.id.chronometer);

        loc1 = new Location("");
        loc2 = new Location("");
        reset_check = true;

        db = RunningLogDB.getDatabase(getActivity().getApplicationContext());
        runningLogDao = db.RunningLogDao();
        readItemsFromDatabase();

        return root;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode){
        //Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        //Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Mode
        String mode = "mode=" + directionMode;
        //Build parameter to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        //Output format
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                if(!running){
                    Log.d("Start btn","CLICKEDDDD");
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    running = true;

                    if(reset_check){
                        distanceInMeters = 0;
                        distance_view.setText("0");
                        distance_view.setVisibility(View.VISIBLE);
                        reset_check = false;
                    }
                    paused_distance = 0;
                    pause_check = false;
                }
                break;

            case R.id.stop_btn:
                if(running){
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    running = false;

                    reset_check = false;
                    paused_distance = distanceInMeters;
                    pause_check = true;
                }
                break;
            case R.id.reset_btn:
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;

                distanceInMeters = 0;
                distance_view.setText("0");
                paused_distance = 0;
                pause_check = true;
                reset_check = true;
                break;

            case R.id.direction_btn:
                Log.d("Check if clicked","CLICKEDDDDD");

                place1 = new MarkerOptions().position(new LatLng(-33.8918,151.189)).title("Location 1");
                place2 = new MarkerOptions().position(new LatLng(-33.8834,151.194)).title("Location 2");

                String url2 = getUrl(place1.getPosition(), place2.getPosition(), "driving");
                Log.d("GET URL",url2);
                new FetchURL(getContext()).execute(url2, "driving");
                break;

            case R.id.save_btn:
                String time = (String) chronometer.getText();

                LogItem log = new LogItem(Double.valueOf(distanceInMeters), time);
                RunningLogArray.add(log);

                saveItemsToDatabase();
                break;

            default:
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mo);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        float zoomLevel = 16.0f; //This goes up to 21

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            if(!previousLocation){
                loc1.setLatitude(location.getLatitude());
                loc1.setLongitude(location.getLongitude());

                distanceInMeters = 0;
                previousLocation = true;
            } else if(pause_check){
                distance_view.setText(Float.toString(paused_distance));
            } else{
                loc1.setLatitude(loc2.getLatitude());
                loc1.setLongitude(loc2.getLongitude());

                loc2.setLatitude(location.getLatitude());
                loc2.setLongitude(location.getLongitude());

                distanceInMeters = distanceInMeters + (loc1.distanceTo(loc2)/1000);
                distance_view.setText(Float.toString(distanceInMeters));
            }
        }

        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void requestLocation(){
        //Get location data according to this criteria of accuracy and power
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);


        String provider = locationManager.getBestProvider(criteria,true);
        locationManager.requestLocationUpdates(provider, 5000, 10, this);
    }

    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted(){
        if(checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.v("myLOGGGG", "Permission is GRANTED");
            return true;
        } else{
            Log.v("myLOGGGG","Permission is NOT GRANTED");
            return false;
        }
    }

    private void showAlert(final int status){
        String message, title, btnText;
        if(status == PERMISSION_ALL){
            message = "Your Locations Settings is set to 'Off'.\nPlease enable location to use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else{
            message = "Please alow this app to access location";
            title = "Permission access";
            btnText = "Grant";
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(status == PERMISSION_ALL){
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        } else{
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        getActivity().finish();
                    }
                });
        dialog.show();
    }


    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline != null){
            currentPolyline.remove();
        } else{
            currentPolyline = mMap.addPolyline((PolylineOptions)values[0]);
        }
    }

    private void readItemsFromDatabase(){
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    List<RunningLog> itemsFromDB = runningLogDao.listAll();
                    RunningLogArray = new ArrayList<LogItem>();
                    if (itemsFromDB != null & itemsFromDB.size() > 0) {
                        for (RunningLog item : itemsFromDB) {
                            RunningLogArray.add(item.getRunningLogItem());

                            Log.i("SQLite read item", "Distance: " + item.getDistance() + "Time: " + item.getTime());
                        }
                    }
                    return null;
                }
            }.execute().get();
        }
        catch(Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    public void saveItemsToDatabase(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                runningLogDao.deleteAll();

                for (LogItem l : RunningLogArray) {
                    Log.i("get pace test: ", l.getPace().toString());
                    RunningLog item = new RunningLog(l.getDistance(),l.getTime(),l.getPace(),l.getDate(),l.getSpeed());
                    runningLogDao.insert(item);
                }
                return null;
            }
        }.execute();
    }
}