package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountInfo;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private static final String appKey = "n8sv033pnhqujme";
    private static final String appSecret = "mlx7499mv5t3tmq";

    private static final int REQUEST_LINK_TO_DBX = 0;
    private DbxAccountManager mDbxAcctMgr;
    private DbxAccountInfo mDbxAcctInfo;
    private TextView mTextOutput;
    private boolean linked



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextOutput = (TextView) findViewById(R.id.textView2);  //set up variable linked to TextView

        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);

        //if linked - get account details (otherwise screen will display link request)
        if (mDbxAcctMgr.hasLinkedAccount()) {
            this.linked = true;
            mDbxAcctInfo = mDbxAcctMgr.getLinkedAccount().getAccountInfo();
        } else this.linked = false;
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*
         * check if linked account exists and display appropriate message
         */
        if (mDbxAcctMgr.hasLinkedAccount()) {

            // check if account information is available
            if ( (mDbxAcctInfo==null) ){
                //linked and missing account info - get from server
                mDbxAcctInfo = mDbxAcctMgr.getLinkedAccount().getAccountInfo();  //check if this step is necessary
            } else processLinked();

        } else processUnlinked();

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

    public void onProceed(View view){

        //check which scenario we are in


        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }



    public void linkDifferent(View view){
        mDbxAcctMgr.unlink();  //note - this will remove all locally stored account information
        recreate();
    }

    public void processLinked(){

        //check if account details are available
        mTextOutput.setText("You are currently linked to Dropbox account\n " + mDbxAcctInfo.displayName);
    }


    public void processUnlinked(){
        mTextOutput.setText("This app needs access to your dropbox account. \nClick OK to link.");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                //start next activity
                Intent intent = new Intent(this, ChooserActivity.class);
                startActivity(intent);
            } else {
                showToast("Link to Dropbox failed or was cancelled.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doDropboxTest() {
        try {
            final String TEST_DATA = "Hello Dropbox";
            final String TEST_FILE_NAME = "hello_dropbox.txt";
            DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

            // Create DbxFileSystem for synchronized file access.
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

            // Print the contents of the root folder. This will block until we can
            // sync metadata the first time.
            List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
            showToast("\nContents of app folder:");
            for (DbxFileInfo info : infos) {
                showToast(info.path + ", " + info.modifiedTime );
            }

            // Create a test file only if it doesn't already exist.
            if (!dbxFs.exists(testPath)) {
                DbxFile testFile = dbxFs.create(testPath);
                try {
                    testFile.writeString(TEST_DATA);
                } finally {
                    testFile.close();
                }
                showToast("Created new file '" + testPath + "'.");
            }

            // Read and print the contents of test file. Since we're not making
            // any attempt to wait for the latest version, this may print an
            // older cached version. Use getSyncStatus() and/or a listener to
            // check for a new version.
            if (dbxFs.isFile(testPath)) {
                String resultData;
                DbxFile testFile = dbxFs.open(testPath);
                try {
                    resultData = testFile.readString();
                } finally {
                    testFile.close();
                }
                showToast("Read file '" + testPath + "' and got data:\n " + resultData);
            } else if (dbxFs.isFolder(testPath)) {
                showToast("'" + testPath.toString() + "' is a folder.");
            }
        } catch (IOException e) {
            showToast("Dropbox test failed: ");
        }
    }
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


}