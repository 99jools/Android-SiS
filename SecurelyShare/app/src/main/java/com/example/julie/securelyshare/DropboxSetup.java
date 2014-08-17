package com.example.julie.securelyshare;

import android.content.Context;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;

/**
 * Created by jrs300 on 23/07/14.
 */
public class DropboxSetup {
    private static final String APPKEY = "nyocwukormiszd9";
    private static final String APPSECRET = "kbg6wiavip0ukah";
    private DbxAccountManager dAccManager;


    public DropboxSetup(Context appContext) {
        this.dAccManager = DbxAccountManager.getInstance(appContext, APPKEY, APPSECRET);

    }

    public DbxAccountManager getAccMgr(){
        return dAccManager;
    }


    public DbxAccount getAccount() {

        return dAccManager.getLinkedAccount();
    }

    public DbxFileSystem getFileSystem() throws DbxException.Unauthorized {
        return DbxFileSystem.forAccount(dAccManager.getLinkedAccount());
    }
}