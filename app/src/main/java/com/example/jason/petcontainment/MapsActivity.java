package com.example.jason.petcontainment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<GpsPoint> inputCoords;
    ArrayList<GpsPoint> revisedCoords;
    //my home LatLng homeLocation = new LatLng(40.385111, -74.517289);
    LatLng homeLocation = new LatLng(40.5260308, -74.434976);
    Polygon polygon;
    //TextView errorTV;
    RelativeLayout layout;
    RelativeLayout.LayoutParams lp;
    ArrayList<String> stringCoords;
    static final String errormessage = "Region is invalid: too few sides.";
    static final String filename = "saved_region.txt";
    Context context;
    File savefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        context = getApplicationContext();
        savefile = new File(this.getFilesDir().getPath().toString() + filename);
        inputCoords = new ArrayList<GpsPoint>();
        revisedCoords = new ArrayList<GpsPoint>();
        /*errorTV = new TextView(this);
        errorTV.setTextSize(30);
        errorTV.setText(errormessage);
        errorTV.setId(1);
        layout = (RelativeLayout) findViewById(R.id.map_content);
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, errorTV.getId());*/
        //reading from saved file
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(myHome).title("Marker for Jason's Home"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.setOnMapClickListener(mapClickListener);
    }

    public void saveAndUpdateMarkers(View view){
        if (revisedCoords.size() < 3){
            System.out.println(errormessage);
            showToast("Invalid region to save.");
            return;
        }
        try{
            if (!savefile.exists())
            savefile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //write to file "saved_region"
        try{
            FileOutputStream outputStream = openFileOutput(filename, context.MODE_PRIVATE);
            for (int i = 0; i < revisedCoords.size(); ++i){
                String encodedCoord = revisedCoords.get(i).location.latitude +
                        ";" + revisedCoords.get(i).location.longitude + "\n";
                outputStream.write(encodedCoord.getBytes());
            }
            outputStream.close();
            showToast("Region saved.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void resetMarkers(View view){
        //layout.removeView(errorTV);
        mMap.clear();
        inputCoords.clear();
        revisedCoords.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20.0f));
    }

    public void enterMarkers(View view){
        //Checks if user entered enough points (3 is the minimum for a polygon) If not,
        //displays error message to user and returns without calculating new region.
        if (inputCoords.size()<3){
            System.out.println(errormessage);
            //layout.addView(errorTV, lp);
            showToast("Invalid region.");
            return;
        }
        calculateRegion();
        GpsPoint.center = GpsPoint.findCenter(inputCoords);
        revisedCoords = GpsPoint.sortPoints(inputCoords);
        PolygonOptions polyopt = new PolygonOptions()
                .strokeColor(Color.GREEN)
                .fillColor(Color.GREEN);
        for (int i = 0; i < revisedCoords.size(); ++i){
            polyopt.add((LatLng)revisedCoords.get(i).location);
        }
        polygon = mMap.addPolygon(polyopt);
    }

    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mMap.addMarker(new MarkerOptions().position(latLng));
            GpsPoint markerLoc = new GpsPoint(latLng);
            inputCoords.add(markerLoc);
            System.out.println("input coordinate: " + inputCoords.get(inputCoords.size() - 1).location);
            System.out.println("sizeofarraylist: " + inputCoords.size());
            //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    };

    public void calculateRegion(){

    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
