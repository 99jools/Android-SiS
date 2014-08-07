package com.example.jrs300.shareinsecrettest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptActivity extends Activity {
    private static final int ENCRYPT_CHOOSER = 1111;
    private File inFile;
    private String saveName;
    private DbxAccountManager mDbxAcctMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        this.mDbxAcctMgr = new DropboxSetup(getApplicationContext()).getAccMgr();
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

    public void onClickChoose(View view){
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("file/*");
        startActivityForResult(getContentIntent, ENCRYPT_CHOOSER);
    }

    public void onClickOK(View view) throws MissingPwdException, IOException, GeneralSecurityException {

        //get the groupID
        EditText getGroup = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroup.getText().toString().trim();

        //check that groupID isn't empty
        if (groupID.length() < 1) {
            getGroup.setError("Please enter a group name for this file");
        } else {

            //add .enc extension to filename
            this.saveName = this.inFile.getName() + ".xps";

            FileInputStream fis = new FileInputStream(inFile);

            // get a FileOutputStream set up for writing to Dropbox
            DbxFile dbxCiphertext = getDbxOutputFile();
            FileOutputStream fos = dbxCiphertext.getWriteStream();

            //encrypt file and write to Dropbox
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPrefs prefs = new SharedPrefs(sp);
            FileCryptor.encryptFile(fis,fos,groupID,prefs);
            showToast(saveName + " saved");
            dbxCiphertext.close();
 //           finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENCRYPT_CHOOSER) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("result ", "file is " + data.getData().getPath());
                //fill in the details on screen, and wait for the user to click the encrypt button
                //get the filename
                TextView getFilename = (TextView) findViewById(R.id.text_filename);
                String path = (data.getData().getPath());
                getFilename.setText(path);
                inFile = new File(path);


            }
        }
    }


    /*
     * @return returns a DbxFile initialised correctly for writing to Dropbox
     */
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

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}