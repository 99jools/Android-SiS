package com.example.jrs300.shareinsecret;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.jrs300.shareinsecrettest.AppKeystore;
import com.example.jrs300.shareinsecrettest.MissingPwdException;
import com.example.jrs300.shareinsecrettest.R;
import com.example.jrs300.shareinsecrettest.SharedPrefs;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AddGroupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickCreate(View view) {
        //get the groupID
        EditText getGroupID = (EditText) findViewById(R.id.text_groupID);
        String groupID = getGroupID.getText().toString().trim();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPrefs prefs = new SharedPrefs(sp);
        //check that filename isn't empty
        if (groupID.length() < 1) {
            getGroupID.setError("Group name can't be blank");
        } else {


            try {
                AppKeystore.addGroupKey(groupID, prefs);
            } catch (MissingPwdException e) {
                Log.e("new group", e.getMessage());
            } catch (IOException e) {
                Log.e("new group", e.getMessage());
            } catch (GeneralSecurityException e) {
                Log.e("new group", e.getMessage());
            }
            finish();
        }
    }
}