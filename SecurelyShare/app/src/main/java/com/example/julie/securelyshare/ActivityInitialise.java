package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * ActivityInitialise is only called when the app is first installed or when the keystore containing the public/private keypair
 * has been otherwise removed (manually by user or due to 3 failed password attempts)
 */
public class ActivityInitialise extends Activity implements Communicator {

    public static final String CERTIFICATE_FILE = "SiSCert.ks";
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
    public static final String KEYSTORE_TYPE = "BKS";
    public static final String KEYPAIR_ALGORITHM = "RSA";
    public static final int KEYPAIR_LENGTH = 1024;

    private Button mButtonContinue;
    private Button mButtonCancel;
    private EditText mEditPwd;
    private EditText mEditPwd2;

    private FragmentManager fm = getFragmentManager();
    private String appPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialise);
        mButtonContinue = (Button) findViewById(R.id.button_continue);

    }
    //create certificate KeyStore

    //Export certificate


    // offer alternative to import certificate keystore - confirm that correct format and
    //can be accessed with supplied password
    //


    public void showKeystoreDialog(View view) {
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.init_keystore, R.string.init_restore);
        newFragment.show(getFragmentManager(), "dialog");
        mButtonContinue.setVisibility(View.GONE);
    }


    @Override
    public void alertDialogResponse(int titleInt, int whichButton) {
        switch (titleInt) {
            case R.string.init_keystore:
                // this is a response from the Import Keystore dialog
                if (whichButton == Communicator.POS_CLICK)
                    showToast("Import Keystore functionality to be inserted here");
                else {
                    //new keystore setup is required
                    //show dialog to set up a master password
                    FragmentDialogPwd dFragment = new FragmentDialogPwd();
                    dFragment.show(fm, "Dialog Fragment Pwd");
                }
                break;

            case R.string.init_confirm:
                if (whichButton == Communicator.POS_CLICK) {
                    Intent intent = new Intent(this, ActivityMain.class);
                    startActivity(intent);
                } else finish();   // user has chosen not to proceed;
                break;
        }
    }

    @Override
    public void onDialogResponse(String data) {
        //this is from the password dialog and returns the chosen master password
        appPwd = data;
        showToast("data");
        try {
            createKeyStore(KEYSTORE_NAME);
            createKeyStore(CERTIFICATE_FILE);
            showConfirmDialog();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        /*set up public/private keypair and export certificate.
         *NOTE: From API 19 it s possible to store this information in the system keystore
         *      however, we are currently maintaining compatibility to API 17 hence a separate
          *     keystore has to be created and will be stored in the app private storage.
         */
//TODO import public-private keystore with Bouncy Castle


    }

    public void showConfirmDialog() {
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.init_confirm, R.string.confirm_msg);
        newFragment.show(getFragmentManager(), "dialog2");
    }

    public void createKeyStore(String name) throws IOException, GeneralSecurityException {
        KeyStore newKS = KeyStore.getInstance(KEYSTORE_TYPE);
        newKS.load(null);
        FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);
        Log.e("FOS ",fos.getFD().toString());
        newKS.store(fos, appPwd.toCharArray());
        fos.close();
    }


    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

}



