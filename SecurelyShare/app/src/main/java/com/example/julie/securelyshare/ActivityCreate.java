package com.example.julie.securelyshare;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.dropbox.sync.android.DbxFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ActivityCreate extends ActivityMain
        implements Communicator, AdapterView.OnItemSelectedListener {

    private String[] groups;
    private String groupID;
    private Spinner spinner;
    private AppKeystore aks;
    String plaintextIn;

    EditText fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        //populate alias array
        try {
            aks = new AppKeystore();
            groups = aks.getGroups();
        } catch (KeystoreAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, groups);
        fileName = (EditText) findViewById(R.id.fileName);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        View detailsFrame = findViewById(R.id.details);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupID = groups[position];
        showToast(groupID);
        // Check what fragment is currently shown, replace if needed.
        FragmentDetails details = (FragmentDetails) getFragmentManager()
                .findFragmentById(R.id.details);
        if (details == null) {
            // Make new fragment to input data
            details = FragmentDetails.newInstance();
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.details, details);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDialogResponse(String data) {
        plaintextIn = data;
        //validate date
        if (fileName.getText().toString().length() < 1) {
            fileName.setError("Please enter a name for your file");
        } else {
            String saveName = fileName.getText().toString() + ".txt";
            DbxFile dbxOut = null;
            try {
                dbxOut = new MyDbxFiles(this).getGroupOutFile(saveName,groupID);
                FileOutputStream fos = dbxOut.getWriteStream();
                //encrypt file and write to Dropbox
                FileCryptor.encryptString(plaintextIn, fos, groupID);
                dbxOut.close();
                showToast(saveName + " saved");
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (KeystoreAccessException e) {
                e.printStackTrace();
            }
        }
    }













/*

    private void onClickEncrypt(View view) throws KeystoreAccessException,IOException, GeneralSecurityException{
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
*/
}