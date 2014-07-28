package com.example.jrs300.shareinsecret;

import android.content.Context;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;

/**
 * Created by jrs300 on 23/07/14.
 */
public class DropboxSync {
    private static final String APPKEY = "n8sv033pnhqujme";
    private static final String APPSECRET = "mlx7499mv5t3tmq";
    private DbxAccountManager dAccManager;
    private DbxChooser dChooser;

    public DropboxSync(Context appContext) {
        this.dAccManager = DbxAccountManager.getInstance(appContext, APPKEY, APPSECRET);
        this.dChooser = new DbxChooser(APPKEY);
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