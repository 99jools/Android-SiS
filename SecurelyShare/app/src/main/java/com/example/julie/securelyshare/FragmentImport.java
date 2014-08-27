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
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentImport extends Fragment  {

    private MyDbxFiles mDbx;
    private Communicator communicator;

    public FragmentImport() {
        // Required empty public constructor
    }
    public static FragmentImport newInstance() {

        return new FragmentImport();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_import, container, false);
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
