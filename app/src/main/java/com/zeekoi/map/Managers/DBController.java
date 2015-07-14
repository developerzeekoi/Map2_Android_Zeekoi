package com.zeekoi.map.Managers;
/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static String ID = "id";
    private SQLiteDatabase mDb;

    public DBController(Context applicationcontext) {
        super(applicationcontext, "map.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query, query_marker;
        query = "CREATE TABLE favourites ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, phone TEXT, latitude TEXT, longitude TEXT)";
        database.execSQL(query);
        query_marker = "CREATE TABLE markers ( _id INTEGER PRIMARY KEY AUTOINCREMENT, marker_id TEXT, title TEXT, snippet TEXT, phone TEXT ,image_url TEXT)";
        database.execSQL(query_marker);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS favourites";
        database.execSQL(query);
        onCreate(database);
    }


    /**
     * Inserts favourites into SQLite DB
     *
     * @param queryValues
     */
    public void insertMarkers(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(ID, queryValues.get("_id"));
        values.put("marker_id", queryValues.get("marker_id"));
        values.put("title", queryValues.get("title"));
        values.put("snippet", queryValues.get("snippet"));
        values.put("phone", queryValues.get("phone"));
        values.put("image_url", queryValues.get("image_url"));

        database.insert("markers", null, values);
        database.close();
        System.out.println("markers inserted "+values.size());
    }

    public void dropMarkers(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query;
        query = "DELETE  FROM markers";
        database.execSQL(query);
        System.out.println("markers values deleted");
    }

    /**
     * Get list of favourites from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllFav() {
        ArrayList<HashMap<String, String>> favList;
        favList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM favourites";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(ID, cursor.getString(0));
                map.put(NAME, cursor.getString(1));
                map.put(ADDRESS, cursor.getString(2));
                map.put(PHONE, cursor.getString(3));
                map.put(LATITUDE, cursor.getString(4));
                map.put(LONGITUDE, cursor.getString(5));
                favList.add(map);

            } while (cursor.moveToNext());
        }
        database.close();
        return favList;
    }

    public String checkDB(Double lat, Double lon) {

        SQLiteDatabase db = this.getWritableDatabase();
//        System.out.println("SELECT * FROM favourites where latitude='" + lat + "'and longitude='" + lon + "'");
        Cursor c = db.rawQuery("SELECT * FROM favourites where latitude='" + lat + "' and longitude='" + lon + "' ", null);
        String flag = "empty";

        if (c.getCount() == 0) {
            return flag;
        } else {
            flag = "full";
            return flag;
        }
    }

}
