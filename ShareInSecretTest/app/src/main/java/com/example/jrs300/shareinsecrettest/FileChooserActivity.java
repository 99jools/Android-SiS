package com.example.jrs300.shareinsecrettest;


import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class FileChooserActivity extends ListActivity {
    //    ListView fcListView;
    private DbxAccountManager fcDbxAcctMgr;
    private DbxFileSystem fcDbxFileSystem;
    private List<DbxFileInfo> fcFileInfo;
    private SharedPrefs prefs;
    private DbxFileInfo mCurrentNode = null;
    private DbxFileInfo mLastNode = null;
    private DbxFileInfo mRootNode = null;
    private ArrayList<DbxFileInfo> mFiles = new ArrayList<DbxFileInfo>();
    private CustomAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        mAdapter = new CustomAdapter(this, R.layout.list_row, mFiles);
        setListAdapter(mAdapter);

        //get DbxFileSystem
        try {
            fcDbxAcctMgr = new DropboxSetup(this.getApplicationContext()).getAccMgr();
            fcDbxFileSystem = DbxFileSystem.forAccount(fcDbxAcctMgr.getLinkedAccount());
            // Get the contents of the ShareInSecret folder. This will block until we can
            // sync metadata the first time.
            fcFileInfo = fcDbxFileSystem.listFolder(new DbxPath("/ShareInSecret"));
            refreshFileList();
        } catch (DbxException unauthorized) {
            unauthorized.printStackTrace();
        }

        /*restore saved state if available
        if (savedInstanceState != null) {
            mRootNode = (DbxFileInfo) savedInstanceState.getSerializable("root_node");
            mLastNode = (DbxFileInfo) savedInstanceState.getSerializable("last_node");
            mCurrentNode = (DbxFileInfo) savedInstanceState.getSerializable("current_node");
        } */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_chooser, menu);
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

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id){
        DbxFileInfo fileInfo = (DbxFileInfo) parent.getItemAtPosition(position);
        try {
            if (position == 1) {
                if (mCurrentNode.compareTo(mRootNode)!=0) {

                    DbxPath p = fileInfo.path.getParent();
                    mCurrentNode = fcDbxFileSystem.getFileInfo(p);
                }
                refreshFileList();

            } else {
                if (fileInfo.isFolder) {
                    mCurrentNode = fileInfo;
                    refreshFileList();
                } else {
                    // get file input stream
                    FileInputStream fis = getFis(fileInfo.path);
                    FileOutputStream fos = getFos(fileInfo.path.getName());
                    decryptFile(fis, fos);
                
                }
            }
            }catch (IOException e) {
                Log.e("decrypt file ", e.getMessage());
            } catch (GeneralSecurityException e) {
                Log.e("decrypt file ",e.getMessage());
            } catch (MissingPwdException e) {
                Log.e("decrypt file ",e.getMessage());
            }
        }

        // Need to add something to handle Failed or was cancelled by the user.


    private void refreshFileList() throws DbxException {
        if (mRootNode == null) mRootNode = fcDbxFileSystem.getFileInfo(new DbxPath("/ShareInSecret"));
        if (mCurrentNode == null) mCurrentNode = mRootNode;
        mLastNode = mCurrentNode;
        fcFileInfo = fcDbxFileSystem.listFolder(mCurrentNode.path);
        mFiles.clear();
        mFiles.add(mRootNode);
        mFiles.add(mLastNode);
        mFiles.addAll(fcFileInfo);
        mAdapter.notifyDataSetChanged();
    }

    private FileInputStream getFis(DbxPath inPath) throws IOException {

        //open a file input stream with given path
        DbxFile myCiphertextFile;
        myCiphertextFile = fcDbxFileSystem.open(inPath);
        return myCiphertextFile.getReadStream();
    }

    private FileOutputStream getFos(String out) throws IOException {

/*******************************************************************************************************************************
 * Writing decrypted file to dropbox is a temporary measure just for testing
 * THIS SHOUD NOT BE LEFT IN FINAL VERSION
 *
 */
        out = out.substring(0, out.length() - 4) + ".dec.txt";
        DbxPath outPath = new DbxPath(out);
        DbxFile myPlaintextFile;
        if (fcDbxFileSystem.exists(outPath))
            myPlaintextFile = fcDbxFileSystem.open(outPath);
        else myPlaintextFile = fcDbxFileSystem.create(outPath);
        return myPlaintextFile.getWriteStream();

//*****************************************************************************************************************************

    } //end getFos

    /**
     * Takes a fileInputStream and returns the corresponding decrypted FileOutputStream
     * @param fis
     * @param fos
     * @throws MissingPwdException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private void decryptFile(FileInputStream fis, FileOutputStream fos)
            throws MissingPwdException, IOException, GeneralSecurityException {
        //get preferences

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = new SharedPrefs(sp);

        // call static method to decrypt
        FileCryptor.decryptFile(fis, fos, prefs);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
