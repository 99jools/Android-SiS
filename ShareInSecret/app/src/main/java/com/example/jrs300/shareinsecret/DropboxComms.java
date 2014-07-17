package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.IOException;
import java.util.List;

public class DropboxComms extends Activity {

    private static final String appKey = "n8sv033pnhqujme";
    private static final String appSecret = "mlx7499mv5t3tmq";

    private static final int REQUEST_LINK_TO_DBX = 0;
    private DbxAccountManager mDbxAcctMgr;
    private TextView mTestOutput;
    private Button mLinkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_comms);
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
        mTestOutput = (TextView) findViewById(R.id.test_output);
        mLinkButton = (Button) findViewById(R.id.link_button);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDbxAcctMgr.hasLinkedAccount()) {
            showLinkedView();
            doDropboxTest();
        } else {
            showUnlinkedView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dropbox_comms, menu);
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

    private void showLinkedView() {
        mLinkButton.setVisibility(View.GONE);
        mTestOutput.setVisibility(View.VISIBLE);
    }

    private void showUnlinkedView() {
        mLinkButton.setVisibility(View.VISIBLE);
        mTestOutput.setVisibility(View.GONE);
    }

    public void linkToDropbox(View view) {

        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                doDropboxTest();
            } else {
                mTestOutput.setText("Link to Dropbox failed or was cancelled.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doDropboxTest() {
        mTestOutput.setText("Dropbox Sync API Version " + DbxAccountManager.SDK_VERSION_NAME + "\n");
        try {
            final String TEST_DATA = "Hello Dropbox";
            final String TEST_FILE_NAME = "hello_dropbox.txt";
            DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

            // Create DbxFileSystem for synchronized file access.
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

            // Print the contents of the root folder.  This will block until we can
            // sync metadata the first time.
            List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
            mTestOutput.append("\nContents of app folder:\n");
            for (DbxFileInfo info : infos) {
                mTestOutput.append("    " + info.path + ", " + info.modifiedTime + '\n');
            }

            // Create a test file only if it doesn't already exist.
            if (!dbxFs.exists(testPath)) {
                DbxFile testFile = dbxFs.create(testPath);
                try {
                    testFile.writeString(TEST_DATA);
                } finally {
                    testFile.close();
                }
                mTestOutput.append("\nCreated new file '" + testPath + "'.\n");
            }

            // Read and print the contents of test file.  Since we're not making
            // any attempt to wait for the latest version, this may print an
            // older cached version.  Use getSyncStatus() and/or a listener to
            // check for a new version.
            if (dbxFs.isFile(testPath)) {
                String resultData;
                DbxFile testFile = dbxFs.open(testPath);
                try {
                    resultData = testFile.readString();
                } finally {
                    testFile.close();
                }
                mTestOutput.append("\nRead file '" + testPath + "' and got data:\n    " + resultData);
            } else if (dbxFs.isFolder(testPath)) {
                mTestOutput.append("'" + testPath.toString() + "' is a folder.\n");
            }
        } catch (IOException e) {
            mTestOutput.setText("Dropbox test failed: " + e);
        }
    }
}
