package com.example.julie.securelyshare;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class FragmentDecrypt extends Fragment  {

    private MyDbxFiles mDbx;


    public FragmentDecrypt() {
        // Required empty public constructor
    }
    public static FragmentDecrypt newInstance() {

        return new FragmentDecrypt();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_decrypt, container, false);
        return view;
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
