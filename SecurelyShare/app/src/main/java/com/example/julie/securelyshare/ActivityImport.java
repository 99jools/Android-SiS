package com.example.julie.securelyshare;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class ActivityImport extends ActivityMain {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void importGroup(View v) {
        showToast("Import");
    }

    public void listGroups(View view) {
        // Check what fragment is currently shown, replace if needed.
        FragmentListGroups lg = (FragmentListGroups) getFragmentManager()
                .findFragmentById(R.id.right);
        if (lg == null) {
            // Make new fragment to input data
            lg = FragmentListGroups.newInstance();
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.right, lg);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }
}