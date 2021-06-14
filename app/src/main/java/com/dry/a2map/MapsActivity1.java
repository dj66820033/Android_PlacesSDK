// DELETE


//package com.dry.a2map;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//
//import java.util.Arrays;
//
//public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback,
//        GoogleMap.OnMyLocationClickListener,
//        ActivityCompat.OnRequestPermissionsResultCallback,
//        GoogleMap.OnMapClickListener {
//
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1 ;
//    private GoogleMap mMap;
//    SupportMapFragment mapFragment;
//    AutocompleteSupportFragment autocompleteSupportFragment;
//    CardView search;
//    AppCompatButton btn_map, btn_info, btn_location;
//    RecyclerView view_info;
//    LocationRequest locationRequest;
//    FusedLocationProviderClient flp;
//    Location location;
//    Boolean locationPermissionGranted;
//    PlacesClient placesClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        autocompleteSupportFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//
//        search = (CardView) findViewById(R.id.search);
//        btn_map = (AppCompatButton) findViewById(R.id.btn_map);
//        btn_info = (AppCompatButton) findViewById(R.id.btn_info);
//        btn_location = (AppCompatButton) findViewById(R.id.btn_location);
//        view_info = (RecyclerView) findViewById(R.id.view_info);
//
//        locationPermissionGranted = true;
//        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
//        placesClient = Places.createClient(this);
//        flp = LocationServices.getFusedLocationProviderClient(this);
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setOnMapClickListener(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setOnMyLocationClickListener(this);
//        enableMyLocation();
//
//        updateLocationUI();
//        getDeviceLocation();
//
//    }
//
//    private void getDeviceLocation() {
//        try {
//            if (locationPermissionGranted) {
//                Task<Location> locationResult = flp.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            Location lastKnownLocation = task.getResult();
//                            if (lastKnownLocation != null) {
//                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                        new LatLng(lastKnownLocation.getLatitude(),
//                                                lastKnownLocation.getLongitude()), 14));
//                            }
//                        } else {
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage(), e);
//        }
//    }
//
//    private void updateLocationUI() {
//        try {
//            if (locationPermissionGranted) {
//                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                //lastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onMapClick(@NonNull LatLng latLng) {
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("clicked"));
//        Toast.makeText(MapsActivity1.this, " "+latLng.latitude+","+latLng.longitude, Toast.LENGTH_LONG).show();
//    }
//
//    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (mMap != null) {
//                mMap.setMyLocationEnabled(true);
//            }
//        } else {
//
//        }
//    }
//
//
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
//                Toast.makeText(MapsActivity1.this, " "+location.getLatitude()+location.getLatitude(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        locationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationPermissionGranted = true;
//                }
//            }
//        }
//        updateLocationUI();
//    }
//
//}