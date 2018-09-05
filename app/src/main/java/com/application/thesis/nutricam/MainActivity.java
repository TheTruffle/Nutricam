package com.application.thesis.nutricam;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final Integer MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final String ALERTDIALOG_TITLE = "Welcome to Nutricam!";
    private static final String ALERTDIALOG_MESSAGE =
            "Good day! I see you are a new user. \n" +
                    "Would you like to fill up our new user form?";
    private boolean _firstRun;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setApplicationPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        setApplicationPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, 0);
        setApplicationPermissions(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
        setActivitySettings();
        if(_firstRun)
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(ALERTDIALOG_TITLE)
                    .setMessage(ALERTDIALOG_MESSAGE)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
    }

    private void setActivitySettings(){
        SharedPreferences settings = getSharedPreferences
                ("", MODE_PRIVATE);
        _firstRun = settings.getBoolean("MainFirst", true);
    }
    private void setApplicationPermissions(String permissionType, Integer requestCode){
        if(ContextCompat.checkSelfPermission(MainActivity.this, permissionType) != PackageManager.PERMISSION_GRANTED)
            if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionType))
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissionType}, requestCode);
    }
}
