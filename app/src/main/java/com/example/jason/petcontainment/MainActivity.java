package com.example.jason.petcontainment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    public final static String EXTRA_MESSAGE = "com.example.jason.petcontainment.MESSAGE";
    static final String filename = "saved_region.txt";
    private GoogleMap mMap;
    MyReceiver myReceiver;
    ArrayList<GpsPoint> savedCoords;
    ArrayList<GpsPoint> sortedCoords;
    ArrayList<String> stringCoords;
    File savefile;
    //my home LatLng homeLocation = new LatLng(40.385111, -74.517289);
    LatLng homeLocation = new LatLng(40.5260308, -74.434976);
    Polygon polygon;
    Marker dogMarker;

    @Override
    protected void onPause(){
        unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.example.jason.petcontainment.SocketService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedCoords = new ArrayList<GpsPoint>();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.example.jason.petcontainment.SocketService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        Intent intent = new Intent(MainActivity.this, SocketService.class);

        startService(intent);

        stringCoords = Helper.readFile(filename, MainActivity.this);

        /*parse strings of lat/long values into savedCoords,
          sets center value in GpsPoint class, and
          sorts points based on angle to horizontal into revisedCoords.
        */
        if (stringCoords!=null && stringCoords.size() > 3) {
            for (int i = 0; i < stringCoords.size(); ++i) {
                String[] strsplit = stringCoords.get(i).split("[;]");
                double lat = Double.parseDouble(strsplit[0]);
                double lng = Double.parseDouble(strsplit[1]);
                savedCoords.add(new GpsPoint(new LatLng(lat, lng)));
            }
            GpsPoint.center = GpsPoint.findCenter(savedCoords);
            sortedCoords = GpsPoint.sortPoints(savedCoords);
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (savedCoords!=null && sortedCoords.size() > 3) {
            homeLocation = GpsPoint.findCenter(savedCoords);
            //add polygon onto map
            PolygonOptions polyopt = new PolygonOptions()
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.GREEN);
            for (int i = 0; i < sortedCoords.size(); ++i) {
                polyopt.add((LatLng) sortedCoords.get(i).location);
            }
            polygon = mMap.addPolygon(polyopt);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    public void toMap(View view) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (dogMarker != null)
                dogMarker.remove();
            Bundle extras = arg1.getExtras();
            String coordinates = extras.getString("coordinates");
            //int datapassed = arg1.getIntExtra("DATAPASSED", 0);
            System.out.println("from MainActivity: "+coordinates);
            showToast(coordinates);
            String[] split = coordinates.split("[;]");
            LatLng latlng = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            dogMarker = mMap.addMarker(new MarkerOptions().position(latlng)
            .icon(BitmapDescriptorFactory.fromAsset("dog.png")));
        }
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}


