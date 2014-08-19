package com.example.julie.securelyshare;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class FragmentDialogUnlock extends DialogFragment implements View.OnClickListener {
    Button mUnlock, mCancel;
    Communicator communicator;
    int layoutID;

    public static FragmentDialogUnlock newInstance(int title, int layoutID) {
        FragmentDialogUnlock frag = new FragmentDialogUnlock();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("layoutID", layoutID);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutID = getArguments().getInt("layoutID");
        View view = inflater.inflate(layoutID, container, false);
        mUnlock = (Button) view.findViewById(R.id.button_unlock);
        mCancel = (Button) view.findViewById(R.id.button_cancel);
        mUnlock.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        setCancelable(false);
        getDialog().setTitle(getArguments().getInt("title"));
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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



}
