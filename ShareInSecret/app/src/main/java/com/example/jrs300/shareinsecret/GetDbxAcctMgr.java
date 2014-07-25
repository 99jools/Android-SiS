package com.example.jrs300.shareinsecret;

import android.content.Context;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.sync.android.DbxAccountManager;

/**
 * Created by jrs300 on 23/07/14.
 */
public class GetDbxAcctMgr {
    private static final String APPKEY = "n8sv033pnhqujme";
    private static final String APPSECRET = "mlx7499mv5t3tmq";
    private DbxAccountManager dbxAcctMgr;
    private DbxChooser mChooser;

    public GetDbxAcctMgr(Context applicationContext) {
        this.dbxAcctMgr = DbxAccountManager.getInstance(applicationContext, APPKEY, APPSECRET);
        this.mChooser = new DbxChooser(APPKEY);
    }

    public DbxAccountManager getmDbxAcctMgr() {
        return dbxAcctMgr;
    }

    public DbxChooser getmChooser() {
        return mChooser;
    }
}