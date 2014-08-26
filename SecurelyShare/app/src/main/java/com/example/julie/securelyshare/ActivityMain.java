package com.example.julie.securelyshare;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileInfo;


public class ActivityMain extends ListActivity implements Communicator {

    private static final int REQUEST_LINK_TO_DBX = 1111;
    private static final int ENCRYPT_CHOSEN = 2222;

    private DbxAccountManager mDbxAcctMgr;
    private AppPwdObj apo;
    private boolean pwdValid = false;
    private int tries = 0;

    private FragmentManager fm = getFragmentManager();
    private ActionBar actionBar;
    private DbxFileInfo selectedDbxFileInfo;
    private MyDbxFiles mDbx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apo = AppPwdObj.makeObj(this.getApplicationContext());
        mDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();
        actionBar = getActionBar();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //sort out dropbox link
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (mDbxAcctMgr.getLinkedAccount().getAccountInfo() != null)
                actionBar.setSubtitle(mDbxAcctMgr.getLinkedAccount().getAccountInfo().displayName);
        } else mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        //sort out password
        if (apo.getValue() == null) {
            // get password from the user and set in AppPwdObj
            FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
            dFragment.show(fm, "Dialog Fragment Unlock");
        }
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
        // Handle action bar item clicks here - (Home/Up handled automatically based onManifest
        View v = null; //dummy view to enable method reuse...
        switch (item.getItemId()) {
            case R.id.action_Create:
                doCreate(v);
                return true;
            case R.id.action_decrypt:
                doDecrypt(v);
                return true;
            case R.id.action_encrypt:
                doEncrypt(v);
                return true;
            case R.id.action_managegroups:
                doManageGroups(v);
                return true;
            case R.id.action_Unlink:
                doUnlink();
                return true;
            case R.id.action_addgroup:
                doAddGroup(v);
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                //put filename in intent and start encrypt activity
                Intent intent = new Intent(this, ActivityEncrypt.class);
                intent.putExtra("filePath", data.getData().getPath());
                startActivity(intent);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDialogResponse(String data) {
        if (data.length()<1) {
            pwdValid = false;
        } else pwdValid = apo.validate(data);
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
     }//end onDialogResponse

    @Override
    public void onDbxFileSelected(DbxFileInfo mDbxFileInfo) {
    }

    @Override
    public void alertDialogResponse(int title, int whichButton) {
    }


    public void doCreate(View v) {
        Intent intent = new Intent(this, ActivityCreate.class);
        startActivity(intent);
    }


    public void doEncrypt(View v) {
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("file/*");
        startActivityForResult(getContentIntent, ENCRYPT_CHOSEN);
    }

    public void doDecrypt(View v) {
        Intent intent = new Intent(this, ActivityDecrypt.class);
        startActivity(intent);
    }


    public void doManageGroups(View v) {
        Intent intent = new Intent(this, ActivityGroups.class);
        startActivity(intent);
    }
    public void doAddGroup(View v) {
        Intent intent = new Intent(this, ActivityAddGroup.class);
        startActivity(intent);
    }

    /**
     * Unlinks the current Dropbox account, deletes local data and terminates the app
     */
    private void doUnlink() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("This will unlink your dropbox account and delete all local copies of your data");
        ad.setTitle("Unlink from Dropbox");
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDbxAcctMgr.unlink();
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}

