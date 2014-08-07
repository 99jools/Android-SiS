package com.example.jrs300.shareinsecrettest;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.IOException;

/**
 * Created by jrs300 on 07/08/14.
 */
public class MyDbxFile {
    private DbxAccountManager mDbxAcctMgr;


    public MyDbxFile( Context context){
        this.mDbxAcctMgr= new DropboxSetup(context.getApplicationContext()).getAccMgr();

    }


    private DbxFile getDbxOutputFile() throws IOException {

        DbxPath dir = new DbxPath("/ShareInSecret");
        DbxPath savePath = new DbxPath(dir, this.saveName);

        // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
        DbxFileSystem dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());

        // Create file only if it doesn't already exist.
        if (!dbxFileSys.exists(savePath)) {
            return dbxFileSys.create(savePath);
        } else return dbxFileSys.open(savePath);
    }