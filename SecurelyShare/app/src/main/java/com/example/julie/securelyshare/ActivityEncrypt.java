package com.example.julie.securelyshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ActivityEncrypt extends ActivityMain implements AdapterView.OnItemSelectedListener {
    private static final int ENCRYPT_CHOOSER = 1111;
    private File inFile;
    private String[] groups;
    private String groupID;
    private Spinner spinner;
    private AppKeystore aks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            TextView getFilename = (TextView) findViewById(R.id.text_filename);
            String path = b.getString("filePath");
            getFilename.setText(path);
            inFile = new File(path);
        }

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
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupID = groups[position];
        showToast(groupID);
    }

    public void onClickOK(View view) throws KeystoreAccessException, IOException, GeneralSecurityException {
        FileInputStream fis = new FileInputStream(inFile);
        DbxFile dbxOut = new MyDbxFiles(this).getGroupOutFile(inFile.getName(), groupID);
        FileOutputStream fos = dbxOut.getWriteStream();

        //encrypt file and write to Dropbox
        FileCryptor.encryptFile(fis, fos, groupID);
        dbxOut.close();
        showToast("File " + inFile +" saved for group " + groupID);
        finish();
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}