package com.application.thesis.nutricam;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.application.thesis.nutricam.Classes.NutriProduct;
import com.application.thesis.nutricam.Classes.StringFinder;
import com.application.thesis.nutricam.Classes.User;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class Scan2Activity extends AppCompatActivity {
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private String[] resultArray = null;
    private NutriProduct nutriProduct = new NutriProduct();
    private String ProcessMode = null;
    private Context context;
    User user = new User();
    CropImageView cropImageView;
    LoadCallback loadCallback;
    Uri imageUri, saveUri;
    Bitmap bitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan2);
        Intent intent = getIntent();
        context = this.getApplicationContext();
        bindViews();
        imageUri = intent.getParcelableExtra("imageuri");
        ProcessMode = intent.getStringExtra("ProcessMode");
        if(ProcessMode.equals("ingre"))
            nutriProduct = (NutriProduct) intent.getSerializableExtra("NutriProduct");
        user.setFromSharedPref(this);
        cropImageView.load(imageUri).execute(loadCallback);
        cropImageView.crop(imageUri).execute(mCropCallback);
    }

    public void cropImage() {
        cropImageView.crop(imageUri).execute(mCropCallback);
    }

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {
            cropImageView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback);
        }

        @Override public void onError(Throwable e) {
        }
    };

    public Uri createSaveUri() {
        saveUri = createNewUri(this, mCompressFormat);
        return saveUri;
    }

    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/croppedimages");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    public Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }

    private void inspect(Uri uri){
        InputStream inputStream = null;
        try{
            inputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inspectFromBitmap(bitmap);
        } catch(Exception ignored){

        } finally {
            if(bitmap != null)
                bitmap.recycle();
            if(inputStream != null){
                try{
                    inputStream.close();
                } catch (IOException ignored){

                }
            }
        }
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new android.app.AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0)
                        return diffOfTops;
                    return diffOfLefts;
                }
            });
            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }
            if(ProcessMode.equals("nutri"))
                resultArray = detectedText.toString().split("\\s+");
            else
                resultArray = detectedText.toString().split("[^\\p{L}0-9']+");
            String check = "";
            for (String s : resultArray) {
                check = check + s + "\n";
            }
            Log.v("RESULTS", check);
        } finally {
            textRecognizer.release();
        }
    }

    private void bindViews() {
        cropImageView = findViewById(R.id.cropImageView);
        findViewById(R.id.buttonDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
                inspect(saveUri);
                ArrayList<String> listAllergens = new ArrayList<>();
                StringFinder stringFinder = new StringFinder();
                stringFinder.setProcessMode(ProcessMode);
                stringFinder.setStringContent(resultArray);
                Log.v("ScanActivity", nutriProduct.toString());
                if (ProcessMode.equals("nutri") && user.getAllergies().isEmpty()) {
                    nutriProduct = stringFinder.getNutriData();
                    Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
                    intent.putExtra("NutriProduct", nutriProduct);
                    intent.putExtra("ProcessMode", "nutri");
                    startActivity(intent);
                } else if (ProcessMode.equals("nutri") && !user.getAllergies().isEmpty()) {
                    nutriProduct = stringFinder.getNutriData();
                    Intent intent = new Intent(getBaseContext(), Scan1Activity.class);
                    intent.putExtra("NutriProduct", nutriProduct);
                    intent.putExtra("ProcessMode", "ingre");
                    startActivity(intent);
                } else if (ProcessMode.equals("ingre")) {
                    Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
                    try {
                        listAllergens = stringFinder.getAllergens(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("NutriProduct", nutriProduct);
                    intent.putExtra("Ingredients", listAllergens);
                    intent.putExtra("ProcessMode", "ingre");
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.buttonFitImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
            }
        });
        findViewById(R.id.button1_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
            }
        });
        findViewById(R.id.buttonFree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
            }
        });
        findViewById(R.id.buttonRotateLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });
        findViewById(R.id.buttonRotateRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });
        findViewById(R.id.buttonCircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
            }
        });
    }
}
