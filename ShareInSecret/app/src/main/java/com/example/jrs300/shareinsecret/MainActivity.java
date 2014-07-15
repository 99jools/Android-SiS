package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    private static final int REQUEST_CHOOSER = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //create intent fo open next activity
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);


    }

    /**run when user clicks button to open existing document */
    public void openExisting(View view){

        //browse files using library project aFileChooser

        File myDirectory = this.getExternalFilesDir(null);
        Uri uriForMyDirectory = Uri.fromFile(myDirectory);

        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent.setData(uriForMyDirectory);
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");

        startActivityForResult(intent, REQUEST_CHOOSER);
          }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {

        //decrypt file to external storage -THIS IS A TEMPORARY SOLUTION FOR TESTING

        //sort out file path
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


        finish();
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}
