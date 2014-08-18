package com.example.julie.securelyshare;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
based on code from http://www.androidbegin.com/tutorial/android-dialogfragment-tutorial/
 */
public class CreateActivity extends MainActivity {
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

        // Capture button clicks
        dfragbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DFragment dFragment = new DFragment();
                dFragment.show(fm, "Dialog Fragment");
            }
        });

        // Capture button clicks
        alertdfragbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                MyAlertDialogFragment alertdFragment = new MyAlertDialogFragment();
                alertdFragment.show(fm, "Alert Dialog Fragment");
            }
        });
    }
    public void doPositiveClick(){
        showToast("positive click");
    }
    public void doNegativeClick(){
        showToast("positive click");
    }
}
