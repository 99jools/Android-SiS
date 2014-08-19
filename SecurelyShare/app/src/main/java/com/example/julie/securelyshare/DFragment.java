package com.example.julie.securelyshare;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DFragment extends DialogFragment implements View.OnClickListener {
    Button mUnlock, mCancel;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("In DFragment", "");
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        mUnlock = (Button) view.findViewById(R.id.button_unlock);
        mCancel = (Button) view.findViewById(R.id.button_cancel);
        mUnlock.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        getDialog().setTitle("DFragment Tutorial");
        setCancelable(false);
        return view;
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_unlock) {
            communicator.onDialogResponse("Keystore was unlocked");
            dismiss();
        } else {
            communicator.onDialogResponse("Keystore - cancel was clicked");
            dismiss();
        }
    }

    interface Communicator {
        public void onDialogResponse(String data);

    }


}
