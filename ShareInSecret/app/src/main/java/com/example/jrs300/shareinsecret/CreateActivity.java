package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

    public void writeToFile(String cipherText){
        String myFilename = "myfile";
        FileOutputStream outputStream;
        File outFile = new File(this.getFilesDir(), myFilename);

       try {
            outputStream = openFileOutput(myFilename, Context.MODE_PRIVATE);
            outputStream.write(cipherText.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
Log.e("MyMessAGE", "I have written to a file");

    }

    public static String encrypt (String text){
        return text.toUpperCase();
    }
}
