package com.application.thesis.nutricam;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.thesis.nutricam.Classes.NutriProduct;
import com.application.thesis.nutricam.Classes.StringFinder;
import com.application.thesis.nutricam.Classes.User;

public class Scan1Activity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 0;
    private Uri imageUri;
    private TextView title, instruct;
    private String processMode;
    private NutriProduct nutriProduct;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan1);
        String processType = getIntent().getStringExtra("ProcessMode");
        user.setFromSharedPref(this);
        initializeFields();
        if(user.getAllergies().isEmpty())
            new AlertDialog.Builder(this)
                    .setMessage("You indicated no allergens in our information sheet. Therefore allergen detection and ingredients scanning in products will be skipped.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        switch (processType){
            case "nutri":
                processMode = "nutri";
                title.setText(getResources().getString(R.string.scan1activity_titlenutri));
                instruct.setText(getResources().getString(R.string.scan1activity_instnutri));
                break;
            case "ingre":
                Intent intent = getIntent();
                nutriProduct = (NutriProduct) intent.getSerializableExtra("NutriProduct");
                processMode = "ingre";
                title.setText(getResources().getString(R.string.scan1activity_titleingre));
                instruct.setText(getResources().getString(R.string.scan1activity_instingre));
                break;
            default: break;
        }

    }

    private void initializeFields(){
        title = findViewById(R.id.tvscan1_title);
        instruct = findViewById(R.id.tvscan1_instructions);
        findViewById(R.id.btnscan1_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename =  System.currentTimeMillis() + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
                try{
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btnscan1_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CAMERA:
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent (getBaseContext(), Scan2Activity.class);
                    if(processMode.equals("ingre"))
                        intent.putExtra("NutriProduct", nutriProduct);
                    intent.putExtra("imageuri", data.getData());
                    intent.putExtra("ProcessMode", processMode);
                    startActivity(intent);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
