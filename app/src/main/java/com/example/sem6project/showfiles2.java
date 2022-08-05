package com.example.sem6project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sem6project.utils.CryptoUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

public class showfiles2 extends AppCompatActivity {

    TextView filepath;
    String filename;
    EditText pkey;
    String s1;
    Uri fileuri;
    String password;
    File file;
    String fileId;
    boolean process_clicked = false;
    boolean dec_success = false;
    private final static String ALGO_SECRET_KEY = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfiles2);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        filepath = (TextView) findViewById(R.id.filepath);
        pkey = (EditText) findViewById(R.id.key);

        //titlebar color change
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3F51B5"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Decryption");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b1 = getIntent().getExtras();

        if (b1 != null) {

            s1 = b1.getString("filepath");

            filename = b1.getString("filename");
            filepath.setText(s1);
        }
        Intent intent = getIntent();

        String image_path = intent.getStringExtra("encfileuri");

        fileuri = Uri.parse(image_path);

    }

    public void decryptFile(View view) throws IOException, GeneralSecurityException {
        if (TextUtils.isEmpty(pkey.getText().toString())) {
            Toast.makeText(this, "Please set a password to your file", Toast.LENGTH_LONG).show();
        } else {
            process_clicked = true;

            InputStream inputStream = getContentResolver().openInputStream(fileuri);

            File outputFileEnc = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Image File Encrypto/");

            outputFileEnc.mkdirs();

            file = new File(outputFileEnc, filename + ".jpeg");

            if (file.exists()) {
                Toast.makeText(this, "Decrypted file is already exists", Toast.LENGTH_SHORT).show();
            } else {

                file.createNewFile();

                try {
                FileOutputStream outputStream = new FileOutputStream(file);

                password = pkey.getText().toString();

                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                    String base64EncFile = sharedPreferences.getString("l","");
                    String base64Salt = sharedPreferences.getString("lsalt", "");
                    String base64IV = sharedPreferences.getString("liv", "");
                    byte[] encFile = Base64.decode(base64EncFile, Base64.NO_CLOSE);
                    byte[] iv = Base64.decode(base64IV, Base64.NO_WRAP);

                    byte[] salt = Base64.decode(base64Salt, Base64.NO_WRAP);

                    HashMap<String, byte[]> hm = new HashMap<String, byte[]>();

                    hm.put("iv", iv);

                    hm.put("salt", salt);

                    hm.put("encFile", encFile);

                    byte[] decrypted = CryptoUtils.decrypt(hm, pkey.getText().toString());

                    outputStream.write(decrypted);

                    outputStream.flush();

                    outputStream.close();

                    Toast.makeText(this, "Image file decrypted successfully", Toast.LENGTH_LONG).show();

                    dec_success = true;

                    if (dec_success){
                        String decfilename = filename + ".jpeg";

                        Encfilenamestore obj = new Encfilenamestore(decfilename);

                        //to store in firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference myRef = database.getReference("AppFiles");

                        DatabaseReference decFileRef = myRef.child("DecryptedFiles");

                        //generate unique id
                        DatabaseReference newRef = decFileRef.push();

                        fileId = newRef.getKey();

                        //get filename from obj getter method
                        //it call obj's getter method and insert value in database
                        newRef.setValue(obj);
                    }

                } catch (NoSuchPaddingException e) {

                    e.printStackTrace();
                } catch (InvalidKeyException e) {

                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {

                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    public boolean deleteFileId(String id){

        FirebaseDatabase.getInstance().getReference()
                .child("AppFiles")
                .child("DecryptedFiles")
                .child(id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        return true;

    }

    public void navDecTab(View view) throws IOException {
        if (process_clicked) {
            if (file.exists()) {

                file.delete();

                Toast.makeText(this, "Decryption process is cancelled", Toast.LENGTH_LONG).show();

                deleteFileId(fileId);
            }
        }else{
            Toast.makeText(this, "You have not decrypt file yet", Toast.LENGTH_LONG).show();
        }
    }
}