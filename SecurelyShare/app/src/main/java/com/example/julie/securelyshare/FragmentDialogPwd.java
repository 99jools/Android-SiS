package com.example.julie.securelyshare;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentDialogPwd extends DialogFragment implements View.OnClickListener {
    Button mSubmit;
    Communicator communicator;
    EditText mEditPwd, mEditPwd2;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd, container, false);
        mEditPwd = (EditText) view.findViewById(R.id.init_pwd);
        mEditPwd2 = (EditText) view.findViewById(R.id.init_pwd2);
        mSubmit = (Button) view.findViewById(R.id.init_submit);
        mSubmit.setOnClickListener(this);
        setCancelable(false);
        getDialog().setTitle("Set up Master password/phrase");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        //check that passwords match
        String s1 = mEditPwd.getText().toString().trim();
        String s2 = mEditPwd2.getText().toString().trim();
        if (s1.equals(s2)) {
            //return password to calling activity
            communicator.onDialogResponse(s1);
            dismiss();
        } else mEditPwd2.setError("Passwords don't match");
    }
}