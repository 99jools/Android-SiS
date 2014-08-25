package com.example.julie.securelyshare;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ActivityMain extends Activity  implements Communicator{

private static final int REQUEST_LINK_TO_DBX = 1111;
    private static final int ENCRYPT_CHOSEN = 2222;

    private DbxAccountManager mDbxAcctMgr;
    private AppPwdObj apo;
    private boolean pwdValid=false;
    private int tries = 0;

    private FragmentManager fm = getFragmentManager();
    private ActionBar  actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("ActivityMain: ", "- am in the onCreate method");
        mDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();
        actionBar = getActionBar();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //sort out dropbox link
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (mDbxAcctMgr.getLinkedAccount().getAccountInfo()!=null )
                actionBar.setSubtitle(mDbxAcctMgr.getLinkedAccount().getAccountInfo().displayName);
        }
        else mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);

        //sort out password
        if (apo.getValue()==null) {
            // get password from the user and set in AppPwdObj
            FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
            dFragment.show(fm, "Dialog Fragment Unlock");
        }


        //once dropbox connected and keystore unlocked, attach titles fragment

        FragmentTransaction fleft =fm.beginTransaction();
        TitlesFragment titles = new TitlesFragment();
        fleft.replace(R.id.left,titles);
        fleft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fleft.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.main, menu);
        actionBar = getActionBar();
        return true;
    }



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
                intent = new Intent(this, ActivityDecrypt.class);
                startActivity(intent);
                return true;

            case R.id.action_Encrypt:
                /* This encrypts an existing file from the device.  This is
                   not a main activity asit requres the file to be present in plaintext
                   - something we seek to avoid!!
                 */

                // Create the ACTION_GET_CONTENT Intent
                Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getContentIntent.setType("file/*");
                startActivityForResult(getContentIntent, ENCRYPT_CHOSEN);
                //put filename in intent and start encrypt activity

                intent = new Intent(this, ActivityEncrypt.class);
                startActivity(intent);
                return true;

            case R.id.action_addgroup:
                intent = new Intent(this, ActivityAddGroup.class);
                startActivity(intent);
                return true;

            case R.id.action_Unlink:
                doUnlink();
                return true;

            case R.id.action_listgroups:

                try {
                    new AppKeystore().listGroups();
                } catch (WrongPwdException e) {
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

    /**
     * Unlinks the current Dropbox account, deletes local data and terminates the app
     */
    private void doUnlink(){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("This will unlink your dropbox account and delete all local copies of your data");
        ad.setTitle("Unlink from Dropbox");
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDbxAcctMgr.unlink();
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
        //   actionBar.setSubtitle("");
        Intent i = new Intent(this, ActivityMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                //start next activity
                showToast("Link to Dropbox complete");
            } else {
                showToast("Link to Dropbox failed or was cancelled by user.");
            }
        } else if (requestCode == ENCRYPT_CHOSEN) {
            if (resultCode == Activity.RESULT_OK) {
//TODO need to sort out what happens when user selects file to be

            }
        } else super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void alertDialogResponse(int title, int whichButton) {
    }


//////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onDialogResponse(String data) {
        Log.e("at start of dialog response", pwdValid+"");
        pwdValid = apo.validate(data);
        Log.e("tries", " " + tries);
        //now check that this pwd provides access to the store

        if (!pwdValid) {
            if (tries < 2) showToast("The password entered is invalid - please retry");
            else {
                if (tries == 2)
                    showToast("Password invalid - ONE MORE FAILURE WILL RESULT IN KEYSTORE BEING WIPED");
                else {
                    showToast("Keystore wiped");
                    finish();
                }

            }
            tries++;
            FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
            dFragment.show(fm, "Dialog Fragment Unlock");
        }
    }









    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}

