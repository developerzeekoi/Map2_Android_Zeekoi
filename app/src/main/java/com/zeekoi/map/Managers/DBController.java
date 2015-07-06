package com.zeekoi.map.Managers;
/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBController extends SQLiteOpenHelper {
    private SQLiteDatabase mDb;
    private static String ID = "id";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    public DBController(Context applicationcontext) {
        super(applicationcontext, "map.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE favourites ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, phone TEXT, latitude TEXT, longitude TEXT)";
        database.execSQL(query);
//        for (int i=0;i<25;i++){
//            database.execSQL("INSERT INTO favourites(name,phone,latitude,longitude) VALUES ('"+i+"asd','98745"+i+"','1234"+i+"','9874')");
//        }
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
    public void insertfav(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, queryValues.get("_id"));
        values.put(NAME, queryValues.get("name"));
        values.put(ADDRESS, queryValues.get("address"));
        values.put(LATITUDE, queryValues.get("latitude"));
        values.put(LONGITUDE, queryValues.get("longitude"));
        database.insert("favourites", null, values);
        database.close();
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

    public String checkDB(double lat, double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("SELECT * FROM favourites where latitude='" + lat + "'and longitude='" + lon + "'");
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