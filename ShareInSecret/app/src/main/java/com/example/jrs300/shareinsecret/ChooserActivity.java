package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ChooserActivity extends Activity {

    private static final int REQUEST_CHOOSER = 1111;
    private static final int DROPBOX_CHOOSER = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
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
    public void useDropbox(View view){

        Intent intent = new Intent(this, FileChooserActivity.class);
        startActivity(intent);

    }

    /**run when user clicks button to open existing document */
    public void openExisting(View view){

        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("file/*");
        startActivityForResult(getContentIntent, REQUEST_CHOOSER);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DROPBOX_CHOOSER) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                Log.e("main", "Link to selected file: " + result.getName());

                //decryption stuff - TEMPORARY !!
                String in = result.getLink().getPath();
                String out = result.getName();
                out = out.substring(0, out.length()-4) + ".dec.txt";

                try {
                    //get the correct key
                    KeyManagement decryptKey = new KeyManagement(in);



                    //get an output stream
                    File myCiphertextFile = new File(in);
                    File myPlaintextFile = new File(this.getExternalFilesDir(null),out);

                    FileInputStream fis = new FileInputStream(myCiphertextFile);
                    FileOutputStream fos = new FileOutputStream((myPlaintextFile));

                    FileCryptor.decryptFile(fis, fos, decryptKey);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Handle the result
            } else {
                // Failed or was cancelled by the user.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




 /*     decryption stuff - temporaroly removed
        String in = data.getData().getPath();
        String out = in.substring(in.lastIndexOf('/') + 1);
        out = out.substring(0, out.length()-4) + ".dec.txt";



        //get an output stream
        File myCiphertextFile = new File(in);
        File myPlaintextFile = new File(this.getExternalFilesDir(null),out);

        Log.e("in ", myCiphertextFile.getAbsolutePath());
        Log.e("out ", myPlaintextFile.getAbsolutePath());


        try {
            //get the correct key
            KeyManagement decryptKey = new KeyManagement(in);

            FileInputStream fis = new FileInputStream(myCiphertextFile);
             FileOutputStream fos = new FileOutputStream((myPlaintextFile));

            FileCryptor.decryptFile(fis, fos, decryptKey);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/


    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}
