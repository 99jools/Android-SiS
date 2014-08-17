package com.example.julie.fragmentlayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "FragmentLayout: OnCreate()", Toast.LENGTH_SHORT)
                .show();

        // Sets the view. Depending on orientation it will select either
        // res/layout/fragment_layout.xml (portrait mode) or
        // res/layout-land/fragment_layout.xml (landscape mode). This is done
        // automatically by the system.
        setContentView(R.layout.fragment_layout);
    }
}