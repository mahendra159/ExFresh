package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Mahendra on 6/14/2015.
 */
public class cert_map extends FragmentActivity implements OnMapReadyCallback {

    AlertDialogManager alert = new AlertDialogManager();

    GoogleMap mMap;

    String lat;
    String lng;
    String f_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_map);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        lat = extras.getString("lat");
        lng = extras.getString("lng");
        f_name = extras.getString("f_name");

        if (lat.equals("") || lng.equals("")){
            lat = "0";
            lng="0";

            alert.showAlertDialog(cert_map.this, "Location Not Available","",false);

            return;
        }

        if (MyDebug.LOG) {
            Log.d("LAT", lat);
            Log.d("LNG", lng);
        }

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getParent(), requestCode);
            dialog.show();

        } else { // Google Play Services are available


            // Getting reference to the SupportMapFragment of activity_main.xml
            //SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            //mMap = fm.getMapAsync(this);

            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

            // Enabling MyLocation Layer of Google Map
            //mMap.setMyLocationEnabled(true);

            // LatLng object to store user input coordinates
            //LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

            // Drawing the marker at the coordinates
            //drawMarker(point);
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
//        doStuff();
        mMap.setMyLocationEnabled(true);

        // LatLng object to store user input coordinates
        LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        // Drawing the marker at the coordinates
        drawMarker(point);
    }

    void doStuff(){
        mMap.setMyLocationEnabled(true);

        // LatLng object to store user input coordinates
        LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        // Drawing the marker at the coordinates
        drawMarker(point);
    }

    private void drawMarker(LatLng point){
        // Clears all the existing coordinates
        mMap.clear();

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Setting title for the InfoWindow
        //markerOptions.title("Position");
        markerOptions.title("ExFresh Associated Farm");

        // Setting InfoWindow contents
        //markerOptions.snippet("Latitude:" + point.latitude + ",Longitude" + point.longitude);
        markerOptions.snippet(f_name);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);

        // Moving CameraPosition to the user input coordinates
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));

    }

}
