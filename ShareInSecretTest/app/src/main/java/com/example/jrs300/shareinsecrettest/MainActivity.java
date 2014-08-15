package com.example.jrs300.shareinsecrettest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends Activity {

    private static final int REQUEST_LINK_TO_DBX = 0;
    private static final int ENCRYPT_CHOOSER = 1111;
    private DbxAccountManager mDbxAcctMgr;
 //   private DbxAccount mDbxAcct;
    private PopupWindow pwindow;
    private TextView mTextOutput;
    private Button mButtonUnlink;
    private Button mButtonOK;     // make this the button to trigger the popup
    private Button mButtonPwd;
    private EditText getPwd;
    private boolean linked;
    private AppPwdObj apo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apo = AppPwdObj.makeObj(this.getApplicationContext());
        mTextOutput = (TextView) findViewById(R.id.textView2);  //set up variable linked to TextView
        mButtonUnlink = (Button) findViewById(R.id.button_unlink);

        mDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();

        mButtonPwd = (Button) findViewById(R.id.button_pwd);



        mButtonOK = (Button) findViewById(R.id.button_OK);
        mButtonOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getPopupWindow();
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
        linked = mDbxAcctMgr.hasLinkedAccount();
        if (linked) processLinked();
        else processUnlinked();
        try {
            apo.getValue();

        } catch (MissingPwdException e) {
            getPopupWindow();
            showToast("Please enter Master Password");
            getPwd.setVisibility(View.VISIBLE);
            mButtonPwd.setVisibility(View.VISIBLE);
        }

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
                //sort out what file we are trying to encrypt

                // Create the ACTION_GET_CONTENT Intent
                Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getContentIntent.setType("file/*");
                startActivityForResult(getContentIntent, ENCRYPT_CHOOSER);
                //put filename in intent and start encrypt activity

                intent = new Intent(this, EncryptActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_addgroup:
                intent = new Intent(this, ImportGroupActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_Unlink:
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
                    public void onClick(DialogInterface dialogInterface, int i) {recreate();}
                });
                // create alert dialog

                AlertDialog alertDialog = ad.create();
                alertDialog.show();
                return true;

          case R.id.action_listgroups:

                try {
                    AppKeystore.listGroups(new AppKeystore());
                } catch (MissingPwdException e) {
                    Log.e("listgroups",e.getMessage());
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

    public void onProceed(View view){
        //check which scenario we are in
        if (linked) {
            //create intent for Chooser activity
            Intent intent = new Intent(this, ChooserActivity.class);
            startActivity(intent);
        } else {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }


    public void onClickPwd(View view){

        String appPwd = getPwd.getText().toString();
        Boolean confirm = null;
        try {
            confirm =  apo.setValue(appPwd);
            showToast("Master password accepted");
            getPwd.setVisibility(View.GONE);
            mButtonPwd.setVisibility(View.GONE);
            pwindow.dismiss();

        } catch (IOException e) {
            showToast("Error entering Master password - please retry");
        }
        getPwd.setText(null);

    } //end onClickPwd

    public void onClickUnlink(View view){
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

    public void processLinked(){
        if (mDbxAcctMgr.getLinkedAccount().getAccountInfo()!=null ){
            mTextOutput.setText("You are currently linked to Dropbox account\n " +
                    mDbxAcctMgr.getLinkedAccount().getAccountInfo().displayName);
        }
        mButtonOK.setText("OK");
        mButtonUnlink.setVisibility(View.VISIBLE);
    }


    public void processUnlinked(){
        mTextOutput.setText("This app needs access a dropbox account");
        mButtonOK.setText("Link to Dropbox");
        mButtonUnlink.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                //start next activity
                linked = true;
                Intent intent = new Intent(this, ChooserActivity.class);
                startActivity(intent);
            } else {
                showToast("Link to Dropbox failed or was cancelled.");
            }
        } else if (requestCode == ENCRYPT_CHOOSER) {
            if (resultCode == Activity.RESULT_OK) {

            }
        } else super.onActivityResult(requestCode, resultCode, data);

    }

    private void getPopupWindow()  {
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_pwd,
                (ViewGroup) findViewById(R.id.popup_element));
        getPwd = (EditText) findViewById(R.id.text_pwd);
        pwindow = new PopupWindow(layout, 300, 370, true);
        pwindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }
    public void onClickClose(View v) {
        pwindow.dismiss();

    }


    //****************************************************************************************************************
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
//******************************************************************************************************************************************

