package com.example.julie.myapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class TestActivity extends Activity implements AdapterView.OnItemSelectedListener {

    String[] groups = {"groupa", "groupb", "groupx", "groupy", "groupz", "another group with a long name"} ;
    Spinner spinner;

    EditText fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, groups);
        fileName = (EditText) findViewById(R.id.fileName);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        View detailsFrame = findViewById(R.id.details);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String groupID = groups[position];
        showToast(groupID);
            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment) getFragmentManager()
                    .findFragmentById(R.id.details);
            if (details == null) {
                // Make new fragment to input data
                details = DetailsFragment.newInstance(groupID,fileName.getText().toString());
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }



}
