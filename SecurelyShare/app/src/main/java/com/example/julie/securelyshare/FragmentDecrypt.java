package com.example.julie.securelyshare;



import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentDecrypt extends Fragment implements Communicator {

    private MyDbxFiles mDbx;
    private Communicator communicator;

    public FragmentDecrypt() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            this.mDbx = new MyDbxFiles(getActivity());
        } catch (DbxException.Unauthorized unauthorized) {
            unauthorized.printStackTrace();
        }

    }



    @Override
    public void onDbxFileSelected(DbxFileInfo mDbxFileInfo) {

    }



    @Override
    public void alertDialogResponse(int title, int whichButton) {
    }

    @Override
    public void onDialogResponse(String data) {
    }


}




}