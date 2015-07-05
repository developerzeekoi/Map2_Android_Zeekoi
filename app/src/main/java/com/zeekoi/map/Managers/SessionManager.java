package com.zeekoi.map.Managers;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String RANGE = "range";
    private static final String PREF_NAME = "Map";
    private static final String MARKER_COUNT = "count";
    private static final String IS_CLICKED = "clicked";
    private static final String RESPONSE = "response";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setResponse(String HTTPresponse) {
        editor.putString(RESPONSE, HTTPresponse);
        editor.commit();
        Log.d(TAG, "response saved..!");
    }

    public void setRange(String range) {
        editor.putString(RANGE, range);
        editor.commit();
        Log.d(TAG, "range saved..!");
    }

    public void setMarkerCount(String count) {
        editor.putString(MARKER_COUNT, count);
        editor.commit();
        Log.d(TAG, "Counter Saved");

    }

    public void setCLick(String click) {

        editor.putString(IS_CLICKED, click);
        editor.commit();
        Log.d(TAG, "click Saved");
    }

    public String getResponse() {
        return pref.getString(RESPONSE, null);
    }

    public String getClick() {
        return pref.getString(IS_CLICKED, null);
    }

    public String getRange() {
        return pref.getString(RANGE, null);
    }

    public String getMarkerCount() {
        return pref.getString(MARKER_COUNT, null);
    }


}
