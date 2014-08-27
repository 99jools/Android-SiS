package com.example.julie.securelyshare;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ActivityAdmin extends ActivityMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void onClickCreate(View view) {
        //get the groupID
        EditText getGroupID = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroupID.getText().toString().trim();
        //check that filename isn't empty
        if (groupID.length() < 1) {
            getGroupID.setError("Group name can't be blank");
        } else {
            try {
                new AppKeystore().addGroupKey(groupID);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeystoreAccessException e) {
                e.printStackTrace();
            }

            finish();
        }
    }

    public void onClickDelete(View view) {
        //get the groupID
        EditText getGroupID = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroupID.getText().toString().trim();
        //check that filename isn't empty
        if (groupID.length() < 1) {
            getGroupID.setError("Group name can't be blank");
        } else {


            try {
                new AppKeystore().deleteGroupKey(groupID);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeystoreAccessException e) {
                e.printStackTrace();
            }

            finish();
        }
    }
}
