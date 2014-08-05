package com.example.jrs300.shareinsecrettest;

import android.content.Context;
import android.util.Log;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;

/**
 * Created by jrs300 on 23/07/14.
 */
public class DropboxSetup {
    private static final String APPKEY = "rq1nhqsionfgtmd";
    private static final String APPSECRET = "b0hx2fvx2fcj9pk";
    private DbxAccountManager dAccManager;
    private DbxChooser dChooser;

    public DropboxSetup(Context appContext) {
        this.dAccManager = DbxAccountManager.getInstance(appContext, APPKEY, APPSECRET);
        this.dChooser = new DbxChooser(APPKEY);
        Log.e("", "setup");
    }

    public DbxAccountManager getAccMgr(){
        return dAccManager;
    }


    public DbxAccount getAccount() {

        return dAccManager.getLinkedAccount();
    }

    public DbxChooser getChooser() {
        return dChooser;
    }

    public DbxFileSystem getFileSystem() throws DbxException.Unauthorized {
        return DbxFileSystem.forAccount(dAccManager.getLinkedAccount());
    }
}