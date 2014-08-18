package com.example.julie.securelyshare;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.crypto)
                    .setTitle("my title")
                    .setPositiveButton("R.string.alert_dialog_ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((CreateActivity)getActivity()).doPositiveClick();
                                }
                            }
                    )
                    .setNegativeButton("R.string.alert_dialog_cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((CreateActivity)getActivity()).doNegativeClick();
                                }
                            }
                    )
                    .create();
        }
    }


/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        // Set Dialog Icon
                ad.setIcon(R.drawable.crypto);
                        // Set Dialog Title
                ad.setTitle("Alert DFragment");
                        // Set Dialog Message
                ad.setMessage("Alert DFragment Tutorial");

                        // Positive button
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                });

                        // Negative Button
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,    int which) {
                        // Do something else
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = ad.create();
        return alertDialog;
    }*/
