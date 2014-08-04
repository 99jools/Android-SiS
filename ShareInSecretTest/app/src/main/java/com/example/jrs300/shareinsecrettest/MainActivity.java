package com.example.jrs300.shareinsecrettest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;

public class MainActivity extends Activity {

    private static final int REQUEST_LINK_TO_DBX = 0;
    private DbxAccountManager mDbxAcctMgr;
 //   private DbxAccount mDbxAcct;
    private TextView mTextOutput;
    private Button mButtonUnlink;
    private Button mButtonOK;
    private boolean linked;

    private String test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextOutput = (TextView) findViewById(R.id.textView2);  //set up variable linked to TextView
        mButtonUnlink = (Button) findViewById(R.id.button_unlink);
        mButtonOK = (Button) findViewById(R.id.button_OK);
        mDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();
        test = "in onCreate";

    }


    @Override
    protected void onResume() {
        Log.e("Main Activity  ", "onResume");
        super.onResume();
        linked = mDbxAcctMgr.hasLinkedAccount();
        if (mDbxAcctMgr.hasLinkedAccount()) {
            processLinked();
        } else {
            processUnlinked();
        }
        test = "in onCreate";
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
                intent = new Intent(this, FileChooserActivity.class);
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
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }
                });
                // create alert dialog
                AlertDialog alertDialog = ad.create();

                // show it
                alertDialog.show();
                return true;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProceed(View view){
        Log.e("Main Activity  ", "onProceed");
        //check which scenario we are in
        if (linked) {
            //create intent for Chooser activity
            Intent intent = new Intent(this, ChooserActivity.class);
            startActivity(intent);
        } else {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }

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
        Log.e("method call", "processUnLinked");
        mTextOutput.setText("This app needs access a dropbox account");
        mButtonOK.setText("Link to Dropbox");
        mButtonUnlink.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Main Activity  ", "onActivityResult");
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                //start next activity
                linked = true;
                Intent intent = new Intent(this, ChooserActivity.class);
                startActivity(intent);
            } else {
                showToast("Link to Dropbox failed or was cancelled.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //****************************************************************************************************************
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
public void methodA(View v){
    test = "This text is set in methodA";
}

    public void methodB(View v){
        showToast(test);
    }


}
