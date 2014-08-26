package com.example.julie.securelyshare;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ActivityGroups extends ActivityMain {

    private String[] groups;
    private String groupID;
    private ListView listgroups;
    private AppKeystore aks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, groups);
        listgroups.setAdapter(adapter);
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
}
