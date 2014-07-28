package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.List;

public class DownloadActivity extends Activity {

    private DbxAccountManager mDbxAcctMgr;
    DbxFileSystem dbxFileSys;
    List<DbxFileInfo> mfileInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        this.mDbxAcctMgr = new DropboxSync(getApplicationContext()).getAccMgr();
    }


    @Override
    protected void onResume() {
        Log.e("method call", "DownloadActivity - onResume");
        super.onResume();

        // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
        try {
            dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());
            if (dbxFileSys.hasSynced() == false)
                dbxFileSys.awaitFirstSync();

            List<DbxFileInfo> mfileInfoList = dbxFileSys.listFolder(DbxPath.ROOT);

        }
        catch (DbxException e) {
            showToast("dropbox initial sync failed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.download, menu);
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

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
