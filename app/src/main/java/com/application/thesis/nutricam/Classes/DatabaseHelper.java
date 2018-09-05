package com.application.thesis.nutricam.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.application.thesis.nutricam.BuildConfig;
import com.application.thesis.nutricam.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "allergy.sqlite";
    @SuppressLint("SdCardPath")
    private final String DATABASE_PATH =
            "/data/data/com.application.thesis.nutricam/databases/";
    private SQLiteDatabase sqLiteDatabase;
    private Context appContext;

    public DatabaseHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        appContext = context;
        boolean dbExist = checkDatabase();
        if(dbExist)
            getDatabase();
        else
            createDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<String> getAllMainCategory(){
        List<String> categoryList = new ArrayList<>();
        String query = "SELECT category_name FROM category";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                categoryList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    private void createDatabase() throws IOException{
        boolean dbExist = checkDatabase();
        if(!dbExist){
            this.getReadableDatabase();
            getDatabasefromAssets();
        }
    }

    private boolean checkDatabase(){
        String databasePath = DATABASE_PATH + DATABASE_NAME;
        File databaseFile = new File(databasePath);
        return databaseFile.exists();
    }

    private void getDatabasefromAssets() throws IOException {
        InputStream inputStream = appContext.getAssets().open(DATABASE_NAME);
        OutputStream outputStream = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void getDatabase() throws SQLException{
        String databasePath = DATABASE_PATH + DATABASE_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close(){
        if(sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }
}
