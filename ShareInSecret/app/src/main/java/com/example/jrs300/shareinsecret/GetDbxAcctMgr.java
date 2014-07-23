package com.example.jrs300.shareinsecret;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

/**
 * Created by jrs300 on 23/07/14.
 */
public class GetDbxAcctMgr {
    private static final String appKey = "n8sv033pnhqujme";
    private static final String appSecret = "mlx7499mv5t3tmq";
    private DbxAccountManager dbxAcctMgr;

    public GetDbxAcctMgr(Context applicationContext) {
        this.dbxAcctMgr = DbxAccountManager.getInstance(applicationContext, appKey, appSecret);
    }

    public DbxAccountManager getmDbxAcctMgr() {
        return dbxAcctMgr;
    }
}