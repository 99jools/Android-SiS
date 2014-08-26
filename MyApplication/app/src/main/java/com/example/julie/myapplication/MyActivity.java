package com.example.julie.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class MyActivity extends Activity implements Communicator {
    char[] password = "test".toCharArray();
    String appPwd = null;
    private FragmentManager fm = getFragmentManager();
    boolean pwdValid = false;
    private int tries = 0;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        file = new File(getExternalFilesDir(null), "SisKeyStore.ks");
        try {
            KeyStore testks = KeyStore.getInstance("BKS");
            FileOutputStream fos = new FileOutputStream(file);
            //create keystore
            testks.load(null);
            testks.store(fos, password);
            fos.close();
            Log.e("kreated keystore", "");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


        //now try and read it again
        if (appPwd == null) {

            // get password from the user and set in AppPwdObj
            FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
            dFragment.show(fm, "Dialog Fragment Unlock");
        }

    }

    @Override
    public void alertDialogResponse(int title, int whichButton) {
    }

    @Override
    public void onDialogResponse(String data) {
        Log.e("at start of dialog response", pwdValid+"");
        appPwd = data;
        KeyStore testks = null;
        try {
            testks = KeyStore.getInstance("BKS");
            FileInputStream fis = new FileInputStream(file);
            testks.load(fis, appPwd.toCharArray());
            pwdValid = true;
            Log.e("accessed", "");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.e("tries", "" + tries);
        //now check that this pwd provides access to the store
        if (!pwdValid) {
            if (tries < 2) showToast("The password entered is invalid - please retry");
            else {
                if (tries == 2)
                    showToast("Password invalid - ONE MORE FAILURE WILL RESULT IN KEYSTORE BEING WIPED");
                else {
                    showToast("Keystore wiped");
                    finish();
                }

            }
            tries++;
            FragmentDialogUnlock dFragment = new FragmentDialogUnlock();
            dFragment.show(fm, "Dialog Fragment Unlock");
        }
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }


    public void nextActivity(View view){
        Intent intent = new Intent();
        intent.setClass(this, TestActivity.class);
        startActivity(intent);
    }
}






