package com.example.julie.securelyshare;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * http://www.cs.dartmouth.edu/~campbell/cs65/lecture13/lecture13.html
 *
 */
public class FragmentAlertDialog extends DialogFragment {
    Communicator myCommunicator;

    public static FragmentAlertDialog newInstance(int title, int msg) {
        FragmentAlertDialog frag = new FragmentAlertDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("msg", msg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myCommunicator = (Communicator) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int  title = getArguments().getInt("title");
        int msg = getArguments().getInt("msg");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.crypto)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                myCommunicator.onOptionSelected(title, whichButton);
                            }
                        }
                )
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                               myCommunicator.onOptionSelected(title, whichButton);
                            }
                        }
                )
                .create();
    }
    //this interface has only one method (note, all implementing classes must supply this method)
    interface Communicator {
        public static final int POS_CLICK = -1;
        public static final int NEG_CLICK = -2;
        public void onOptionSelected(int title, int whichButton);
    }


}
/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        // Set Dialog Icon
                ad.setIcon(R.drawable.crypto);
                        // Set Dialog Title
                ad.setTitle("Alert FragmentDialogUnlock");
                        // Set Dialog Message
                ad.setMessage("Alert FragmentDialogUnlock Tutorial");

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
