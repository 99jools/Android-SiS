package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public  class FragmentDetails extends Fragment implements View.OnClickListener{
    Button mContinue;
    EditText mPlaintext;
    Communicator communicator;

    public static FragmentDetails newInstance(String myText) {
        FragmentDetails f = new FragmentDetails();
        Bundle args = new Bundle();
        args.putString("myText", myText);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        mPlaintext = (EditText) view.findViewById(R.id.plaintext);
        mPlaintext.setText(getPlaintext());
        mContinue = (Button) view.findViewById(R.id.btn_continue);
        mContinue.setOnClickListener(this);
        return view;
    }


    public String getPlaintext() {
        String myText = getArguments().getString("myText");
        Log.e("myText", myText);
        return myText;
    }

    @Override
    public void onClick(View view) {
      communicator.onDialogResponse(mPlaintext.getText().toString());
    }
}
