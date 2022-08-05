package com.example.sem6project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity{

    TabLayout tl;
    TabItem ti1, ti2;
    ViewPager vp;
    PageAdapter pa;

    boolean enc_clicked = false;
    boolean dec_clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define Actionbar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        tl = findViewById(R.id.tablay);
        ti1 = findViewById(R.id.taben);
        ti2 = findViewById(R.id.tabde);

        vp = findViewById(R.id.viewpage1);

        pa = new PageAdapter(getSupportFragmentManager(), tl.getTabCount());

        vp.setAdapter(pa);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        }

        //titlebar color change
        //define colorDrawable object and parseColor using parseColor method
        //with color hash code as its parameter
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3F51B5"));
        actionBar.setBackgroundDrawable(colorDrawable);

        tl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                vp.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0 || tab.getPosition() == 1){

                    pa.notifyDataSetChanged();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("MainActivity","executed 77");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("MainActivity","executed 78");
            }
        });

        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));

        //listen to tab change by click and swipe
        //We recommend using a newer Android Gradle plugin to use compileSdk = 32
        //
        //This Android Gradle plugin (7.0.4) was tested up to compileSdk = 31

    }


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.EncFiles:
                Intent intent = new Intent(MainActivity.this, AppEncfiles.class);
                startActivity(intent);
                break;
            case R.id.DecFiles:
                intent = new Intent(MainActivity.this, AppDecFiles.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void selectFilesForEn(View view) {
        enc_clicked = true;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    intent.setType("image/*");

                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                            "image/jpg",
                            "image/jpeg",  //ODT files like Word,ZIP, XML
                            "image/png"
                    });

                    startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (enc_clicked) {

                Intent i = new Intent(this, Showfiles.class);

                Uri uri = null;

                Bitmap pic = null;

                if (resultData != null) {

                    uri = resultData.getData();

                    String path = uri.getPath();

                    //get filename
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    cursor.moveToFirst();

                    String filename = cursor.getString(nameIndex); //till here file have extension

                    int pos = filename.lastIndexOf(".");

                        if (pos > 0) {

                            filename = filename.substring(0, pos); //filename exclude extension

                        }

                        pic = null;

                        try {

                            //convert uri to stream
                            InputStream pictureInputStream = getContentResolver().openInputStream(uri);

                            pic = BitmapFactory.decodeStream(pictureInputStream); //bitmap

                            //convert Bitmap to byteArray
                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                            //in decryption error start from here, watch that video
                            pic.compress(Bitmap.CompressFormat.PNG, 100, bStream);

                            byte[] byteArray = bStream.toByteArray();

                            i.putExtra("BitmapImage", byteArray);

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                        Bundle bundle = new Bundle();

                        bundle.putString("filepath", path);

                        bundle.putString("filename", filename);

                        i.putExtras(bundle);

                        startActivity(i); //bundle has limit of 1MB

                    }
                }
            else if (dec_clicked) {

                Intent i2 = new Intent(this, showfiles2.class);

                Uri uri = null; //to select multiple documents - getClipData() see ss

                if (resultData != null) {

                    uri = resultData.getData();
                    Uri selectedEncfile = resultData.getData();
                    i2.putExtra("encfileuri", selectedEncfile.toString());

                    String path = uri.getPath();

                    //get filename
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    cursor.moveToFirst();

                    String filename = cursor.getString(nameIndex); //till here file have extension

                    int pos = filename.lastIndexOf(".");

                    if (pos > 0) {

                        filename = filename.substring(0, pos);
                    }

                    Bundle bundle = new Bundle();

                    bundle.putString("filepath", path);

                    bundle.putString("filename", filename);

                    i2.putExtras(bundle);

                    startActivity(i2);

                }
            }

                super.onActivityResult(requestCode, resultCode, resultData);

        }
    }



    public void selectFilesForDe(View view) {
        dec_clicked = true;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("*/*");

        startActivityForResult(intent, 100);

    }
}