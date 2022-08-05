package com.example.sem6project;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sem6project.utils.CryptoUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Showfiles extends AppCompatActivity {

    TextView filepath;
    String filename;
    EditText pkey;
    String s1;
    File file;
    boolean process_clicked = false;
    String password;
    boolean enc_success = false;
    boolean fileremoved = false;
    String fileId;

    //constructor's name should be same as class name
    public Showfiles(){
        //this constructor is required
    }

    public Showfiles(String fileId){
        this.fileId = fileId;
    }


    public String getFileId(){
        return fileId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfiles);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        filepath = (TextView) findViewById(R.id.filepath);
        pkey = (EditText) findViewById(R.id.key);

        password = pkey.getText().toString();

        //titlebar color change
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3F51B5"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Encryption");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b1 = getIntent().getExtras();

        if (b1 != null) {

            s1 = b1.getString("filepath");

            filename = b1.getString("filename");

            filepath.setText(s1);
        }
    }

    public void encryptFile(View view) throws IOException, NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        if (TextUtils.isEmpty(pkey.getText().toString())) {
            Toast.makeText(this, "Please set a password to your file", Toast.LENGTH_LONG).show();
        } else {
            process_clicked = true;
            Log.i("Showfiles", "executed 8");
            InputStream inputStream = new ByteArrayInputStream(getIntent().getByteArrayExtra("BitmapImage"));

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            File outputFileEnc = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Image File Encrypto/");

            outputFileEnc.mkdirs();

            file = new File(outputFileEnc, filename + ".enc");

            if (file.exists()) {
                Toast.makeText(this, "Encrypted file is already exists", Toast.LENGTH_SHORT).show();
            } else {
                file.createNewFile();
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);

                     //check here for key

                    //original key now can be discarded
                    HashMap map = CryptoUtils.encrypt(bytes, pkey.getText().toString());

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                    objectOutputStream.writeObject(map);

                    objectOutputStream.close();

                    outputStream.close();


                    Toast.makeText(this, "Image file encrypted successfully", Toast.LENGTH_LONG).show();

                    enc_success = true;

                    if (enc_success) {
                        String encfilename = filename + ".enc";

                        Encfilenamestore obj = new Encfilenamestore(encfilename);

                        //to store in firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference myRef = database.getReference("AppFiles");

                        DatabaseReference encFileRef = myRef.child("EncryptedFiles");

                        //generate unique id
                        DatabaseReference newRef = encFileRef.push();

                        fileId = newRef.getKey();
                        //get filename from obj getter method
                        //it call obj's getter method and insert value in database
                        newRef.setValue(obj);

                    }
                    String valueBase64String = Base64.encodeToString((byte[])map.get("encryptedFile"), Base64.NO_WRAP);

                    //getting decoder
                    //decoding string

                    String saltBase64String = Base64.encodeToString((byte[])map.get("salt"), Base64.NO_WRAP);
                    String ivBase64String = Base64.encodeToString((byte[])map.get("iv"), Base64.NO_WRAP);
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("l", valueBase64String);

                    editor.putString("lsalt", saltBase64String);

                    editor.putString("liv", ivBase64String);

                    editor.commit(); //or editor.apply();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteFileId(String id){
        FirebaseDatabase.getInstance().getReference()
                .child("AppFiles")
                .child("EncryptedFiles")
                .child(id)
                .removeValue();
        return true;
    }

    public void navEncTab (View view){
        if (process_clicked) {
            Log.i("Showfiles", "executed 22");
            if (file.exists()) {
                Log.i("Showfiles", "executed 23");
                file.delete();
                Log.i("Showfiles", "executed 24");
                Toast.makeText(this, "Encryption process is cancelled", Toast.LENGTH_LONG).show();

                deleteFileId(fileId);
            }
        } else {
            Log.i("Showfiles", "executed 25");
            Toast.makeText(this, "You have not encrypt file yet", Toast.LENGTH_LONG).show();
        }
    }
}
