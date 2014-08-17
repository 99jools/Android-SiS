package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;


public class MainActivity extends Activity {

    private static final int REQUEST_LINK_TO_DBX = 1111;
    private static final int ENCRYPT_CHOSEN = 2222;

    private DbxAccountManager mDbxAcctMgr;
    private AppPwdObj apo;

    private TextView mTextOutput;
    private Button mButtonUnlink;
    private Button mButtonOK;
    private Button mButtonPwd;
    private EditText getPwd;
    private boolean linked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_layout);

//************************************************************************************
        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT){

        // Code to run whilst device is portrait goes here
            hideAlphaPane();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /****************NOT IMPLEMENTED YET*********************************
     *
     * @param item
     * @return

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here - (Home/Up handled automatically based onManifest The action bar will
        Intent intent;

        switch (item.getItemId()){
            case R.id.action_Create:
                intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_Open:
                intent = new Intent(this, DecryptActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_Encrypt:
                /* This encrypts an existing file from the device.  This is
                   not a main activity asit requres the file to be present in plaintext
                   - something we seek to avoid!!
                 */

/*                // Create the ACTION_GET_CONTENT Intent
                Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getContentIntent.setType("file/*");
                startActivityForResult(getContentIntent, ENCRYPT_CHOOSER);
                //put filename in intent and start encrypt activity

                intent = new Intent(this, EncryptActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_addgroup:
                intent = new Intent(this, AddGroupActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_Unlink:
                doUnlink();
                return true;

            case R.id.action_listgroups:

                try {
                    com.example.jrs300.shareinsecrettest.AppKeystore.listGroups();
                } catch (MissingPwdException e) {
                    Log.e("listgroups", e.getMessage());
                } catch (GeneralSecurityException e) {
                    Log.e("listgroups", e.getMessage());
                } catch (IOException e) {
                    Log.e("listgroups", e.getMessage());
                }

                return true;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);


    }
                */
    /**
     * Unlinks the current Dropbox account, deletes local data and terminates the app
     */
    private void doUnlink(){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("This will unlink your dropbox account, delete all local data and terminate the app");
        ad.setTitle("Unlink from Dropbox");
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDbxAcctMgr.unlink();
                finish();
            }
        });
        ad.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recreate();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = ad.create();
        // show it
        alertDialog.show();
    }






    /** * Method to hide the Alpha pane */
    private void hideAlphaPane() {
        View alphaPane = findViewById(R.id.left);
        if (alphaPane.getVisibility() == View.VISIBLE) {
            alphaPane.setVisibility(View.GONE);
        }
    }

    /** * Method to show the Alpha pane */
    private void showAlphaPane() {
        View alphaPane = findViewById(R.id.left);
        if (alphaPane.getVisibility() == View.GONE) {
            alphaPane.setVisibility(View.VISIBLE);
        }
    }







}
