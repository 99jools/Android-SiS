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

import java.io.File;
import java.io.FileOutputStream;

public class CreateActivity extends Activity {

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

        //encrypt text
        String cipherText = CreateActivity.encrypt(plaintextIn);

        //write encrypted text to file
        writeToFile(cipherText);

    }

    /**
     * takes a string of text, encrypts and writes to a file
     * @param cipherText
     */
    public void writeToFile(String cipherText){

        if (isExternalStorageWritable()) {

            try {

                FileOutputStream fos = openFileOutput("DayTwentyTwoFile", Context.MODE_APPEND);
                fos.write(cipherText.getBytes());
                fos.close();

                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {

                    File file = new File(getExternalFilesDir(null),
                            "DayTwentyTwoFileTwo.enc");

                    FileOutputStream fos2 = new FileOutputStream(file);

                 //   fos2.write(cipherText.getBytes());
                    
                    fos2.close();

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
}
