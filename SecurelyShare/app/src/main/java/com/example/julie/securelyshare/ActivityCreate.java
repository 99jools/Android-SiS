package com.example.julie.securelyshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.sync.android.DbxFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ActivityCreate extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//      Log.e("method call", "CreateActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    public void onClickEncryptSave(View view) throws WrongPwdException,IOException, GeneralSecurityException{
        //get the plaintext from the screen
        EditText editText = (EditText) findViewById(R.id.plaintextIn);
        String plaintextIn = editText.getText().toString();

        //get the groupID
        EditText getGroup = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroup.getText().toString().trim();
        //check that filename isn't empty
        if (groupID.length()< 1){
            getGroup.setError( "Please enter a group name for this file" );
        }

        //get the filename
        EditText getFilename = (EditText) findViewById(R.id.text_filename);



        //check that filename isn't empty
        if (getFilename.getText().toString().length()< 1){
              getFilename.setError( "Please enter a name for your file" );
        }
        else {

            String saveName = getFilename.getText().toString() + ".txt";
            DbxFile dbxOut = new MyDbxFiles(this).getOutFile(saveName);
            FileOutputStream fos = dbxOut.getWriteStream();

            //encrypt file and write to Dropbox
            FileCryptor.encryptString(plaintextIn, fos, groupID);
            dbxOut.close();
            showToast(saveName + " saved");
            finish();
        }

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}