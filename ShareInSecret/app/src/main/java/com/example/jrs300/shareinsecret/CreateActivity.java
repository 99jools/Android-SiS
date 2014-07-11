package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CreateActivity extends Activity {

    private String plaintextIn;
    private String saveName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * called whenever the user clicks the Encrypt and Save button
     */
    public void encryptAndSave(View view) {


        //get the plaintext from the screen
        EditText editText = (EditText) findViewById(R.id.plaintextIn);
        String plaintextIn = editText.getText().toString();

        //get the filename
        EditText getFilename = (EditText) findViewById(R.id.text_filename);
        saveName = getFilename.getText().toString();


        try {
            //get a new FileOutputStream
            FileOutputStream fos = getNewFos();

            //encrypt text with new key and write to file

            KeyManagement keyUsedToEncrypt = FileCryptor.encryptString(plaintextIn, fos);
            showToast("File saved");
            finish();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }



    }

    /**
     * takes a string of ciphertext and writes to a file
     * @param cipherText
     */
    public FileOutputStream getNewFos(){

        if (isExternalStorageWritable()) {

            try {

                FileOutputStream fos = openFileOutput(saveName, Context.MODE_APPEND);

                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {

                    FileOutputStream fos2 = new FileOutputStream(new File(getExternalFilesDir(null),
                            saveName));

                }

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
        else Log.e("writefile", "external storage is not writeable");

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String encrypt (String text)
    {
        return text.toUpperCase();
    }

    public void showToast(String message)

    {

        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);

        toast.show();

    }
}
