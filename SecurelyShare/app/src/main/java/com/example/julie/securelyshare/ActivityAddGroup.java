package com.example.julie.securelyshare;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class ActivityAddGroup extends ActivityMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
    }




    public void onClickCreate(View view) {
        //get the groupID
        EditText getGroupID = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroupID.getText().toString().trim();
        //check that filename isn't empty
        if (groupID.length() < 1) {
            getGroupID.setError("Group name can't be blank");
        } else {


    //            new AppKeystore().importGroupKey(groupID,);

            finish();
        }
    }
}
