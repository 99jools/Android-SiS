package com.example.julie.myapplication;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public  class DetailsFragment extends Fragment {

    // Create a new instance of DetailsFragment, initialized to show the
    // text at 'index'.

    public static DetailsFragment newInstance(String groupID, String fileName) {
        DetailsFragment f = new DetailsFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("groupID", groupID);
        args.putString("fileName", fileName);
        f.setArguments(args);
        return f;
    }

    public String getFileName() {
        return getArguments().getString("fileName");
    }

    public String getGroupID() {
        return getArguments().getString("groupID");
    }


    // We create the UI with a scrollview and text and return a reference to
    // the scoller which is then drawn to the screen

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_details, container, false);
        return v;
    }
}

