package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
    public void encryptAndSave(View view) throws IOException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {


        //get the plaintext from the screen
        EditText editText = (EditText) findViewById(R.id.plaintextIn);
        String plaintextIn = editText.getText().toString();

        //get the filename
        EditText getFilename = (EditText) findViewById(R.id.text_filename);
        saveName = getFilename.getText().toString();

        //check that filename isn't empty
        if (saveName.length()< 1){
              getFilename.setError( "Please enter a name for your file" );
        }
        else {

            //add ,txt extension to filename
            saveName = saveName + ".txt";

            //check if filename already exists

            boolean fileExists =  new File(saveName).isFile();
            showToast(String.valueOf(fileExists));

            //get an output stream
            FileOutputStream fos = openFileOutput(saveName, Context.MODE_PRIVATE);

            //encrypt text with new key and write to file
            KeyManagement keyUsedToEncrypt = FileCryptor.encryptString(plaintextIn, fos);
            showToast(saveName + " saved");
            finish();
        }

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
