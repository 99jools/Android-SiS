package com.example.julie.securelyshare;



import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentDecrypt extends Fragment  {

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

    public void decryptAsString(DbxFileInfo mDbxFileInfo){
        showToast("I am going to decrypt " + mDbxFileInfo.path.getName());
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}
