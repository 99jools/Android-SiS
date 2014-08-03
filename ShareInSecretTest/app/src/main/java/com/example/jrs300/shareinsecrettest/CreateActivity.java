package com.example.jrs300.shareinsecrettest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CreateActivity extends Activity {

    private String plaintextIn;
    private String saveName;
    private DbxAccountManager mDbxAcctMgr;
    private SharedPrefs prefs;  //NOTE: need to get these parameters from somewhere
    private String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//      Log.e("method call", "CreateActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        this.mDbxAcctMgr = new DropboxSync(getApplicationContext()).getAccMgr();
    }

    @Override
    protected void onResume() {

        //THIS IS JUST HERE FOR TESTING PURPOSES - REMOVE
        Log.e("method call", "CreateActivity - onResume");
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
    public void encryptAndSave(View view) throws IOException, GeneralSecurityException{
        //get the plaintext from the screen
        EditText editText = (EditText) findViewById(R.id.plaintextIn);
        this.plaintextIn = editText.getText().toString();

        //get the filename
        EditText getFilename = (EditText) findViewById(R.id.text_filename);
        this.saveName = getFilename.getText().toString();

        //check that filename isn't empty
        if (this.saveName.length()< 1){
              getFilename.setError( "Please enter a name for your file" );
        }
        else {

            //add .enc extension to filename
            this.saveName = this.saveName + ".enc";

            FileOutputStream fos = getDbxOutputStream();

            //encrypt text with new key and write to file
            FileCryptor.encryptString(plaintextIn, fos, groupID ,prefs);
            showToast(saveName + " saved");
            finish();
        }

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    private FileOutputStream getDbxOutputStream() throws DbxException, IOException{

        DbxPath savePath = new DbxPath(DbxPath.ROOT, this.saveName);
        FileOutputStream newFos=null;

        // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
        DbxFileSystem dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());
        if ( dbxFileSys.hasSynced() ==  false)
            dbxFileSys.awaitFirstSync();


        // Create a test file only if it doesn't already exist.
        if (!dbxFileSys.exists(savePath)) {
            DbxFile newFile = dbxFileSys.create(savePath);
            newFos = newFile.getWriteStream();

        }
        return newFos;
    }
}