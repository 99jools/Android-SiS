package com.example.jrs300.shareinsecrettest;


import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
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
    private File mCurrentNode = null;
    private File mLastNode = null;
    private File mRootNode = null;
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
            DbxPath dir = new DbxPath("/ShareInSecret");
            fcFileInfo = fcDbxFileSystem.listFolder(dir);
        } catch (DbxException unauthorized) {
            unauthorized.printStackTrace();
        }

        //restore saved state if available
        if (savedInstanceState != null) {
            mRootNode = (File) savedInstanceState.getSerializable("root_node");
            mLastNode = (File) savedInstanceState.getSerializable("last_node");
            mCurrentNode = (File) savedInstanceState.getSerializable("current_node");
        }
        refreshFileList();
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("root_node", mRootNode);
        outState.putSerializable("current_node", mCurrentNode);
        outState.putSerializable("last_node", mLastNode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id){
        File f = (File) parent.getItemAtPosition(position);
        if (position == 1) {
            if (mCurrentNode.compareTo(mRootNode)!=0) {
                mCurrentNode = f.getParentFile();
                refreshFileList();
            }
        } else if (f.isDirectory()) {
            mCurrentNode = f;
            refreshFileList();
        } else {
            Toast.makeText(this, "You selected: "+f.getName()+"!", Toast.LENGTH_SHORT).show();
        }

        try {
            // get file input stream
            FileInputStream fis = getFis(position);
            FileOutputStream fos = getFos(position, "To Dropbox");
            decryptFile(fis, fos);
        } catch (IOException e) {
            Log.e("decrypt file ", e.getMessage());
        } catch (GeneralSecurityException e) {
            Log.e("decrypt file ",e.getMessage());
        } catch (MissingPwdException e) {
            Log.e("decrypt file ",e.getMessage());
        }

        // Need to add something to handle Failed or was cancelled by the user.
    }

    private void refreshFileList() {
        if (mRootNode == null) mRootNode = new File(Environment.getExternalStorageDirectory().toString());
        if (mCurrentNode == null) mCurrentNode = mRootNode;
        mLastNode = mCurrentNode;
        File[] files = mCurrentNode.listFiles();
        mFiles.clear();
        mFiles.add(mRootNode);
        mFiles.add(mLastNode);
        if (files!=null) {
            for (int i = 0; i< files.length; i++) mFiles.add(files[i]);
        }
        mAdapter.notifyDataSetChanged();
    }





    private FileInputStream getFis(int position) throws IOException {
        DbxPath inPath = fcFileInfo.get(position).path;
        //open a file input stream with given path
        DbxFile myCiphertextFile;
        myCiphertextFile = fcDbxFileSystem.open(inPath);
        return myCiphertextFile.getReadStream();
    }

    private FileOutputStream getFos(int position, String outLoc) throws IOException {

/*******************************************************************************************************************************
 * Writing decrypted file to dropbox is a temporary measure just for testing
 * THIS SHOUD NOT BE LEFT IN FINAL VERSION
 *
 */
        String out = fcFileInfo.get(position).path.getName();
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
