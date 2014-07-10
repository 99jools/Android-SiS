package com.example.jrs300.filecrypt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final int REQUEST_CHOOSER = 1234;
    private static final int FILE_SELECT_CODE = 999;

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
    public void doEncryptFile(View view) {
        showFileChooser();
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }




    /**run when user clicks button to encrypt file */
    public void doDecryptFile(View view){
/*
        //browse files using library project aFileChooser

        File myDirectory = getExternalFilesDir(null);
     //   Uri uriForMyDirectory = Uri.fromFile(myDirectory);

        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();
     //   getContentIntent.setData(uriForMyDirectory);
        Intent intent = Intent.createChooser(getContentIntent, "Select file to encrypt");

        startActivityForResult(intent, REQUEST_CHOOSER);
*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //do something
        Log.e("encrypt file", data.getData().toString());
    }





}
