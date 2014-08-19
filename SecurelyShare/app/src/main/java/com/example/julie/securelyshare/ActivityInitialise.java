package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ActivityInitialise is only called when the app is first installed or when the keystore containing the public/private keypair
 * has been otherwise removed (manually by user or due to 3 failed password attempts)
 */
public class ActivityInitialise extends Activity implements FragmentAlertDialog.Communicator {
    Button mButtonConfirm;
    Button mButtonCancel;
    EditText mEditPwd;
    EditText mEditPwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialise);



        // Watch for button clicks.
        Button button = (Button) findViewById(R.id.button_continue);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }
        });















    }

        //create certificate KeyStore

        //Export certificate

        //create app keystore for group encryption keys

        // confirm to user with alert dialog that keystore password is
        //not recoverable

        // offer alternative to import certificate keystore - confirm that correct format and
        //can be accessed with supplied password
        //
        protected void showDialog() {
            DialogFragment newFragment = FragmentAlertDialog
                    .newInstance(R.string.init_keystore, R.string.init_restore);
            newFragment.show(getFragmentManager(), "dialog");
        }

    public void doPositiveClick() {
        // Do stuff here.
        showToast("FragmentAlertDialog - Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        showToast("FragmentAlertDialog - Negative click!");
    }
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onOptionSelected(int titleInt, int whichButton) {
        if (titleInt == R.string.init_keystore){
            // this is a response from the Import Keystore dialog
            if (whichButton == FragmentAlertDialog.Communicator.POS_CLICK)
                showToast("button pressed is YES " + whichButton );
            else   showToast("button pressed is NO " + whichButton );

        }
    }
}



