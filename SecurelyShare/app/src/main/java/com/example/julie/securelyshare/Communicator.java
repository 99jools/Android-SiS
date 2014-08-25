package com.example.julie.securelyshare;

import com.dropbox.sync.android.DbxFileInfo;

/**
 * Created by Julie on 19/08/2014.
 */
public interface Communicator {

    public static final int POS_CLICK = -1;
    public static final int NEG_CLICK = -2;

    public void alertDialogResponse(int title, int whichButton);

    public void onDialogResponse(String data);

    public void onDbxFileSelected(DbxFileInfo mDbxFileInfo);

}
