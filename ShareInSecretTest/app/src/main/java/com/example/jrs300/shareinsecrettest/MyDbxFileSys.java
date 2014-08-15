package com.example.jrs300.shareinsecrettest;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by jrs300 on 07/08/14.
 */
public class MyDbxFileSys {
    private DbxAccountManager mDbxAcctMgr;
    private DbxFileSystem mDbxFileSys;
    private DbxPath root;

    //constructor
    public MyDbxFileSys(Context context) throws DbxException.Unauthorized {
        this.mDbxAcctMgr = new DropboxSetup(context.getApplicationContext()).getAccMgr();
        this.mDbxFileSys = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
        this.root = new DbxPath("/ShareInSecret");
    }

    public DbxPath getRoot(){
        return this.root;
    }


    /**
     * Overloaded method to generate a DbxFile to use as output file
     * Uses just a filename as input and adds .xps to make the output filename
     * @param filename
     * @return
     * @throws IOException
     */
    public DbxFile getOutFile(String filename) throws IOException {
        String savename = filename + ".xps";
        DbxPath savePath = new DbxPath(this.root, savename);
        // Create DbxFileSystem for synchronized file access and ensure first sync is complete.
        DbxFileSystem dbxFileSys = DbxFileSystem.forAccount(this.mDbxAcctMgr.getLinkedAccount());
        // Create file only if it doesn't already exist.
        if (!dbxFileSys.exists(savePath)) {
            return dbxFileSys.create(savePath);
        } else return dbxFileSys.open(savePath);
    }


    public DbxFile getOutFile(File inFile) throws IOException {
         return getOutFile(inFile.getName());
    }

    public DbxFile getInFile(DbxFileInfo fileInfo ) throws DbxException {
        return this.mDbxFileSys.open(fileInfo.path);
    }



    public List<DbxFileInfo> listRoot() throws DbxException {
        // Get the contents of the ShareInSecret folder. This will block until we can
        // sync metadata the first time.
        return this.mDbxFileSys.listFolder(root);
    }

    public List<DbxFileInfo> listFolder(DbxPath folder) throws DbxException {
        // Get the contents of the ShareInSecret folder. This will block until we can
        // sync metadata the first time.
        return this.mDbxFileSys.listFolder(folder);
    }

    public  DbxFileInfo getFileInfo(DbxPath p) throws DbxException {
        return this.mDbxFileSys.getFileInfo(p);
    }



}