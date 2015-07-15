package com.zeekoi.map.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.vstechlab.easyfonts.EasyFonts;
import com.zeekoi.map.Managers.DBController;
import com.zeekoi.map.Managers.ResizableImageView;
import com.zeekoi.map.Managers.SessionManager;
import com.zeekoi.map.R;

public class CollapsingToolbar extends AppCompatActivity {
    private static final String TAG = "StoreInfoActivity";
    DBController controller = new DBController(this);
    private SessionManager session;
    private String markerTitle;
    private String markerSnippet;
    private String imageURL, phone;
    private ResizableImageView call, msg;
    private Bitmap bitmap;
    private Toast mToast;
    private Double latitude;
    private Double longitude;
    private GoogleProgressBar progressBar;
    private FloatingActionButton fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_toolbar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Store Details");


        session = new SessionManager(getApplicationContext());
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        try {
            Intent i = getIntent();
            String markerID = i.getStringExtra("markerID");
            latitude = Double.longBitsToDouble(session.getTemplat());
            longitude = Double.longBitsToDouble(session.getTemplong());
            System.out.println("marker id click " + markerID + "lat " + latitude + " lon " + longitude);
            SQLiteDatabase db = controller.getWritableDatabase();
            Cursor c = db.rawQuery("select * from markers where marker_id='" + markerID + "'", null);
            while (c.moveToNext()) {
                System.out.println("marker title " + c.getString(2));
                markerTitle = c.getString(2);
                markerSnippet = c.getString(3);
                phone = c.getString(4);
                imageURL = c.getString(5);
                System.out.println(c.getString(5));
                System.out.println(c);
            }
        } catch (NullPointerException r) {

        }

        loadImage();

        fav = (FloatingActionButton) findViewById(R.id.fav);
        call = (ResizableImageView) findViewById(R.id.call);
        msg = (ResizableImageView) findViewById(R.id.msg);
        progressBar = (GoogleProgressBar) findViewById(R.id.googleProgressbar);
        progressBar.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.markerTitle);
        title.setText(String.valueOf(Html.fromHtml(markerTitle)));
        title.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        TextView snippet = (TextView) findViewById(R.id.markerSnippet);
        TextView phoneDB = (TextView) findViewById(R.id.phone_storeinfo);
        TextView locAddress = (TextView) findViewById(R.id.locAddress);
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView duration = (TextView) findViewById(R.id.duration);
        TextView durationJson = (TextView) findViewById(R.id.distancejson);

        snippet.setText(markerSnippet);
        phoneDB.setText("Tel. No. :  " + phone);
        phoneDB.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        snippet.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        locAddress.setText(session.getAddressText());
        distance.setText(session.getDistanceText() + " away");
        duration.setText(session.getDurationText());
        distance.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        duration.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        phoneDB.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        locAddress.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));


        try {
            SQLiteDatabase db = controller.getWritableDatabase();
            String flag = controller.checkDB(latitude, longitude);
            if (flag == "full") {
                System.out.println("db full star enabled");
                fav.setImageResource(android.R.drawable.btn_star_big_on);

            } else {
                fav.setImageResource(android.R.drawable.btn_star_big_off);
            }
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = controller.getWritableDatabase();
                    String flag = controller.checkDB(latitude, longitude);
                    if (flag == "full") {
                        mToast.setText("Removed from Favourites..");
                        mToast.show();
                        synchronized (db) {
                            db.execSQL("DELETE from favourites WHERE " +
                                    "latitude='" + latitude + "" +
                                    "' and longitude='" + longitude + "' and " +
                                    "phone = '" + markerSnippet + "'");
                        }
//                                        fav.setBackgroundResource(R.drawable.star_disabled);
                        fav.setImageResource(android.R.drawable.btn_star_big_off);


                    } else {
                        mToast.setText("Added to Favourites..");
                        mToast.show();
                        synchronized (db) {
                            db.execSQL("INSERT INTO favourites(name,phone,latitude,longitude) VALUES" +
                                    "('" + markerTitle + "','" + markerSnippet + "'," +
                                    "'" + latitude + "','" + longitude + "')");
                        }

                        fav.setImageResource(android.R.drawable.btn_star_big_on);

                    }

                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.ZoomIn).duration(500).delay(100).playOn(findViewById(R.id.call));
                mToast.setText("calling...");
                mToast.show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.ZoomIn).duration(500).delay(100).playOn(findViewById(R.id.msg));
                mToast.setText("Sending text...");
                mToast.show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + phone)));
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_collapsing_toolbar, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadImage() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(imageURL).centerCrop().into(imageView);
    }
}
