package com.example.julie.securelyshare;


// Displays a list of items that are managed by an adapter similar to
// ListActivity. It provides several methods for managing a list view, such
// as the onListItemClick() callback to handle click events.


import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class FragmentListGroups extends ListFragment {

    private String[] groups;
    private AppKeystore aks;

    public static FragmentListGroups newInstance() {
          return new FragmentListGroups();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //populate alias array
        try {
            aks = new AppKeystore();
            groups = aks.getGroups();
        } catch (KeystoreAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                groups));

        View rightFrame = getActivity().findViewById(R.id.right);
    }


/*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(position, true);
        Toast.makeText(getActivity(),
                "onListItemClick position is" + position, Toast.LENGTH_LONG)
                .show();

    }
*/

}

    /*
private String[] groups;
private String groupID;
private ListView listgroups;
private AppKeystore aks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        listgroups = (ListView) findViewById(android.R.id.list);
        //populate alias array
        try {
            aks = new AppKeystore();
            groups = aks.getGroups();
        } catch (KeystoreAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, groups));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        groupID = groups[position];
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.delete_confirm, R.string.delete_msg);
        newFragment.show(getFragmentManager(), "dialogG");
    }


    @Override
    public void alertDialogResponse(int titleInt, int whichButton) {
        if (whichButton == Communicator.POS_CLICK) {
            try {
                aks.deleteGroupKey(groupID);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogResponse(String data) {

    }

    @Override
    public void onDbxFileSelected(DbxFileInfo mDbxFileInfo) {

    }
}
*/