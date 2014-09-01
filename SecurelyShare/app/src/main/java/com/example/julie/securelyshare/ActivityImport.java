package com.example.julie.securelyshare;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class ActivityImport extends ListActivity {

    private MyDbxFiles mDbx;
    private DbxFileInfo mCurrentNode = null;
    private DbxFileInfo mRootNode = null;
    private List<DbxFileInfo> fcFileInfo;
    private ArrayList<DbxFileInfo> mFiles = new ArrayList<DbxFileInfo>();
    private CustomAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        mAdapter = new CustomAdapter(this, R.layout.list_row, mFiles);
        setListAdapter(mAdapter);

        // Check what fragment is currently shown, replace if needed.
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FragmentImport fi = (FragmentImport) fm.findFragmentById(R.id.import_right);
        if (fi == null) {
            // Make new fragment to input data
            fi = FragmentImport.newInstance();
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            ft = fm.beginTransaction();
            ft.add(R.id.import_right, fi);
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
                } else importGroup(fileInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public void importGroup(DbxFileInfo keyFileInfo) {
        try {
            DbxFile keyFile = mDbx.getInFile(keyFileInfo);
            AppKeystore aks = AppKeystore.getInstance();
            FileInputStream fis = keyFile.getReadStream();


            String groupID = keyFileInfo.path.getParent().getParent().getName();
            aks.importGroupKey(groupID, fis);
            keyFile.close();
            finish();

        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyKeystoreAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    public void listGroups(View view) {
        // Check what fragment is currently shown, replace if needed.
        FragmentListGroups lg = (FragmentListGroups) getFragmentManager()
                .findFragmentById(R.id.import_right);
        if (lg == null) {
            // Make new fragment to input data
            lg = FragmentListGroups.newInstance();
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.import_right, lg);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

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
}
