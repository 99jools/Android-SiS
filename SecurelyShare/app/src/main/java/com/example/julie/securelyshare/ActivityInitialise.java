package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dropbox.sync.android.DbxFileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * ActivityInitialise is only called when the app is first installed or when the keystore containing the public/private keypair
 * has been otherwise removed (manually by user or due to 3 failed password attempts)
 */
public class ActivityInitialise extends Activity implements Communicator {

    public static final String CERTIFICATE_FILE = "SiSCert.bks";
    public static final String KEYSTORE_NAME = "SiSKeyStore.bks";
    public static final String KEYSTORE_TYPE = "BKS";
    private static final int CHOOSE_BACKUP = 9999;

    private Button mButtonContinue;
    private FragmentManager fm = getFragmentManager();
    private String appPwd;
    private String importPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialise);
        mButtonContinue = (Button) findViewById(R.id.button_continue);
    }

    /**
     * Shows the AlertDialog to ask whether user wants to import a keystore from backup
     * rather than creating a new one
     * This is th onClick method fr the "Continue" button on the initial screen
     * Response is handled in alertDialogResponse
     */
    public void showKeystoreDialog(View view) {
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.init_keystore, R.string.init_restore);
        newFragment.show(getFragmentManager(), "dialog");
        mButtonContinue.setVisibility(View.GONE);
    }

    /**
     * Handles the response from the AlertDialogFragment
     *
     * @param titleInt    - indicates which instance of the Alert Dialog Fragment is responding
     * @param whichButton - indicates which button has been pressed
     */
    @Override
    public void alertDialogResponse(int titleInt, int whichButton) {
        switch (titleInt) {
            case R.string.init_keystore:
                // this is a response from the Import Keystore dialog
                if (whichButton == Communicator.POS_CLICK) {
                    Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getContentIntent.setType("file/*");
                    startActivityForResult(getContentIntent, CHOOSE_BACKUP);
                } else {
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
                    finish();
                } else finish();   // user has chosen not to proceed;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_BACKUP) {
            if (resultCode == Activity.RESULT_OK) {
                importPath = data.getData().getPath();
                //show dialog to set up a master password
                FragmentDialogPwd dFragment = new FragmentDialogPwd();
                dFragment.show(fm, "Dialog Fragment Pwd");
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDialogResponse(String data) {
        //this is from the password dialog and returns the chosen master password
        appPwd = data;
        try {
            createKeyStore(KEYSTORE_NAME);
//         createKeyStore(CERTIFICATE_FILE);
            importKeyStore(CERTIFICATE_FILE);
            showConfirmDialog();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        /*
         *NOTE: From API 18 it s possible to store this information in the system keystore
         *      and hence we would add functionality to generate the keys programmatically.
         *      For demonstration purposes and since we are working with an API 17 device,
         *      we will need to generate the private key/certificate pair using java sdk Keytool
         *      and transfer manually - hence we will always select the option above to import
         *      the keystore
         */
    }

    @Override
    public void onDbxFileSelected(DbxFileInfo mDbxFileInfo) {
        //not used
    }

    public void showConfirmDialog() {
        DialogFragment newFragment = FragmentAlertDialog
                .newInstance(R.string.init_confirm, R.string.confirm_msg);
        newFragment.show(getFragmentManager(), "dialog2");
    }

    public void createKeyStore(String name) throws IOException, GeneralSecurityException {
        KeyStore newKS = KeyStore.getInstance(KEYSTORE_TYPE);

        //FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);
        //this has been moved to external storage for testing and demonstration purposes
        File mFile = new File(getExternalFilesDir(null), name);
        FileOutputStream fos = new FileOutputStream(mFile);
        newKS.load(null);
        newKS.store(fos, appPwd.toCharArray());
        fos.close();
    }

    public void importKeyStore(String name) throws IOException, GeneralSecurityException {
        KeyStore newKS = KeyStore.getInstance(KEYSTORE_TYPE);
        File outFile = new File(getExternalFilesDir(null), name);
        File inFile = new File(importPath);
        FileOutputStream fos = new FileOutputStream(outFile);
        FileInputStream fis = new FileInputStream(inFile);
        newKS.load(fis, appPwd.toCharArray());
        newKS.store(fos, appPwd.toCharArray());
        fos.close();
    }
}



