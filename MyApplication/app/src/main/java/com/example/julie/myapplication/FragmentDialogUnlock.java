package com.example.julie.myapplication;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class FragmentDialogUnlock extends DialogFragment implements View.OnClickListener {
    Button mUnlock, mCancel;
    EditText mEditPwd;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_unlock, container, false);
        mUnlock = (Button) view.findViewById(R.id.button_unlock);
        mCancel = (Button) view.findViewById(R.id.button_cancel);
        mEditPwd = (EditText) view.findViewById(R.id.pwd);
        mUnlock.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        setCancelable(false);
        getDialog().setTitle("Unlock Application KeyStore");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_unlock) {
            //return password to calling activity
            communicator.onDialogResponse(mEditPwd.getText().toString().trim());
            dismiss();
        } else {
            communicator.onDialogResponse(null);
            dismiss();
        }
    }



}
