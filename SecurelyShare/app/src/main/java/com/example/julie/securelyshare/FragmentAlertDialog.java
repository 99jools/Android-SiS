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
                                myCommunicator.alertDialogResponse(title, whichButton);
                            }
                        }
                )
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                               myCommunicator.alertDialogResponse(title, whichButton);
                            }
                        }
                )
                .create();
    }
}

