package au.edu.sydney.comp5216.running_diary.ui.gpstracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import au.edu.sydney.comp5216.running_diary.R;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class GpsTrackerFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GpsTrackerViewModel gpsTrackerViewModel;
    private GoogleMap mMap;
    LocationManager locationManager;
    MarkerOptions mo;
    Marker marker;

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gpsTrackerViewModel =
                ViewModelProviders.of(this).get(GpsTrackerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gpstracker, null, false);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.gpsmap);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.gpsmap);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(0,0)).title("My Current Location");
//
        if(Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()){
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else{
            requestLocation();
        }

        if(!isLocationEnabled()){
            showAlert(1);
        }

        return root;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_gpstracker);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.gpsmap);
//        mapFragment.getMapAsync(this);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        mo = new MarkerOptions().position(new LatLng(0,0)).title("My Current Location");
//
//        if(Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()){
//            requestPermissions(PERMISSIONS, PERMISSION_ALL);
//        } else{
//            requestLocation();
//        }
//
//        if(!isLocationEnabled()){
//            showAlert(1);
//        }
//    }

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
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
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
}