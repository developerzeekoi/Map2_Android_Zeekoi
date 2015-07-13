package com.zeekoi.map.Activities;

/**
 * Created by Nowfal on 7/11/2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.kogitune.activity_transition.ActivityTransition;
import com.vstechlab.easyfonts.EasyFonts;
import com.zeekoi.map.Managers.DBController;
import com.zeekoi.map.Managers.SessionManager;

import com.zeekoi.map.R;

import java.io.InputStream;
import java.net.URL;

public class StoreInfoActivity extends AppCompatActivity {

    private static final String TAG = "StoreInfoActivity";
    DBController controller = new DBController(this);
    private SessionManager session;
    private String markerTitle;
    private String markerSnippet;
    private String imageURL, phone;
    private ImageView call;
    private Bitmap bitmap;
    private Toast mToast;
    private Double latitude;
    private Double longitude;
    private GoogleProgressBar progressBar;
    private ImageView fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_noboringactionbar);
        setContentView(R.layout.storeinfo_activity);
//        temp = (ImageView)findViewById(R.id.temp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//        ActivityTransition.with(getIntent()).to(findViewById(R.id.card_view5)).start(savedInstanceState);

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
                markerTitle = c.getString(2);
                markerSnippet = c.getString(3);
                phone = c.getString(4);
                imageURL = c.getString(5);
                System.out.println(c.getString(5));
                System.out.println(c);
            }
        } catch (NullPointerException r) {

        }

        fav = (ImageView) findViewById(R.id.fav);
        call = (ImageView) findViewById(R.id.call);
        progressBar = (GoogleProgressBar) findViewById(R.id.progressBarStore);
        progressBar.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.markerTitle);
        title.setText(String.valueOf(Html.fromHtml(markerTitle)));
        title.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        TextView snippet = (TextView) findViewById(R.id.markerSnippet);
        TextView phoneDB = (TextView) findViewById(R.id.phone_storeinfo);
        snippet.setText(markerSnippet);
        phoneDB.setText("Tel. No. :  "+phone);
        System.out.println("phoone "+phone);
        snippet.setTypeface(EasyFonts.caviarDreamsBoldItalic(getApplicationContext()));
        new LoadImage().execute(imageURL);

        try {
            SQLiteDatabase db = controller.getWritableDatabase();
            String flag = controller.checkDB(latitude, longitude);
            if (flag == "full") {
                fav.setBackgroundResource(R.drawable.star_enabled);
            } else {
                fav.setBackgroundResource(R.drawable.star_disabled);
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
                        fav.setImageResource(R.drawable.star_disabled);


                    } else {
                        mToast.setText("Added to Favourites..");
                        mToast.show();
                        synchronized (db) {
                            db.execSQL("INSERT INTO favourites(name,phone,latitude,longitude) VALUES" +
                                    "('" + markerTitle + "','" + markerSnippet + "'," +
                                    "'" + latitude + "','" + longitude + "')");
                        }

                        fav.setImageResource(R.drawable.star_enabled);

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
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(findViewById(R.id.call));
                mToast.setText("calling...");
                mToast.show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {

        }

        @Override

        protected Bitmap doInBackground(String... args) {
            try {
                System.out.println("loadimg img");
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                System.out.println("loadimg img done");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                progressBar.setVisibility(View.INVISIBLE);
                ImageView header_picture = (ImageView) findViewById(R.id.headerImage);
                header_picture.setImageBitmap(image);
            } else {
//                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(StoreInfoActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


}