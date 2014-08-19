package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ActivityInitialise is only called when the app is first installed or when the keystore containing the public/private keypair
 * has been otherwise removed (manually by user or due to 3 failed password attempts)
 */
public class ActivityInitialise extends Activity implements Communicator {

    public static final String CERTIFICATE_FILE = "SiSCert.ks";
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
    public static final String KEYSTORE_TYPE = "BKS";
    public static final String KEYPAIR_ALGORITHM = "RSA";
    public static final int    KEYPAIR_LENGTH = 1024;

    Button mButtonContinue;
    Button mButtonCancel;
    EditText mEditPwd;
    EditText mEditPwd2;

    FragmentManager fm = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialise);
        mButtonContinue = (Button) findViewById(R.id.button_continue);

    }
     //create certificate KeyStore

        //Export certificate

        //create app keystore for group encryption keys

        // confirm to user with alert dialog that keystore password is
        //not recoverable

        // offer alternative to import certificate keystore - confirm that correct format and
        //can be accessed with supplied password
        //


    public void showDialog(View view) {
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.init_keystore, R.string.init_restore);
        newFragment.show(getFragmentManager(), "dialog");
        mButtonContinue.setVisibility(View.GONE);
    }

        @Override
    public void alertDialogResponse(int titleInt, int whichButton) {
        if (titleInt == R.string.init_keystore){
            // this is a response from the Import Keystore dialog
            if (whichButton == Communicator.POS_CLICK)
                showToast("Import Keystore functionality to be inserted here" );
            else  {
                //new keystore setup is required
                //show dialog to set up a master password
                FragmentDialogPwd dFragment = new FragmentDialogPwd();
                dFragment.show(fm, "Dialog Fragment Pwd");
            }
        }
    }

    @Override
    public void onDialogResponse(String data) {
        showToast(data);
    }
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}



