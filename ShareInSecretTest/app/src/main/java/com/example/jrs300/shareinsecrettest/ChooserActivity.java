package com.example.jrs300.shareinsecrettest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class ChooserActivity extends Activity {

    private static final int ENCRYPT_CHOOSER = 1111;
    private static final int DROPBOX_CHOOSER = 2222;
    private SharedPrefs prefs;          // need to sort out getting these

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        Log.e("", "in chooser create");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    //my code starts here
    public void createNewDoc(View view){

        //create intent for Create Activity
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);

    }

    //Link to Dropbox
    public void chooseFromDropbox(View view){
        Log.e("", "in dropbox");

        DropboxSetup d = new DropboxSetup(getApplicationContext());
        DbxChooser mChooser = d.getChooser();
        mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK).
                launch(ChooserActivity.this, DROPBOX_CHOOSER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*******************************************************************************88
         * Do this if choose file from dropbox chooser
         */
        if (requestCode == DROPBOX_CHOOSER) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);

                Log.e(" ", result.getName());
                Log.e(" ", result.getLink().toString());
            }
        }
    }
       /*               try {
                           // get file input stream
                           FileInputStream fis = getFis(position);
                           FileOutputStream fos = getFos(position, "To Dropbox");
                           decryptFile(fis, fos);
                       } catch (IOException e) {
                           Log.e("decrypt file ",e.getMessage());
                       } catch (GeneralSecurityException e) {
                           Log.e("decrypt file ",e.getMessage());
                       } catch (MissingPwdException e) {
                           Log.e("decrypt file ",e.getMessage());
                       }


               } else {
                   // Failed or was cancelled by the user.
               }
           } else {
               super.onActivityResult(requestCode, resultCode, data);
           }
       } //end onActivityResult


       private FileInputStream getFis(int position) throws IOException {
           DbxPath inPath = fcFileInfo.get(position).path;
           //open a file input stream with given path
           DbxFile myCiphertextFile;
           myCiphertextFile = fcDbxFileSystem.open(inPath);
           return myCiphertextFile.getReadStream();
       }





       /**
        * Takes a fileInputStream and returns the corresponding decrypted FileOutputStream
        * @param fis
        * @param fos
        * @throws MissingPwdException
        * @throws IOException
        * @throws GeneralSecurityException
        */
    private void decryptFile(FileInputStream fis, FileOutputStream fos)
            throws MissingPwdException, IOException, GeneralSecurityException {

        FileCryptor.decryptFile(fis, fos);
    }


    public void showToast (String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}