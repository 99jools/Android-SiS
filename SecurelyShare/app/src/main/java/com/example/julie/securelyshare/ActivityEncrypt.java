package com.example.julie.securelyshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ActivityEncrypt extends ActivityMain {
    private static final int ENCRYPT_CHOOSER = 1111;
    private File inFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null)
        {
            //fill in the details on screen, and wait for the user to click the encrypt button
            TextView getFilename = (TextView) findViewById(R.id.text_filename);
            String path = b.getString("filePath");
            getFilename.setText(path);
            inFile = new File(path);
        }
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
/*
    public void onClickChoose(View view){
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("file/*");
        startActivityForResult(getContentIntent, ENCRYPT_CHOOSER);
    }
*/
    public void onClickOK(View view) throws KeystoreAccessException, IOException, GeneralSecurityException {

        //get the groupID
        EditText getGroup = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroup.getText().toString().trim();

        //check that groupID isn't empty
        if (groupID.length() < 1) {
            getGroup.setError("Please enter a group name for this file");
        } else {
            FileInputStream fis = new FileInputStream(inFile);
            DbxFile dbxOut = new MyDbxFiles(this).getOutFile(inFile);
            FileOutputStream fos = dbxOut.getWriteStream();

            //encrypt file and write to Dropbox
            FileCryptor.encryptFile(fis,fos,groupID);
            dbxOut.close();
           finish();
        }

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}/*    /*
     * @return returns a DbxFile initialised correctly for writing to Dropbox

    private DbxFile getDbxOutputFile() throws IOException {

        DbxPath dir = new DbxPath("/ShareInSecret");
        DbxPath savePath = new DbxPath(dir, this.saveName);

        // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
        DbxFileSystem dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());
        if (!dbxFileSys.hasSynced())
            dbxFileSys.awaitFirstSync();

        // Create file only if it doesn't already exist.
        if (!dbxFileSys.exists(savePath)) {
            return dbxFileSys.create(savePath);
        } else return dbxFileSys.open(savePath);

    }*/