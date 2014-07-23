package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CreateActivity extends Activity {

    private String plaintextIn;
    private String saveName;
    private DbxAccountManager mDbxAcctMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        this.mDbxAcctMgr = new GetDbxAcctMgr(getApplicationContext()).getmDbxAcctMgr();
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
/* comment this out temporarily
            //get an output stream
            File myFile = new File(this.getExternalFilesDir(null),saveName);
            FileOutputStream fos = new FileOutputStream(myFile);


            //encrypt text with new key and write to file
            KeyManagement keyUsedToEncrypt = FileCryptor.encryptString(plaintextIn, fos);
            showToast(saveName + " saved");
            Log.e("CreateActivity ", myFile.getAbsolutePath());
*/


            finish();
        }

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    private FileOutputStream getDbxOutputStream() {

        try {
            DbxPath savePath = new DbxPath(DbxPath.ROOT, this.saveName);

            // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
            DbxFileSystem dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());
            if ( dbxFileSys.hasSynced() ==  false)
                dbxFileSys.awaitFirstSync();


         // Create a test file only if it doesn't already exist.
            if (!dbxFileSys.exists(savePath)) {
                DbxFile newFile = dbxFileSys.create(savePath);
                FileOutputStream newFos = newFile.getWriteStream();
         /*       try {
                    newFile.writeString(this.plaintextIn);
                    newFile.
                } finally {
                    newFile.close();
                }
*/
                return newFos;
            }


        } catch (IOException e) {
            showToast("Dropbox test failed: " + e);
        }
    }}