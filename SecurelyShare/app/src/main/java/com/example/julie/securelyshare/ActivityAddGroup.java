package com.example.julie.securelyshare;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ActivityAddGroup extends ActivityMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
    }




    public void onClickCreate(View view) {
        //get the groupID
        Log.e("in add","");
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
            } catch (WrongPwdException e) {
                e.printStackTrace();
            }

            finish();
        }
    }
}
