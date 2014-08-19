package com.example.julie.securelyshare;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
based on code from http://www.androidbegin.com/tutorial/android-dialogfragment-tutorial/
 */
public class ActivityCreate extends ActivityMain implements Communicator {
    Button dfragbutton;
    Button alertdfragbutton;
    FragmentManager fm = getFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_create);

        dfragbutton = (Button) findViewById(R.id.dfragbutton);
        alertdfragbutton = (Button) findViewById(R.id.alertdfragbutton);

        FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
        dFragment.show(fm, "Dialog Fragment");






        // Capture button clicks
        dfragbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
                dFragment.show(fm, "Dialog Fragment");
            }
        });

        // Capture button clicks
        alertdfragbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                FragmentAlertDialog alertdFragment = new FragmentAlertDialog();
                alertdFragment.show(fm, "Alert Dialog Fragment");
            }
        });
    }

    @Override
    public void alertDialogResponse(int title, int whichButton) {

    }

    @Override
    public void onDialogResponse(String data)
    {
        showToast(data);
    }
}
