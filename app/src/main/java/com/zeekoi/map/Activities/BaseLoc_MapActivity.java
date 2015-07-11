package com.zeekoi.map.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.zeekoi.map.Managers.AppLocationService;
import com.zeekoi.map.Managers.SessionManager;
import com.zeekoi.map.R;

public class BaseLoc_MapActivity extends AppCompatActivity {

    private GoogleMap mMap;
    AppLocationService appLocationService;
    private GoogleProgressBar progressBar;
    private Marker marker;
    private SessionManager session;
    private Toast mToast;
    private Circle circle;
    boolean rangeFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_loc__map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        appLocationService = new AppLocationService(BaseLoc_MapActivity.this);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapBaseLoc);
        mMap = mapFragment.getMap();
        // Changing map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Showing / hiding your current location
        mMap.setMyLocationEnabled(true);

        // Enable / Disable zooming controls
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Enable / Disable my location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        mMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        progressBar = (GoogleProgressBar) findViewById(R.id.progressBarBaseLoc);
        progressBar.setVisibility(View.VISIBLE);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        session = new SessionManager(this);

        initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_loc__map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.favourites) {

            Intent favIntent = new Intent(getApplicationContext(), fav_activity.class);
            startActivity(favIntent);
        }
        if (id == R.id.select_range) {

            final Dialog dialog = new Dialog(this, R.style.cust_dialog);
            dialog.setContentView(R.layout.customdialog);
            dialog.setTitle("Select Range(KM)");
            final EditText rangeText = (EditText) dialog.findViewById(R.id.range);
            rangeText.setGravity(Gravity.CENTER);
            rangeText.setText(session.getRange());
            Button rangebutton = (Button) dialog.findViewById(R.id.rangebutton);
            rangebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.setRange(rangeText.getText().toString());
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Range Saved..!" + session.getRange(), Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    initialize();

                }
            });
            dialog.show();
        }
       if(id == android.R.id.home){
           session.setActivitySwitchFlag("1");
           onBackPressed();
       }

        return super.onOptionsItemSelected(item);
    }


    public  void initialize(){

        if(!session.getLatitudeBaseLoc().equals(null) && !session.getLongitudeBaseLoc().equals(null)) {
            double latitude = Double.longBitsToDouble(session.getLatitudeBaseLoc());
            double longitude = Double.longBitsToDouble(session.getLongitudeBaseLoc());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Base Location")
                    .visible(true));

//             circle = mMap.addCircle(new CircleOptions()
//                    .center(new LatLng(latitude, longitude))
//                    .radius(Integer.parseInt(session.getRange()) * 1000)
//                    .strokeColor(Color.BLUE)
//                    .strokeWidth(2));


            LatLng latLng = new LatLng(latitude,longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 9);
            mMap.animateCamera(cameraUpdate);
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker!=null){
                    marker.remove();
//                    circle.remove();
                }
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .draggable(true)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Base Location")
                        .visible(true));

//                circle = mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(latLng.latitude, latLng.longitude))
//                        .radius(Integer.parseInt(session.getRange()) * 1000)
//                        .strokeColor(Color.BLUE)
//                        .strokeWidth(2));

                session.setLatitudeBaseLoc(latLng.latitude);
                session.setLongitudeBaseLoc(latLng.longitude);

                mToast.setText("Base Location Saved..!");
                mToast.show();


                double latitude = Double.longBitsToDouble(session.getLatitudeBaseLoc());
                double longitude = Double.longBitsToDouble(session.getLongitudeBaseLoc());
                System.out.println("from session "+ latitude +" "+ longitude);
                System.out.println(latLng.latitude + "---" + latLng.longitude);
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                System.out.println("start drag "+marker.getPosition().latitude+" "+marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                session.setLatitudeBaseLoc(marker.getPosition().latitude);
                session.setLongitudeBaseLoc(marker.getPosition().longitude);
//                if(circle!=null){
//                    circle.remove();
//                }
//                circle = mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
//                        .radius(Integer.parseInt(session.getRange()) * 1000)
//                        .strokeColor(Color.BLUE)
//                        .strokeWidth(2));
                mToast.setText("Base Location Saved..!");
                mToast.show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                session.setActivitySwitchFlag("1");
                onBackPressed();
                // do something here
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
