package com.dry.a2map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dry.a2map.adapters.ListAdapter;
import com.dry.a2map.beans.ItemBean;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener,
        PlaceSelectionListener {

    SupportMapFragment mapFragment;
    AutocompleteSupportFragment autocompleteSupportFragment;
    CardView search;
    AppCompatButton btn_map, btn_info, btn_location;
    RecyclerView view_info;
    private GoogleMap mMap;
    private FusedLocationProviderClient flp;
    private boolean isLocationPermissionGranted;
    private List<String> ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(this);

        search = (CardView) findViewById(R.id.search);
        btn_map = (AppCompatButton) findViewById(R.id.btn_map);
        btn_info = (AppCompatButton) findViewById(R.id.btn_info);
        btn_location = (AppCompatButton) findViewById(R.id.btn_location);
        view_info = (RecyclerView) findViewById(R.id.view_info);

        flp = new FusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        currentLocation();
        mMap.setOnMapClickListener(this);
    }

    private void currentLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            getLocationUpdates();
        } else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        isLocationPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (isLocationPermissionGranted) {
            getLocationUpdates();
        }
    }

    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //mMap.setMyLocationEnabled(true);
        Task<Location> task = flp.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),18));
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("here"));
                    Toast.makeText(MapsActivity.this, " "+location.getLatitude()+","+location.getLatitude(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng.latitude + "," + latLng.longitude + "&radius=50&rankby=prominence&key=" + getString(R.string.google_maps_key);
        List<ItemBean> listdata = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    ItemBean data = new ItemBean();
                    JSONObject result = results.getJSONObject(1);
                    if(result.has("vicinity")){
                        data.tel = result.getString("vicinity");
                    }else data.tel="null";
                    if(result.has("name")){
                        data.name = result.getString("name");
                    }else data.name="null";
                    if(result.has("types")){
                        JSONArray categorieslist = result.getJSONArray("types");
                        String types = "";
                        int index = categorieslist.length();
                        for(int i=0; i<index-1;i++){
                            types = types + categorieslist.getString(i) + ",";
                        }
                        types += categorieslist.getString(index-1);
                        data.category = types;
                    }else data.category="null";

                    listdata.add(data);

                    ListAdapter listAdapter = new ListAdapter(listdata);
                    view_info.setAdapter(listAdapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MapsActivity.this);
                    view_info.setLayoutManager(linearLayoutManager);

                    mMap.addMarker(new MarkerOptions().position(latLng).title(data.name));
                    Toast.makeText(MapsActivity.this, " " + latLng.latitude + "," + latLng.longitude, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }


    public void location_clicked(View view) {
        mMap.clear();
        currentLocation();
        btn_map.setBackgroundColor(0xff3969e3);
        btn_map.setTextColor(0xffffffff);
        btn_info.setBackgroundColor(0xffffffff);
        btn_info.setTextColor(0xff000000);
        view_info.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        LatLng latLng = place.getLatLng();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        mMap.clear();
//        ID = getID(latLng);
//        showmap(ID);
        showmap(latLng);
    }

    private void showmap(LatLng latLng) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng.latitude + "," + latLng.longitude + "&radius=3000&rankby=prominence&key=" + getString(R.string.google_maps_key);
        List<ItemBean> listdata = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 1; i < results.length(); i++) {
                        ItemBean data = new ItemBean();
                        JSONObject result = results.getJSONObject(i);
                        if(result.has("vicinity")){
                            data.tel = result.getString("vicinity");
                        }else data.tel="null";
                        if(result.has("name")){
                            data.name = result.getString("name");
                        }else data.name="null";
                        if(result.has("types")){
                            JSONArray categorieslist = result.getJSONArray("types");
                            String types = "";
                            int index = categorieslist.length();
                            for(int j=0; j<index-1;j++){
                                types = types + categorieslist.getString(j) + ",";
                            }
                            types += categorieslist.getString(index-1);
                            data.category = types;
                        }else data.category="null";

                        listdata.add(data);

                        double lat = result.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double lng = result.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        LatLng eachlatLng = new LatLng(lat,lng);
                        Log.e("lat: ",String.valueOf(lat+","+lng));

                        mMap.addMarker(new MarkerOptions().position(eachlatLng).title(data.name));
                    }

                    ListAdapter listAdapter = new ListAdapter(listdata);
                    view_info.setAdapter(listAdapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MapsActivity.this);
                    view_info.setLayoutManager(linearLayoutManager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

//    private void showmap(List<String> id) {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        List<ItemBean> listdata = new ArrayList<>();
//
//        for (int i = 0; i < id.size();i++){
//            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+ id.get(i) +"&fields=name,formatted_phone_number,type&key="+getString(R.string.google_maps_key);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        JSONObject result = response.getJSONObject("result");
//                        ItemBean data = new ItemBean();
//                        if(result.has("international_phone_number")){
//                            data.tel = result.getString("international_phone_number");
//                        }else data.tel="null";
//                        if(result.has("name")){
//                            data.name = result.getString("name");
//                        }else data.name="null";
//                        if(result.has("types")){
//                            JSONArray categorieslist = result.getJSONArray("types");
//                            String types = "";
//                            int index = categorieslist.length();
//                            for(int i=0; i<index-1;i++){
//                                types = types + categorieslist.getString(i) + ",";
//                            }
//                            types += categorieslist.getString(index-1);
//                            data.category = types;
//                        }else data.category="null";
//
//                        listdata.add(data);
//
//                        ListAdapter listAdapter = new ListAdapter(listdata);
//                        view_info.setAdapter(listAdapter);
//
//                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MapsActivity.this);
//                        view_info.setLayoutManager(linearLayoutManager);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//            queue.add(jsonObjectRequest);
//        }
//
//
//
//    }
//
//    private List<String> getID(LatLng latLng) {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng.latitude + "," + latLng.longitude + "&radius=5000&rankby=prominence&key=" + getString(R.string.google_maps_key);
//        List<String> iditems = new ArrayList<>();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray results = response.getJSONArray("results");
//                    for (int i=0; i<results.length();i++){
//                        JSONObject result = results.getJSONObject(i);
//                        if (result.has("place_id")){
//                            iditems.add(result.getString("place_id"));
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        return iditems;
//    }


    @Override
    public void onError(@NonNull Status status) {

    }

    public void map_clicked(View view) {
        btn_map.setBackgroundColor(0xff3969e3);
        btn_map.setTextColor(0xffffffff);
        btn_info.setBackgroundColor(0xffffffff);
        btn_info.setTextColor(0xff000000);
        view_info.setVisibility(View.INVISIBLE);
    }

    public void info_clicked(View view) {
        btn_info.setBackgroundColor(0xff3969e3);
        btn_info.setTextColor(0xffffffff);
        btn_map.setBackgroundColor(0xffffffff);
        btn_map.setTextColor(0xff000000);
        view_info.setVisibility(View.VISIBLE);
    }
}
