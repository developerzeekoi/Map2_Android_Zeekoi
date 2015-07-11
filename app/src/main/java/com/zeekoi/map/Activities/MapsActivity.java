package com.zeekoi.map.Activities;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zeekoi.map.Listeners.OnCALLWindowElemTouchListener;
import com.zeekoi.map.Listeners.OnInfoWindowElemTouchListener;
import com.zeekoi.map.Managers.AppLocationService;
import com.zeekoi.map.Managers.DBController;
import com.zeekoi.map.Managers.MapWrapperLayout;
import com.zeekoi.map.Managers.SessionManager;
import com.zeekoi.map.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    AppLocationService appLocationService;
    DBController controller = new DBController(this);
    boolean doubleBackToExitPressedOnce = false;
    private ViewGroup infoWindow;
    private TextView infoTitle, idForDelete;
    private TextView infoSnippet, phoneSnippet;
    private Button infoButton, callButton, baseLoc, MyLoc;
    private ImageButton imginfowindow;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private OnCALLWindowElemTouchListener callButtonListener;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    private SessionManager session;
    private String storeid = "";
    private double latJSON;
    private double longJSON;
    private List<Marker> markersList;
    private Toast mToast;
    private Marker markerTemp;
    private GoogleProgressBar progressBar;
    private Location gpsLocation;
    private Marker markerBaseLoc;
    private HashMap<String, String> MarkersDB;

    private static final double EARTH_RADIUS = 6378100.0;
    private int offset;

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        appLocationService = new AppLocationService(MapsActivity.this);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mMap = mapFragment.getMap(); // Might be null if Google Play services APK is not available.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (GoogleProgressBar) findViewById(R.id.progressBar);

        baseLoc = (Button) findViewById(R.id.baseLocation);
        MyLoc = (Button) findViewById(R.id.myLocation);

        progressBar.setVisibility(View.VISIBLE);

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


        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 10 + 5));
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_window_info, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
//        this.phoneSnippet = (TextView) infoWindow.findViewById(R.id.phone_snippet);
        this.imginfowindow = (ImageButton) infoWindow.findViewById(R.id.imageButton);
//        this.infoButton = (Button) infoWindow.findViewById(R.id.button);
//        this.infoButton.setBackgroundResource(R.drawable.star_disabled);
        callButton = (Button) infoWindow.findViewById(R.id.call_but);
//        callButton.setBackgroundResource(R.drawable.callimage);


        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        session = new SessionManager(getApplicationContext());

        if (session.getRange() == null) {
            session.setRange("20");
        }

        if (session.getButtonFlag() == null) {
            session.setButtonFlag("0");
        }

        gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (session.getButtonFlag().equals("0") || session.getButtonFlag() == null) {

            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                fetchMapDataApi(latitude, longitude, session.getRange());
            } else {
                showSettingsAlert("GPS");
            }
        }

        if (session.getButtonFlag().equals("1")) {

            fetchMapDataApi(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
                    Double.longBitsToDouble(session.getLongitudeBaseLoc()),
                    session.getRange());
        }

        baseLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (Double.longBitsToDouble(session.getLatitudeBaseLoc()) == 0.0) {

                    Intent BaseLocIntent = new Intent(getApplicationContext(), BaseLoc_MapActivity.class);
                    startActivity(BaseLocIntent);

                } else {
                    session.setButtonFlag("1");
                    mMap.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    fetchMapDataApi(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
                            Double.longBitsToDouble(session.getLongitudeBaseLoc()),
                            session.getRange());

                }
            }
        });

        MyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (gpsLocation != null) {
                    mMap.clear();
                    session.setButtonFlag("0");
                    latitude = gpsLocation.getLatitude();
                    longitude = gpsLocation.getLongitude();
                    fetchMapDataApi(latitude, longitude, session.getRange());
                } else {
                    session.setButtonFlag("0");
                    mMap.clear();
                    showSettingsAlert("GPS");
                }
            }
        });

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker.getTitle().equals("Base Location")) {
//                    System.out.println("marker clicked" + marker.getTitle());
//                    Intent io = new Intent(getApplicationContext(),NoBoringActionBarActivity.class);
//                    startActivity(io);
//
//
//                }
//                return false;
//            }
//        });
//infoWindow.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        System.out.println("info window clicked "+v.getId());
//        Intent io = new Intent(getApplicationContext(),NoBoringActionBarActivity.class);
//                    startActivity(io);
//    }
//});

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                if (marker.getTitle().equals("Base Location")) {
                    System.out.println("no activity for base location");
                    callButton.setVisibility(View.INVISIBLE);
                    imginfowindow.setVisibility(View.INVISIBLE);
                } else{
                    callButton.setVisibility(View.VISIBLE);
                    imginfowindow.setVisibility(View.VISIBLE);

                }
//                try {
//                    SQLiteDatabase db = controller.getWritableDatabase();
//                    String flag = controller.checkDB(marker.getPosition().latitude, marker.getPosition().longitude);
//                    if (flag == "full") {
//                        infoButton.setBackgroundResource(R.drawable.star_enabled);
//                    } else {
//                        infoButton.setBackgroundResource(R.drawable.star_disabled);
//                    }
//                } catch (SQLException e) {
//                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
//                infoWindow.setVisibility(View.VISIBLE);
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                markerTemp = marker;
                if (marker.getTitle().equals("Base Location")) {
                    System.out.println("no activity for base location");
                    callButton.setVisibility(View.INVISIBLE);
                    imginfowindow.setVisibility(View.INVISIBLE);
                } else{
                    callButton.setVisibility(View.VISIBLE);
                    imginfowindow.setVisibility(View.VISIBLE);

                }
//                infoButton.setVisibility(View.VISIBLE);
//                callButton.setVisibility(View.VISIBLE);
//                imginfowindow.setVisibility(View.VISIBLE);
//               / System.out.println("title bas loc "+markerBaseLoc.getTitle());

//                        if(markerBaseLoc.getTitle().equals("Base Location")){
//                            System.out.println("title bas loc loop " + markerBaseLoc.getTitle());
//                            infoButton.setVisibility(View.INVISIBLE);
//                            callButton.setVisibility(View.INVISIBLE);
//                            imginfowindow.setVisibility(View.INVISIBLE);
//
//                        }

//                infoButtonListener.setMarker(marker);
                callButtonListener.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }//onCreate method end

    @Override
    public void onResume() {
        super.onResume();
        if (session.getActivitySwitchFlag() != null) {

            if (session.getActivitySwitchFlag().equals("1")) {
                mMap.clear();
                markersList.clear();
                session.setButtonFlag("1");
                progressBar.setVisibility(View.VISIBLE);
                fetchMapDataApi(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
                        Double.longBitsToDouble(session.getLongitudeBaseLoc()),
                        session.getRange());
                session.setActivitySwitchFlag("0");
            }
        }
    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setTitle(provider + " SETTINGS");
        alertDialog.setMessage(provider + " is not enabled! Want to go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MapsActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //if cancel dialog box ..fetch location from network
                        Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

                        if (nwLocation != null) {
                            fetchMapDataApi(nwLocation.getLatitude(), nwLocation.getLongitude(), session.getRange());
                        }
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.favourites) {
            try {
                markerTemp.hideInfoWindow();
            } catch (NullPointerException ex) {

            }
            Intent favIntent = new Intent(getApplicationContext(), fav_activity.class);
            startActivity(favIntent);
        }
        if (id == R.id.select_range) {
            try {
                markerTemp.hideInfoWindow();
            } catch (NullPointerException ex) {

            }
            final Dialog dialog = new Dialog(MapsActivity.this, R.style.cust_dialog);
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
                    progressBar.setVisibility(View.VISIBLE);
                    fetchMapDataApi(Double.longBitsToDouble(session.getLastLocLatitude()),
                            Double.longBitsToDouble(session.getLastLocLongitude()), session.getRange());

//                    Intent intent = getIntent();
//                    finish();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(intent);
                }
            });
            dialog.show();
        }

        if (id == R.id.setBaseLocation) {
            Intent BaseLocIntent = new Intent(getApplicationContext(), BaseLoc_MapActivity.class);
            startActivity(BaseLocIntent);

        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchMapDataApi(final double latitude, final double longitude, final String range) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("latitude", "" + latitude);
        params.put("longitude", "" + longitude);
        params.put("range", range);
        String apiKey = getString(R.string.API_KEY).toString();
        client.post("http://thanthrimandalam.com/demo/Api.php?key=" + apiKey + "&action=getstores", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                controller.dropMarkers();

                session.setResponse(response);

                System.out.println(response);
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jarray = jobject.getAsJsonArray("data");
                session.setMarkerCount(String.valueOf(jarray.size()));
                session.setLastLocLatitude(latitude);
                session.setLastLocLongitude(longitude);

                markersList = new ArrayList<Marker>();
                MarkersDB = new HashMap<String, String>();
                for (int i = 0; i < jarray.size(); i++) {
                    jobject = jarray.get(i).getAsJsonObject();
                    storeid = jobject.get("storeid").getAsString();
                    String name = jobject.get("name").getAsString().toUpperCase();
                    String address = jobject.get("address").getAsString();
                    String phone = jobject.get("phone").getAsString();
                    String nameAddress = "<strong>" + name + "</strong><br>" + address;
                    latJSON = jobject.get("lat").getAsDouble();
                    longJSON = jobject.get("long").getAsDouble();
                    Spanned spannedContent = Html.fromHtml(nameAddress);
                    Marker markerobj = mMap.addMarker(new MarkerOptions()
                            .title(String.valueOf(Html.fromHtml(nameAddress)))
                            .snippet(phone)
                            .position(new LatLng(latJSON, longJSON)));
                    markersList.add(markerobj);
                    MarkersDB.put("marker_id", markerobj.getId());
                    MarkersDB.put("title", nameAddress);
                    MarkersDB.put("snippet", phone);
                    MarkersDB.put("image_url", jobject.get("image").getAsString());
                    controller.insertMarkers(MarkersDB);
                }


//                if(session.getButtonFlag().equals("1")){
//
//                    Circle circle = mMap.addCircle(new CircleOptions()
//                            .center(new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
//                                    Double.longBitsToDouble(session.getLongitudeBaseLoc())))
//                            .radius(Integer.parseInt(session.getRange()) * 1000)
//                            .strokeColor(Color.BLUE)
//                            .strokeWidth(2));
//                }

                if (markersList.size() == 0 || session.getButtonFlag().equals("1")) {

                    mToast.setText(markersList.size() + " Stores found");
                    mToast.show();

                    markerBaseLoc = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
                                    Double.longBitsToDouble(session.getLongitudeBaseLoc())))

                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title("Base Location"));


                    LatLng latLng = new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc())
                            , Double.longBitsToDouble(session.getLongitudeBaseLoc()));


//                    Circle circle = mMap.addCircle(new CircleOptions()
//                            .center(new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
//                                    Double.longBitsToDouble(session.getLongitudeBaseLoc())))
//                            .radius(Integer.parseInt(session.getRange()) * 1000)
//                            .strokeColor(Color.BLUE)
//                            .strokeWidth(2));

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 9);
                    mMap.animateCamera(cameraUpdate);

                } else {
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    for (Marker m : markersList) {
                        b.include(m.getPosition());
                    }
                    LatLngBounds bounds = b.build();
                    //Change the padding as per needed

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25));
                }
                //Calculate the markers to get their position
                YoYo.with(Techniques.FadeOut).duration(500).delay(100).playOn(progressBar);


                callButtonListener = new OnCALLWindowElemTouchListener(callButton, getApplicationContext()) {
                    @Override
                    protected void onClickConfirmed(View v, Marker marker) {
                        System.out.println("resonse=" + session.getResponse());
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(infoWindow.findViewById(R.id.call_but));


//                        if(marker.getSnippet().toString().equals("")){
//                            mToast.setText("Phone Number not available...");
//                            mToast.show();
//                        }else {
//                            Intent callIntent = new Intent(Intent.ACTION_CALL);
//                            callIntent.setData(Uri.parse("tel:" + marker.getSnippet().toString()));
//                            startActivity(callIntent);
//                        }
                        if (marker.getTitle().equals("Base Location")) {
                            System.out.println("no activity for base location");
                        } else {
                            mToast.setText("Loading...");
                            mToast.show();
                            Intent io = new Intent(getApplicationContext(), NoBoringActionBarActivity.class);
                            io.putExtra("markerID", marker.getId());
                            session.setTemplat(marker.getPosition().latitude);
                            session.setTempLong(marker.getPosition().longitude);
                            startActivity(io);
                        }

                    }
                };
                callButton.setOnTouchListener(callButtonListener);

//                infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
//                        getResources().getDrawable(R.drawable.star_disabled),
//                        getResources().getDrawable(R.drawable.star_enabled), getApplicationContext()) {
//
//                    @Override
//                    protected void onClickConfirmed(View v, Marker marker) {
//                        // Here we can perform some action triggered after clicking the button
//                        String markerid = marker.getId();
//                        for (int j = 0; j < Integer.parseInt(session.getMarkerCount()); j++) {
//                            String concateID = "m" + j;
//                            if (markerid.equals(concateID)) {
//                                try {
//                                    SQLiteDatabase db = controller.getWritableDatabase();
//                                    String flag = controller.checkDB(marker.getPosition().latitude, marker.getPosition().longitude);
//                                    if (flag == "full") {
//                                        mToast.setText("Removed from Favourites..");
//                                        mToast.show();
//                                        synchronized (db) {
//                                            db.execSQL("DELETE from favourites WHERE " +
//                                                    "latitude='" + marker.getPosition().latitude + "" +
//                                                    "' and longitude='" + marker.getPosition().longitude + "' and" +
//                                                    " phone = '" + marker.getSnippet().toString() + "'");
//                                        }
//                                        v.setBackgroundResource(R.drawable.star_disabled);
//                                        marker.hideInfoWindow();
//                                        marker.showInfoWindow();
//                                        break;
//                                    } else {
//                                        mToast.setText("Added to Favourites..");
//                                        mToast.show();
//                                        synchronized (db) {
//                                            db.execSQL("INSERT INTO favourites(name,phone,latitude,longitude) VALUES" +
//                                                    "('" + marker.getTitle() + "','" + marker.getSnippet() + "'," +
//                                                    "'" + marker.getPosition().latitude + "','" + marker.getPosition().longitude + "')");
//                                        }
//                                        v.setBackgroundResource(R.drawable.star_enabled);
//                                        marker.hideInfoWindow();
//                                        marker.showInfoWindow();
//                                        break;
//                                    }
//
//                                } catch (SQLException e) {
//                                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
//                                    e.printStackTrace();
//                                }
//                                break;
//                            }
//                        }
//                    }
//                };
//                infoButton.setOnTouchListener(infoButtonListener);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {

                Toast.makeText(getApplicationContext(), "Device might not be connected to Internet", Toast.LENGTH_LONG).show();

                //load map from cache and shows locations from saved datas
                String Json_Session = session.getResponse();
                JsonElement jelement = new JsonParser().parse(Json_Session);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jarray = jobject.getAsJsonArray("data");
                session.setMarkerCount(String.valueOf(jarray.size()));

                markersList = new ArrayList<Marker>();
                for (int i = 0; i < jarray.size(); i++) {
                    jobject = jarray.get(i).getAsJsonObject();
                    storeid = jobject.get("storeid").getAsString();
                    String name = jobject.get("name").getAsString().toUpperCase();
                    String address = jobject.get("address").getAsString();
                    String phone = jobject.get("phone").getAsString();
                    String nameAddress = "<b>" + name + "</b><br>" + address;
                    latJSON = jobject.get("lat").getAsDouble();
                    longJSON = jobject.get("long").getAsDouble();
                    Spanned spannedContent = Html.fromHtml(nameAddress);
                    Marker markerobj = mMap.addMarker(new MarkerOptions()
                            .title(spannedContent.toString())
                            .snippet(phone)
                            .position(new LatLng(latJSON, longJSON)));
                    markersList.add(markerobj);
                }


                if (markersList.size() == 0 || session.getButtonFlag().equals("1")) {
                    mToast.setText(markersList.size() + " Stores found");
                    mToast.show();
                    markerBaseLoc = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
                                    Double.longBitsToDouble(session.getLongitudeBaseLoc())))

                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title("Base Location"));
                    markerBaseLoc.hideInfoWindow();
                    LatLng latLng = new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc())
                            , Double.longBitsToDouble(session.getLongitudeBaseLoc()));

//                    Circle circle = mMap.addCircle(new CircleOptions()
//                            .center(new LatLng(Double.longBitsToDouble(session.getLatitudeBaseLoc()),
//                                    Double.longBitsToDouble(session.getLongitudeBaseLoc())))
//                            .radius(Integer.parseInt(session.getRange()) * 1000)
//                            .strokeColor(Color.BLUE)
//                            .strokeWidth(2));

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
                    mMap.animateCamera(cameraUpdate);
                    YoYo.with(Techniques.FadeOut).duration(500).delay(100).playOn(progressBar);

                } else {
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    for (Marker m : markersList) {
                        b.include(m.getPosition());
                    }
                    LatLngBounds bounds = b.build();
                    //Change the padding as per needed
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25));
                }
                //Calculate the markers to get their position

                callButtonListener = new OnCALLWindowElemTouchListener(callButton, getApplicationContext()) {
                    @Override
                    protected void onClickConfirmed(View v, Marker marker) {
                        System.out.println("resonse=" + session.getResponse());
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(infoWindow.findViewById(R.id.call_but));


                        if (marker.getTitle().equals("Base Location")) {
                        System.out.println("no activity for base location");
                    } else {
                            mToast.setText("Loading...");
                            mToast.show();
                        Intent io = new Intent(getApplicationContext(), NoBoringActionBarActivity.class);
                        io.putExtra("markerID", marker.getId());
                        session.setTemplat(marker.getPosition().latitude);
                        session.setTempLong(marker.getPosition().longitude);
                        startActivity(io);
                    }
                    }
                };
                callButton.setOnTouchListener(callButtonListener);
//
//                infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
//                        getResources().getDrawable(R.drawable.star_disabled),
//                        getResources().getDrawable(R.drawable.star_enabled), getApplicationContext()) {
//
//                    @Override
//                    protected void onClickConfirmed(View v, Marker marker) {
//                        // Here we can perform some action triggered after clicking the button
//                        String markerid = marker.getId();
//                        for (int j = 0; j < Integer.parseInt(session.getMarkerCount()); j++) {
//                            String concateID = "m" + j;
//                            if (markerid.equals(concateID)) {
//                                try {
//                                    SQLiteDatabase db = controller.getWritableDatabase();
//                                    String flag = controller.checkDB(marker.getPosition().latitude, marker.getPosition().longitude);
//                                    if (flag == "full") {
//                                        mToast.setText("Removed from Favourites..");
//                                        mToast.show();
//                                        synchronized (db) {
//                                            db.execSQL("DELETE from favourites WHERE " +
//                                                    "latitude='" + marker.getPosition().latitude + "" +
//                                                    "' and longitude='" + marker.getPosition().longitude + "' and " +
//                                                    "phone = '" + marker.getSnippet().toString() + "'");
//                                        }
//                                        v.setBackgroundResource(R.drawable.star_disabled);
//                                        marker.hideInfoWindow();
//                                        marker.showInfoWindow();
//                                        break;
//                                    } else {
//                                        mToast.setText("Added to Favourites..");
//                                        mToast.show();
//                                        synchronized (db) {
//                                            db.execSQL("INSERT INTO favourites(name,phone,latitude,longitude) VALUES" +
//                                                    "('" + marker.getTitle() + "','" + marker.getSnippet() + "'," +
//                                                    "'" + marker.getPosition().latitude + "','" + marker.getPosition().longitude + "')");
//                                        }
//                                        v.setBackgroundResource(R.drawable.star_enabled);
//                                        marker.hideInfoWindow();
//                                        marker.showInfoWindow();
//                                        break;
//                                    }
//
//                                } catch (SQLException e) {
//                                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
//                                    e.printStackTrace();
//                                }
//                                break;
//                            }
//                        }
//                    }
//                };
//                infoButton.setOnTouchListener(infoButtonListener);
                progressBar.setVisibility(View.INVISIBLE);
            } //onFailure ends
        });
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            session.setActivitySwitchFlag("0");
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }

}
