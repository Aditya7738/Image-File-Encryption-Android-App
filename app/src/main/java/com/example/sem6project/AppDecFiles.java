package com.example.sem6project;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppDecFiles extends AppCompatActivity {

    Button showdecfile;
    ListView decfilelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_dec_files);

        showdecfile = findViewById(R.id.showdec);
        decfilelist = findViewById(R.id.decfilelist);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        //titlebar color change
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3F51B5"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Decrypted image files");
    }


    public void displayDecFiles(View view) {
        ArrayList<String> a = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(AppDecFiles.this, R.layout.decitems, a);
        decfilelist.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference()
                .child("AppFiles")
                .child("DecryptedFiles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            a.clear();
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                Encfilenamestore i = snapshot1.getValue(Encfilenamestore.class);
                                String t = i.getFilename();
                                a.add(t);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}