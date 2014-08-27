package com.example.julie.securelyshare;


import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class ActivityDecrypt extends ListActivity {

    private MyDbxFiles mDbx;
    private DbxFileInfo mCurrentNode = null;
    private DbxFileInfo mRootNode = null;
    private List<DbxFileInfo> fcFileInfo;
    private ArrayList<DbxFileInfo> mFiles = new ArrayList<DbxFileInfo>();
    private CustomAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        mAdapter = new CustomAdapter(this, R.layout.list_row, mFiles);
        setListAdapter(mAdapter);

        // Check what fragment is currently shown, replace if needed.
        FragmentDecrypt fd = (FragmentDecrypt) getFragmentManager()
                .findFragmentById(R.id.right);
        if (fd == null) {
            // Make new fragment to input data
            fd = FragmentDecrypt.newInstance();
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.add(R.id.right, fd);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
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
    public void onListItemClick(ListView parent, View v, int position, long id) {
        DbxFileInfo fileInfo = (DbxFileInfo) parent.getItemAtPosition(position);
        try {
            if (position == 1) {
                if (mCurrentNode.compareTo(mRootNode) != 0) {
                    DbxPath p = fileInfo.path.getParent();
                    mCurrentNode = mDbx.getFileInfo(p);
                }
                refreshFileList();
            } else {
                if (fileInfo.isFolder) {
                    mCurrentNode = fileInfo;
                    refreshFileList();
                } else
                    try {
                        doDecrypt(fileInfo);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (MyKeystoreAccessException e) {
                        showToast(e.getMessage());
                        finish();
                    } catch (MyMissingKeyException e) {
                        showToast(e.getMessage());
                        finish();
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

    private void doDecrypt(DbxFileInfo fileInfo) throws IOException, MyKeystoreAccessException,
            GeneralSecurityException, MyMissingKeyException {
        //open a file input stream with given path
        DbxFile dbxIn = mDbx.getInFile(fileInfo);
        FileInputStream fis = dbxIn.getReadStream();

        File myPlaintextFile = getFos(fileInfo.path.getName());
        FileOutputStream fos = new FileOutputStream(myPlaintextFile);
        FileCryptor.decryptFile(fis, fos);
        dbxIn.close();
        // start new intent to open
        Uri myUri = Uri.fromFile(myPlaintextFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(myUri);
        startActivity(intent);
    }

    private File getFos(String out) throws IOException {
        //sort out filemame for decrypted file
        out = out.substring(0, out.length() - 4);
        return new File(getExternalCacheDir(), out);
    } //end getFos

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.import_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_create:
                intent = new Intent(this, ActivityCreate.class);
                startActivity(intent);
                return true;
            case R.id.action_decrypt:
                intent = new Intent(this, ActivityDecrypt.class);
                startActivity(intent);
                return true;
            case R.id.action_import:
                intent = new Intent(this, ActivityImport.class);
                startActivity(intent);
                return true;
            case R.id.action_admin:
                intent = new Intent(this, ActivityAdmin.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
