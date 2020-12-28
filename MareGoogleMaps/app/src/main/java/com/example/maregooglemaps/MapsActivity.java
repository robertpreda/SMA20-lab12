package com.example.maregooglemaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private final int REQ_PEMISSION = 5;
    private LatLng A,B;
    private int toggle = 0;
    private IconGenerator iconGenerator;
    private List<Polyline> lines;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.iconGenerator = new IconGenerator(this);
        this.lines = new ArrayList<>();


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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(toggle == 0){
                    A = latLng;
                    toggle = 1;
                    iconGenerator.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
                    mMap.addMarker(new MarkerOptions()
                            .position(A)
                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Marker A"))));
                }else if(toggle == 1){
                    B = latLng;
                    toggle = 0;

                    iconGenerator.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
                    mMap.addMarker(new MarkerOptions()
                            .position(B)
                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Marker B"))));
                }
                if(A != null && B != null){
                    drawPolyLineOnMap(A,B, mMap);
                }
            }
        });

        // Add a marker in Sydney and move the camera

        if(checkPermission())
            mMap.setMyLocationEnabled(true);
        else{
            askPermission();
        }
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PEMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch(requestCode){
            case REQ_PEMISSION:{
                if(grantResult.length > 2 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPermission()){
                        mMap.setMyLocationEnabled(true);
                    }
                    else{
                        Toast.makeText(this,"Need location access",Toast.LENGTH_SHORT).show();
                        askPermission();
                    }
                    break;
                }
            }
        }
    }

    private void drawPolyLineOnMap(LatLng A, LatLng B, GoogleMap gmap){
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.GREEN);
        polyOptions.width(8);
        polyOptions.add(A);
        polyOptions.add(B);
        gmap.addPolyline(polyOptions);
        
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(A);
        builder.include(B);
        builder.build();
    }


}

