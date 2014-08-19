package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;


public class ActivityMain extends Activity {

    private static final int REQUEST_LINK_TO_DBX = 1111;
    private static final int ENCRYPT_CHOSEN = 2222;

    private DbxAccountManager mDbxAcctMgr;
    private AppPwdObj apo;

    private TextView mTextOutput;

    private Button mButtonOK;
    private Button mButtonPwd;
    private EditText getPwd;
    private boolean linked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //stuff from original version
        apo = AppPwdObj.makeObj(this.getApplicationContext());
        mTextOutput = (TextView) findViewById(R.id.textView2);  //set up variable linked to TextView
        mButtonOK = (Button) findViewById(R.id.button_OK);
        mDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();
//        getPwd = (EditText) findViewById(R.id.text_pwd);
//        mButtonPwd = (Button) findViewById(R.id.button_pwd);

        // Hiding Alpha pane only applies on portrait
        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            hideAlphaPane();

            View omegaPane = findViewById(R.id.right);
            omegaPane.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft(){
                    hideAlphaPane();
                    super.onSwipeLeft();
                }

                public void onSwipeRight(){
                    showAlphaPane();
                    super.onSwipeRight();
                }

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideAlphaPane();
                    return super.onTouch(v, event);
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        linked = mDbxAcctMgr.hasLinkedAccount();
        if (linked) processLinked();
        else {
            mTextOutput.setText("This app needs access a dropbox account");
            mButtonOK.setText("Link to Dropbox");
        }
        try {
            apo.getValue();

        } catch (MissingPwdException e) {
            showToast("Please enter Master Password");
//            getPwd.setVisibility(View.VISIBLE);
 //           mButtonPwd.setVisibility(View.VISIBLE);
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
                intent = new Intent(this, ActivityCreate.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                //start next activity
                linked = true;
showToast("Completed link to Dropbox - now ready for next activity");
            } else {
                showToast("Link to Dropbox failed or was cancelled.");
            }
        } else if (requestCode == ENCRYPT_CHOSEN) {
            if (resultCode == Activity.RESULT_OK) {

            }
        } else super.onActivityResult(requestCode, resultCode, data);

    }
    private void processLinked(){
        if (mDbxAcctMgr.getLinkedAccount().getAccountInfo()!=null ){
            mTextOutput.setText("You are currently linked to Dropbox account\n " +
                    mDbxAcctMgr.getLinkedAccount().getAccountInfo().displayName);
        }
        mButtonOK.setText("OK");
    }

    public void onProceed(View view){
        //check which scenario we are in
        if (linked) {
            Intent intent = new Intent(this, ActivityCreate.class);
            startActivity(intent);
        } else {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
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
