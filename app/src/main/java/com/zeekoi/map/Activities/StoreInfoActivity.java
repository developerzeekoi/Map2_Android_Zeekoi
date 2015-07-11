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
    private String imageURL;
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
                imageURL = c.getString(4);
                System.out.println(c.getString(4));
                System.out.println(c);
            }
        } catch (NullPointerException r) {

        }

        fav = (ImageView) findViewById(R.id.fav);
        call = (ImageView) findViewById(R.id.call);
        progressBar = (GoogleProgressBar) findViewById(R.id.progressBar);
        TextView title = (TextView) findViewById(R.id.markerTitle);
        title.setText(String.valueOf(Html.fromHtml(markerTitle)));
        title.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        TextView snippet = (TextView) findViewById(R.id.markerSnippet);
        snippet.setText("Tel. No. :  "+markerSnippet);
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
                callIntent.setData(Uri.parse("tel:" + markerSnippet));
                startActivity(callIntent);
            }
        });


//        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
//        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
//        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();
//
//
//
//
//        mListView = (ListView) findViewById(R.id.listview);
//        mHeader = findViewById(R.id.toolbar1);
//        mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
//        mHeaderPicture.setResourceIds(R.drawable.picture1, R.drawable.picture1);
//        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
//
//        mActionBarTitleColor = getResources().getColor(R.color.actionbar_title_color);
//
//        mSpannableString = new SpannableString(getString(R.string.noboringactionbar_title));
//        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
//
//        setupActionBar();
//
//
//
//        setupListView();
//    }
//
//    private void setupListView() {
//        ArrayList<String> FAKES = new ArrayList<String>();
//        FAKES.add(String.valueOf(Html.fromHtml(markerTitle)));
//        FAKES.add(markerSnippet);
////        for (int i = 0; i < 1000; i++) {
////            FAKES.add("entry " + i);
////        }
//        mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
//        mListView.addHeaderView(mPlaceHolderView);
//        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FAKES));
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int scrollY = getScrollY();
//                //sticky actionbar
//                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
//                //header_logo --> actionbar icon
//                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
//                interpolate(mHeaderLogo, getActionBarIconView(), mSmoothInterpolator.getInterpolation(ratio));
//                //actionbar title alpha
//                //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
//                //---------------------------------
//                //better way thanks to @cyrilmottier
//                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
//            }
//        });
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void setTitleAlpha(float alpha) {
//        mAlphaForegroundColorSpan.setAlpha(alpha);
//        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getActionBar().setTitle(mSpannableString);
//    }
//
//    public static float clamp(float value, float min, float max) {
//        return Math.max(min, Math.min(value, max));
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void interpolate(View view1, View view2, float interpolation) {
//        getOnScreenRect(mRect1, view1);
//        getOnScreenRect(mRect2, view2);
//
//        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
//        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
//        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
//        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));
//
//        view1.setTranslationX(translationX);
//        view1.setTranslationY(translationY - mHeader.getTranslationY());
//        view1.setScaleX(scaleX);
//        view1.setScaleY(scaleY);
//    }
//
//    private RectF getOnScreenRect(RectF rect, View view) {
//        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
//        return rect;
//    }
//
//    public int getScrollY() {
//        View c = mListView.getChildAt(0);
//        if (c == null) {
//            return 0;
//        }
//
//        int firstVisiblePosition = mListView.getFirstVisiblePosition();
//        int top = c.getTop();
//
//        int headerHeight = 0;
//        if (firstVisiblePosition >= 1) {
//            headerHeight = mPlaceHolderView.getHeight();
//        }
//
//        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
//    }
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    private void setupActionBar() {
//        ActionBar actionBar = getActionBar();
//        actionBar.setIcon(R.drawable.ic_cast_light);
//
//        //getActionBarTitleView().setAlpha(0f);
//    }
//
//    private ImageView getActionBarIconView() {
//        return (ImageView) findViewById(android.R.id.home);
//    }
//
//    /*private TextView getActionBarTitleView() {
//        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        return (TextView) findViewById(id);
//    }*/
//
//    public int getActionBarHeight() {
//        if (mActionBarHeight != 0) {
//            return mActionBarHeight;
//        }
//        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
//        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
//        return mActionBarHeight;
//    }
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
//                temp.setImageBitmap(image);//setting profile image from url in do..in..backgroud task
                ImageView header_picture = (ImageView) findViewById(R.id.headerImage);
                header_picture.setImageBitmap(image);
//                temp.setVisibility(View.INVISIBLE);

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(StoreInfoActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


}