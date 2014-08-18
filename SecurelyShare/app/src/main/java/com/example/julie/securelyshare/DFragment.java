package com.example.julie.securelyshare;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_fragment, container, false);
            getDialog().setTitle("DFragment Tutorial");
            return view;
        }
    }
