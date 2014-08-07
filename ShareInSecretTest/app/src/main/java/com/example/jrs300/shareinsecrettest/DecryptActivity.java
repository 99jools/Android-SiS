package com.example.jrs300.shareinsecrettest;


import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class DecryptActivity extends ListActivity {

    private MyDbxFiles mDbx;
    private DbxFileInfo mCurrentNode = null;
    private DbxFileInfo mRootNode = null;
    private List<DbxFileInfo> fcFileInfo;
    private ArrayList<DbxFileInfo> mFiles = new ArrayList<DbxFileInfo>();
    private CustomAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        mAdapter = new CustomAdapter(this, R.layout.list_row, mFiles);
        setListAdapter(mAdapter);

        //get DbxFileSystem and root folders

        try {
            this.mDbx = new MyDbxFiles(this);
            fcFileInfo = mDbx.listRoot();
            refreshFileList();
        } catch (DbxException unauthorized) {
            unauthorized.printStackTrace();
        }
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
                    mCurrentNode = mDbx.getFileInfo(p);
                }
                refreshFileList();

            } else {
                if (fileInfo.isFolder) {
                    mCurrentNode = fileInfo;
                    refreshFileList();
                } else {

                    //open a file input stream with given path
                    DbxFile dbxIn = mDbx.getInFile(fileInfo);
                    FileInputStream fis = dbxIn.getReadStream();

                    File myPlaintextFile  = getFos(fileInfo.path.getName());
                    FileOutputStream fos = new FileOutputStream(myPlaintextFile);
//      DbxFile myPlaintextFile  = getFos(fileInfo.path.getName());
//      FileOutputStream fos = myPlaintextFile.getWriteStream();
                    decryptFile(fis, fos);
                    dbxIn.close();
                    // start new intent to open
                    Uri myUri = Uri.fromFile(myPlaintextFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(myUri);
                    startActivity(intent);
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
        if (mRootNode == null) mRootNode = mDbx.getFileInfo(mDbx.getRoot());
        if (mCurrentNode == null) mCurrentNode = mRootNode;
        DbxFileInfo mLastNode = mCurrentNode;
        fcFileInfo = mDbx.listFolder(mCurrentNode.path);
        mFiles.clear();
        mFiles.add(mRootNode);
        mFiles.add(mLastNode);
        mFiles.addAll(fcFileInfo);
        mAdapter.notifyDataSetChanged();
    }

     private File getFos(String out) throws IOException {
        //sort out filemame for decrypted file
        out = out.substring(0, out.length() - 4);
        return new File(getExternalCacheDir(),out);
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
        SharedPrefs prefs = new SharedPrefs(sp);

        // call static method to decrypt
        FileCryptor.decryptFile(fis, fos);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
