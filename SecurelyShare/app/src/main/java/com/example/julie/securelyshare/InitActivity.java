package com.example.julie.securelyshare;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * InitActivity is only called when the app is first installed or when the keystore containing the public/private keypair
 * has been otherwise removed (manually by user or due to 3 failed password attempts)
 */
public class InitActivity extends Activity {
    Button mButtonConfirm;
    Button mButtonCancel;
    EditText mEditPwd;
    EditText mEditPwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        mButtonConfirm = (Button) findViewById(R.id.init_confirm);
        mButtonCancel = (Button) findViewById(R.id.init_cancel);
        mEditPwd = (EditText) findViewById(R.id.init_pwd);
        mEditPwd2 = (EditText) findViewById(R.id.init_pwd2);

        //get password
         LeftFragment dropbox = new LeftFragment();
         FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.add(R.id.init_container, dropbox);
        transaction.commit();
    }

        //create certificate KeyStore

        //Export certificate

        //create app keystore for group encryption keys

        // confirm to user with alert dialog that keystore password is
        //not recoverable

        // offer alternative to import certificate keystore - confirm that correct format and
        //can be accessed with supplied password
        //

}



