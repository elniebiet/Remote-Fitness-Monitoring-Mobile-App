package com.example.fitnessmonitor.fitnessmonitor.views;
/**
 * Created by Aniebiet Akpan.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessmonitor.fitnessmonitor.MainActivity;
import com.example.fitnessmonitor.fitnessmonitor.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//public class ExerciseRunning extends AppCompatActivity {
public class NearbyHealthCentres extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private String myLocationLong = "";
    private String myLocationLat = "";
    private boolean locationUpdateDone = false;
    private String apiKey = "AIzaSyADjGr1P8uXe4bCeSq51nbLaIHsYLyiQ1I";
    private String linkToMap = "https://www.google.com/maps/search/?api=1&query=Google&query_place_id=";
    private ArrayList<String> centreNames = new ArrayList<>();
    private ArrayList<String> centreIds = new ArrayList<>();
    private ArrayList<Double> centreLatitudes = new ArrayList<>();
    private ArrayList<Double> centreLongitudes = new ArrayList<>();
    private boolean doneLoadingToBuffers = false;
    private String jsonString = "";
    private String searchRadius = "5000";
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_health_centres);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println("width is "+width);
        //set flBack height
        int flBackHeight = (int)(0.05 * height);
        int flBackMarginTop = 10;
        FrameLayout flBack = (FrameLayout) findViewById(R.id.flBack);
        ViewGroup.LayoutParams flBackLayoutParams = flBack.getLayoutParams();
        flBackLayoutParams.height = flBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flBack, 0,flBackMarginTop,0,0);
        flBack.setLayoutParams(flBackLayoutParams);
        //set flTitle height
        int flTitleHeight = (int)(0.08 * height);
        int flTitleMarginTop = 0;
        FrameLayout flTitle = (FrameLayout) findViewById(R.id.flTitle);
        ViewGroup.LayoutParams flTitleLayoutParams = flTitle.getLayoutParams();
        flTitleLayoutParams.height = flTitleHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flTitle, 0,flTitleMarginTop,0,0);
        flTitle.setLayoutParams(flTitleLayoutParams);

        //set flMap height
        int flMapHeight = (int)(0.9 * height);
        int flMapMarginTop = 0;
        FrameLayout flMap = (FrameLayout) findViewById(R.id.flMap);
        ViewGroup.LayoutParams flMapLayoutParams = flMap.getLayoutParams();
        flMapLayoutParams.height = flMapHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flMap, 0,flMapMarginTop,0,0);
        flMap.setLayoutParams(flMapLayoutParams);

        //test load data from file
//        String jsonString = loadJSONFromAsset();
//        doneLoadingToBuffers = loadDataToBuffers(jsonString);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }


    public void goBackHome(View view){
        locationManager.removeUpdates(locationListener);
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

            progressDialog = ProgressDialog.show(this, "Find Places"
                , "Searching health centres...", true);

            mMap = googleMap;
            //create location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //create location listener
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    progressDialog.dismiss();

                    myLocationLong = Double.toString(location.getLongitude());
                    myLocationLat = Double.toString(location.getLatitude());
                    //Get nearby facilities
                    NearbyHealthFacilities checkr = new NearbyHealthFacilities();
                    String apiQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + myLocationLat + "," + myLocationLong + "&radius=" + searchRadius + "&type=health&keyword=hospital%20health%20centre%20clinic%20nhs&key=" + apiKey;
                    checkr.execute(apiQuery);

                    if(doneLoadingToBuffers == true) {
                        if (locationUpdateDone == false) {
                            mMap.clear();
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Marker marker;
                            marker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            marker.showInfoWindow();
                            for (int i = 0; i < centreIds.size(); i++) {
                                userLocation = new LatLng(centreLatitudes.get(i), centreLongitudes.get(i));
                                marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(centreNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                marker.showInfoWindow();
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

//                            locationUpdateDone = true;
                        }
                    }
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
            };


            //ignore if version < 23, just request location updates
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                myLocationLong = Double.toString(lastKnownLocation.getLongitude());
                myLocationLat = Double.toString(lastKnownLocation.getLatitude());
                //Get nearby facilities
                NearbyHealthFacilities checkr = new NearbyHealthFacilities();
                String apiQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + myLocationLat + "," + myLocationLong + "&radius=" + searchRadius + "&type=health&keyword=hospital%20health%20centre%20clinic%20nhs&key=" + apiKey;
                checkr.execute(apiQuery);

                mMap.clear();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                Marker marker;
                marker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.showInfoWindow();
                for(int i=0; i<centreIds.size(); i++) {
                    userLocation = new LatLng(centreLatitudes.get(i), centreLongitudes.get(i));
                    marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(centreNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    marker.showInfoWindow();
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

            } else {
                //request permission if not granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //get current location
                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        myLocationLong = Double.toString(lastKnownLocation.getLongitude());
                        myLocationLat = Double.toString(lastKnownLocation.getLatitude());
                        //Get nearby facilities
                        NearbyHealthFacilities checkr = new NearbyHealthFacilities();
                        String apiQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + myLocationLat + "," + myLocationLong + "&radius=" + searchRadius + "&type=health&keyword=hospital%20health%20centre%20clinic%20nhs&key=" + apiKey;
                        checkr.execute(apiQuery);

                        mMap.clear();
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        myLocationLong = Double.toString(lastKnownLocation.getLongitude());
                        myLocationLat = Double.toString(lastKnownLocation.getLatitude());
                        System.out.println("MYLOCATION: LONG " + myLocationLong + " LAT " + myLocationLat);

                        LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        Marker mk = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        mk.showInfoWindow();
                        for(int i=0; i<centreIds.size(); i++) {
                            userLocation = new LatLng(centreLatitudes.get(i), centreLongitudes.get(i));
                            mk = mMap.addMarker(new MarkerOptions().position(userLocation).title(centreNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            mk.showInfoWindow();
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
                    } catch (Exception ex) {
                        Log.i("ERROR GETTING LOCATN: ", ex.getMessage());
                    }
                }
            }
//        }
    }

    //MAPS API Class
    public class NearbyHealthFacilities extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.i("DATA", s);
                doneLoadingToBuffers = loadDataToBuffers(s);

            } catch (Exception e) {
                Log.i("FAILED TO GET", "FAILED TO GET JSON DATA");
                e.printStackTrace();
            }

        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            System.out.println("ERROR READING JSON FILE " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    //Test function to import json data from file, real data from API not using this function
    public boolean loadDataToBuffers(String jsString){
        //test json
        try {
            JSONObject obj = new JSONObject(jsString);
            JSONArray m_jArry = obj.getJSONArray("results");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            if(m_jArry.length() == 0){
                Toast.makeText(getApplicationContext(), "No nearby health centers found.", Toast.LENGTH_LONG).show();
            }

            for (int i = 0; i < m_jArry.length(); i++) {
                try {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    String businessStatus = jo_inside.getString("business_status");
                    JSONObject geometry = jo_inside.getJSONObject("geometry");
                    JSONObject locatn = geometry.getJSONObject("location");
                    Double ltitude = locatn.getDouble("lat");
                    Double lngitude = locatn.getDouble("lng");
                    String name = jo_inside.getString("name");
                    String placeId = jo_inside.getString("place_id");
                    JSONObject openingHours = jo_inside.getJSONObject("opening_hours");
                    Boolean openNow = openingHours.getBoolean("open_now");
                    System.out.println("LAT AND LONG : " + ltitude + " " + lngitude + " NAME :" + name + " BUSINESS STAT: " + businessStatus + " LOCATION: " + locatn + " OPEN NOW: " + openNow + " ID: " + placeId);

                    if(openNow == true){
                        System.out.println("BUSINESS IS OPERATIONAL");
                        centreNames.add(name);
                        centreIds.add(placeId);
                        centreLatitudes.add(ltitude);
                        centreLongitudes.add(lngitude);
                    }
                } catch(Exception ex){
                    System.out.println("ERROR FETCHING JSON " + ex.getMessage());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
