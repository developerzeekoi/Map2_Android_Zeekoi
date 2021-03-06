package com.zeekoi.map.Managers;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Shared preferences file name
    private static final String RANGE = "range";
    private static final String PREF_NAME = "Map";
    private static final String MARKER_COUNT = "count";
    private static final String IS_CLICKED = "clicked";
    private static final String RESPONSE = "response";
    private static final String LATITUDE_BASE_LOC = "lattitude base location";
    private static final String LONGITUDE_BASE_LOC = "longitude base location";
    private static final String BUTTON_FLAG = "button flag onclick";
    private static final String LAST_LOC_LAT = "last location latitude";
    private static final String LAST_LOC_LONG = "last location longitude";
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setCLick(String click) {

        editor.putString(IS_CLICKED, click);
        editor.commit();
        Log.d(TAG, "click Saved");
    }

    public void setTempLong(double LastLongitude) {
        editor.putLong("temp long", Double.doubleToLongBits(LastLongitude));
        editor.commit();
//        Log.d(TAG, "LAST_LOC_LONG saved");
    }

    public String getDistanceText() {
        return pref.getString("DistanceText", null);
    }

    public void setDistanceText(String v) {
        editor.putString("DistanceText", v);
        editor.commit();
    }

    public String getDurationText() {
        return pref.getString("DurationText", null);
    }

    public void setDurationText(String v) {
        editor.putString("DurationText", v);
        editor.commit();
    }

    public String getAddressText() {
        return pref.getString("AddressText", null);
    }

    public void setAddressText(String v) {
        editor.putString("AddressText", v);
        editor.commit();
    }

    public String getActivitySwitchFlag() {
        return pref.getString("ActivitySwitchFlag", null);
    }

    public void setActivitySwitchFlag(String ActivitySwitchFlag){
        editor.putString("ActivitySwitchFlag", ActivitySwitchFlag);
        editor.commit();

    }

    public Long getTemplat() {
        return pref.getLong("temp lat", 0);
    }

    public void setTemplat(double LastLatitude){
        editor.putLong("temp lat", Double.doubleToLongBits(LastLatitude));
        editor.commit();
//        Log.d(TAG, "LAST_LOC_LAT saved");
    }

    public Long getTemplong(){ return pref.getLong("temp long", 0);}

    public Long getLastLocLatitude(){ return pref.getLong(LAST_LOC_LAT, 0);}

    public void setLastLocLatitude(double LastLatitude) {
        editor.putLong(LAST_LOC_LAT, Double.doubleToLongBits(LastLatitude));
        editor.commit();
        Log.d(TAG, "LAST_LOC_LAT saved");
    }

    public Long getLastLocLongitude(){ return pref.getLong(LAST_LOC_LONG, 0);}

    public void setLastLocLongitude(double LastLongitude) {
        editor.putLong(LAST_LOC_LONG, Double.doubleToLongBits(LastLongitude));
        editor.commit();
        Log.d(TAG, "LAST_LOC_LONG saved");
    }

    public String getButtonFlag(){ return pref.getString(BUTTON_FLAG, null);}

    public void setButtonFlag(String Buttonflag) { // 0 for mylocation  1 for base location
        editor.putString(BUTTON_FLAG, Buttonflag);
        editor.commit();
        Log.d(TAG, "BUTTON_FLAG saved");
    }

    public String getResponse() {
        return pref.getString(RESPONSE, null);
    }

    public void setResponse(String HTTPresponse) {
        editor.putString(RESPONSE, HTTPresponse);
        editor.commit();
        Log.d(TAG, "response saved..!");
    }

    public String getClick() {
        return pref.getString(IS_CLICKED, null);
    }

    public String getRange() {
        return pref.getString(RANGE, null);
    }

    public void setRange(String range) {
        editor.putString(RANGE, range);
        editor.commit();
        Log.d(TAG, "range saved..!");
    }

    public String getMarkerCount() {
        return pref.getString(MARKER_COUNT, null);
    }

    public void setMarkerCount(String count) {
        editor.putString(MARKER_COUNT, count);
        editor.commit();
        Log.d(TAG, "Counter Saved");

    }

    public Long getLatitudeBaseLoc() {
        return pref.getLong(LATITUDE_BASE_LOC, 0);
    }

    public void setLatitudeBaseLoc(double latitudeBaseLoc) {
        editor.putLong(LATITUDE_BASE_LOC, Double.doubleToLongBits(latitudeBaseLoc));
        editor.commit();
        Log.d(TAG, "LATITUDE_BASE_LOC saved");
    }

    public Long getLongitudeBaseLoc() {
        return pref.getLong(LONGITUDE_BASE_LOC, 0);
    }

    public void setLongitudeBaseLoc(double longitudeBaseLoc) {
        editor.putLong(LONGITUDE_BASE_LOC, Double.doubleToLongBits(longitudeBaseLoc));
        editor.commit();
        Log.d(TAG, "LONGITUDE_BASE_LOC saved");
    }


}
